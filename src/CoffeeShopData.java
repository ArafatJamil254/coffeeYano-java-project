import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class CoffeeShopData {
    private Path folder;
    private ArrayList<CoffeeItem> menu = new ArrayList<>();
    private ArrayList<CoffeeOrder> orders = new ArrayList<>();
    private ArrayList<String[]> accounts = new ArrayList<>();
    private ArrayList<String[]> employees = new ArrayList<>();

    public CoffeeShopData() throws IOException {
        String customFolder = System.getProperty("coffeeyano.data.dir");
        if (customFolder == null || customFolder.trim().isEmpty()) {
            customFolder = System.getProperty("user.home") + "/.coffeeyano";
        }
        folder = Paths.get(customFolder);
        Files.createDirectories(folder);
        loadMenu();
        loadAccounts();
        loadOrders();
        loadEmployees();
    }

    public Path getDataFolder() { return folder; }
    public ArrayList<CoffeeItem> getMenu() { return new ArrayList<>(menu); }
    public ArrayList<CoffeeOrder> getOrders() { return new ArrayList<>(orders); }
    public ArrayList<String[]> getEmployees() { return new ArrayList<>(employees); }

    public ArrayList<String> getUsernames() {
        ArrayList<String> result = new ArrayList<>();
        for (String[] account : accounts) result.add(account[0]);
        return result;
    }

    private static void checkText(String value, String field) {
        if (value == null || value.trim().isEmpty())
            throw new IllegalArgumentException(field + " cannot be empty.");
        if (value.length() > 60 || value.contains("|") || value.contains("~") || value.contains("@") ||
                value.contains("\n") || value.contains("\r") || value.contains("\t"))
            throw new IllegalArgumentException(field + " contains unsupported characters or is too long.");
    }

    private void loadMenu() throws IOException {
        Path file = folder.resolve("menu.txt");
        if (!Files.exists(file)) {
            menu.add(new CoffeeItem(1, "Espresso", "Classic", 350, true));
            menu.add(new CoffeeItem(2, "Latte", "Milk Coffee", 250, true));
            menu.add(new CoffeeItem(3, "Cappuccino", "Milk Coffee", 250, true));
            menu.add(new CoffeeItem(4, "Americano", "Classic", 350, true));
            menu.add(new CoffeeItem(5, "Mocha", "Specialty", 350, true));
            menu.add(new CoffeeItem(6, "Flat White", "Milk Coffee", 250, true));
            menu.add(new CoffeeItem(7, "Cold Brew", "Iced Coffee", 250, true));
            menu.add(new CoffeeItem(8, "Macchiato", "Specialty", 350, true));
            saveMenu();
            return;
        }
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length != 5) throw new IOException("Invalid menu.txt entry: " + line);
            menu.add(new CoffeeItem(Integer.parseInt(p[0]), p[1], p[2], Integer.parseInt(p[3]), Boolean.parseBoolean(p[4])));
        }
    }

    private void saveMenu() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (CoffeeItem item : menu) {
            lines.add(item.getId() + "|" + item.getName() + "|" + item.getCategory() + "|" + item.getPrice() + "|" + item.isAvailable());
        }
        saveLines("menu.txt", lines);
    }

    public CoffeeItem getCoffee(int id) {
        for (CoffeeItem item : menu) if (item.getId() == id) return item;
        return null;
    }

    public void addCoffee(String name, String category, int price) throws IOException {
        checkText(name, "Coffee name");
        checkText(category, "Category");
        if (price < 1 || price > 100000) throw new IllegalArgumentException("Enter a valid price.");
        int nextId = 1;
        for (CoffeeItem item : menu) {
            if (item.getName().equalsIgnoreCase(name.trim())) throw new IllegalArgumentException("That coffee is already on the menu.");
            nextId = Math.max(nextId, item.getId() + 1);
        }
        menu.add(new CoffeeItem(nextId, name.trim(), category.trim(), price, true));
        try { saveMenu(); } catch (IOException e) { menu.remove(menu.size() - 1); throw e; }
    }

    public void updateCoffee(int id, String name, String category, int price, boolean available) throws IOException {
        checkText(name, "Coffee name");
        checkText(category, "Category");
        if (price < 1 || price > 100000) throw new IllegalArgumentException("Enter a valid price.");
        CoffeeItem item = getCoffee(id);
        if (item == null) throw new IllegalArgumentException("Select an existing coffee.");
        for (CoffeeItem other : menu) {
            if (other.getId() != id && other.getName().equalsIgnoreCase(name.trim()))
                throw new IllegalArgumentException("That coffee is already on the menu.");
        }
        String oldName = item.getName(), oldCategory = item.getCategory();
        int oldPrice = item.getPrice(); boolean oldAvailable = item.isAvailable();
        item.setName(name.trim()); item.setCategory(category.trim()); item.setPrice(price); item.setAvailable(available);
        try { saveMenu(); } catch (IOException e) {
            item.setName(oldName); item.setCategory(oldCategory); item.setPrice(oldPrice); item.setAvailable(oldAvailable);
            throw e;
        }
    }

    public void deleteCoffee(int id) throws IOException {
        CoffeeItem item = getCoffee(id);
        if (item == null) throw new IllegalArgumentException("Select an existing coffee.");
        int index = menu.indexOf(item);
        menu.remove(index);
        try { saveMenu(); } catch (IOException e) { menu.add(index, item); throw e; }
    }

    private void loadAccounts() throws IOException {
        Path file = folder.resolve("accounts.txt");
        if (!Files.exists(file)) return;
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length != 3) throw new IOException("Invalid accounts.txt format.");
            accounts.add(p);
        }
    }

    private static byte[] hexToBytes(String input) {
        if (input.length() % 2 != 0) throw new IllegalArgumentException("Invalid account data.");
        byte[] bytes = new byte[input.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int h = Character.digit(input.charAt(i * 2), 16);
            int l = Character.digit(input.charAt(i * 2 + 1), 16);
            if (h < 0 || l < 0) throw new IllegalArgumentException("Invalid account data.");
            bytes[i] = (byte) ((h << 4) | l);
        }
        return bytes;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder text = new StringBuilder();
        for (byte value : bytes) text.append(String.format("%02x", value & 255));
        return text.toString();
    }

    private static byte[] hashPassword(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, 120000, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Password hashing is not available in this Java installation.", e);
        } finally {
            spec.clearPassword();
        }
    }

    public void register(String username, char[] password) throws IOException {
        if (username == null || !username.matches("[A-Za-z0-9_.-]{3,24}"))
            throw new IllegalArgumentException("Username must be 3-24 letters, numbers, dots, underscores or hyphens.");
        if (password == null || password.length < 6 || password.length > 128)
            throw new IllegalArgumentException("Password must be between 6 and 128 characters.");
        for (String[] account : accounts) {
            if (account[0].equalsIgnoreCase(username)) throw new IllegalArgumentException("Username is already taken.");
        }
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        String[] account = { username, bytesToHex(salt), bytesToHex(hashPassword(password, salt)) };
        accounts.add(account);
        try { saveAccounts(); } catch (IOException e) { accounts.remove(accounts.size() - 1); throw e; }
    }

    public boolean login(String username, char[] password) {
        if (username == null || password == null) return false;
        for (String[] account : accounts) {
            if (account[0].equalsIgnoreCase(username.trim())) {
                byte[] actual = hashPassword(password, hexToBytes(account[1]));
                return MessageDigest.isEqual(actual, hexToBytes(account[2]));
            }
        }
        return false;
    }

    private void saveAccounts() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (String[] account : accounts) lines.add(String.join("|", account));
        saveLines("accounts.txt", lines);
    }

    private void loadOrders() throws IOException {
        Path file = folder.resolve("orders.txt");
        if (!Files.exists(file)) return;
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length != 5) throw new IOException("Invalid orders.txt format.");
            ArrayList<CoffeeOrder.Item> items = new ArrayList<>();
            for (String token : p[4].split("~")) {
                String[] part = token.split("@", -1);
                if (part.length != 3) throw new IOException("Invalid order items format.");
                items.add(new CoffeeOrder.Item(part[0], Integer.parseInt(part[1]), Integer.parseInt(part[2])));
            }
            orders.add(new CoffeeOrder(Integer.parseInt(p[0]), p[1], p[2], p[3], items));
        }
    }

    public CoffeeOrder placeOrder(String customer, String payment, ArrayList<CoffeeOrder.Item> items) throws IOException {
        checkText(customer, "Customer");
        if (!Arrays.asList("Cash on pickup", "Pay at counter", "Card demo").contains(payment))
            throw new IllegalArgumentException("Select a demo checkout method.");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Add something to your basket first.");
        int total = 0;
        for (CoffeeOrder.Item item : items) {
            if (item.getQuantity() < 1 || item.getQuantity() > 100 || item.getPrice() < 1)
                throw new IllegalArgumentException("Invalid item quantity or price.");
            checkText(item.getName(), "Item name");
            total += item.getSubtotal();
        }
        if (total <= 0) throw new IllegalArgumentException("Order total must be positive.");
        int nextId = 1001;
        for (CoffeeOrder order : orders) nextId = Math.max(nextId, order.getId() + 1);
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        CoffeeOrder order = new CoffeeOrder(nextId, date, customer.trim(), payment, items);
        orders.add(order);
        try { saveOrders(); } catch (IOException e) { orders.remove(orders.size() - 1); throw e; }
        return order;
    }

    private void saveOrders() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (CoffeeOrder order : orders) {
            StringBuilder itemText = new StringBuilder();
            for (CoffeeOrder.Item item : order.getItems()) {
                if (itemText.length() > 0) itemText.append("~");
                itemText.append(item.getName()).append("@").append(item.getPrice()).append("@").append(item.getQuantity());
            }
            lines.add(order.getId() + "|" + order.getDate() + "|" + order.getCustomer() + "|" +
                    order.getPaymentMethod() + "|" + itemText);
        }
        saveLines("orders.txt", lines);
    }

    public int getTotalSales() {
        int total = 0;
        for (CoffeeOrder order : orders) total += order.getTotal();
        return total;
    }

    private void loadEmployees() throws IOException {
        Path file = folder.resolve("team.txt");
        if (!Files.exists(file)) return;
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length != 2) throw new IOException("Invalid team.txt entry.");
            employees.add(p);
        }
    }

    public void addEmployee(String name, String role) throws IOException {
        checkText(name, "Name");
        checkText(role, "Role");
        String[] employee = {name.trim(), role.trim()};
        employees.add(employee);
        try { saveEmployees(); } catch (IOException e) { employees.remove(employees.size() - 1); throw e; }
    }

    public void removeEmployee(int index) throws IOException {
        if (index < 0 || index >= employees.size()) throw new IllegalArgumentException("Select a team member.");
        String[] employee = employees.remove(index);
        try { saveEmployees(); } catch (IOException e) { employees.add(index, employee); throw e; }
    }

    private void saveEmployees() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (String[] employee : employees) lines.add(employee[0] + "|" + employee[1]);
        saveLines("team.txt", lines);
    }

    private void saveLines(String fileName, List<String> lines) throws IOException {
        Path target = folder.resolve(fileName);
        Path temporary = folder.resolve(fileName + ".tmp");
        Files.write(temporary, lines, StandardCharsets.UTF_8);
        try {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
