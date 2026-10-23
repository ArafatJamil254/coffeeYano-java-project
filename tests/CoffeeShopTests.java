import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;

public class CoffeeShopTests {
    private static int checks = 0;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static void expectError(Runnable action, String message) {
        boolean failed = false;
        try { action.run(); } catch (IllegalArgumentException e) { failed = true; }
        check(failed, message);
    }

    public static void main(String[] args) throws Exception {
        Path folder = Files.createTempDirectory("coffeeyano-tests-");
        System.setProperty("coffeeyano.data.dir", folder.toString());
        CoffeeShopData data = new CoffeeShopData();

        check(data.getMenu().size() == 8, "Eight starting menu items");
        check(data.getCoffee(1).getName().equals("Espresso"), "Original espresso");
        check(data.getCoffee(1).getPrice() == 350, "Original espresso price");
        check(data.getCoffee(2).getPrice() == 250, "Original latte price");
        check(data.getCoffee(8).getPrice() == 350, "Original macchiato price");
        check(data.getTotalSales() == 0, "Starts with no orders");
        check(data.getUsernames().isEmpty(), "Starts with no accounts");
        check(data.getEmployees().isEmpty(), "Starts with no employees");

        data.addCoffee("Honey Latte", "Specialty", 390);
        check(data.getMenu().size() == 9, "Add coffee");
        check(data.getCoffee(9).getPrice() == 390, "Added coffee price");
        expectError(() -> {
            try { data.addCoffee("honey latte", "Classic", 200); }
            catch (java.io.IOException e) { throw new RuntimeException(e); }
        }, "Duplicate coffee rejected");
        expectError(() -> {
            try { data.addCoffee("Bad|Name", "Classic", 120); }
            catch (java.io.IOException e) { throw new RuntimeException(e); }
        }, "Delimiter injection rejected");
        expectError(() -> {
            try { data.addCoffee("Zero", "Classic", 0); }
            catch (java.io.IOException e) { throw new RuntimeException(e); }
        }, "Zero price rejected");
        data.updateCoffee(9, "Honey Latte", "Milk Coffee", 400, false);
        check(!data.getCoffee(9).isAvailable(), "Pause item");
        check(data.getCoffee(9).getPrice() == 400, "Update price");
        data.deleteCoffee(9);
        check(data.getMenu().size() == 8, "Delete item");
        check(data.getCoffee(9) == null, "Deleted item absent");

        char[] password = "local-demo-123".toCharArray();
        data.register("maya_01", password);
        check(data.getUsernames().size() == 1, "Registration adds an account");
        check(data.login("maya_01", password), "Correct password signs in");
        check(data.login("MAYA_01", password), "Username is case-insensitive");
        check(!data.login("maya_01", "wrong password".toCharArray()), "Incorrect password rejected");
        check(!data.login("unknown", password), "Unknown user rejected");
        String accountFile = new String(Files.readAllBytes(folder.resolve("accounts.txt")), StandardCharsets.UTF_8);
        check(!accountFile.contains("local-demo-123"), "No plaintext password saved");
        expectError(() -> {
            try { data.register("MAYA_01", password); }
            catch (java.io.IOException e) { throw new RuntimeException(e); }
        }, "Duplicate user rejected");
        expectError(() -> {
            try { data.register("x", password); }
            catch (java.io.IOException e) { throw new RuntimeException(e); }
        }, "Too-short username rejected");
        expectError(() -> {
            try { data.register("testuser", "123".toCharArray()); }
            catch (java.io.IOException e) { throw new RuntimeException(e); }
        }, "Too-short password rejected");

        ArrayList<CoffeeOrder.Item> basket = new ArrayList<>();
        basket.add(new CoffeeOrder.Item("Latte", 250, 2));
        basket.add(new CoffeeOrder.Item("Espresso", 350, 1));
        CoffeeOrder order = data.placeOrder("maya_01", "Cash on pickup", basket);
        check(order.getId() == 1001, "First order ID");
        check(order.getTotal() == 850, "Multiple quantities totaled correctly");
        check(order.getItems().size() == 2, "Order contains item snapshots");
        check(order.getReceipt().contains("Tk 850"), "Receipt shows total");
        check(order.getReceipt().contains("No real payment"), "Payment labeled as demo");
        check(data.getTotalSales() == 850, "Total sales aggregate");
        check(data.getOrders().size() == 1, "Order saved");
        expectError(() -> {
            try { data.placeOrder("Guest", "Cash on pickup", new ArrayList<CoffeeOrder.Item>()); }
            catch (java.io.IOException e) { throw new RuntimeException(e); }
        }, "Empty cart rejected");
        expectError(() -> {
            try { data.placeOrder("Guest", "Real card", basket); }
            catch (java.io.IOException e) { throw new RuntimeException(e); }
        }, "Undocumented payment method rejected");

        data.addEmployee("Nila Rahman", "Barista");
        data.addEmployee("Sami Ahmed", "Cashier");
        check(data.getEmployees().size() == 2, "Two employees added");
        data.removeEmployee(1);
        check(data.getEmployees().size() == 1, "Employee removed");
        check(data.getEmployees().get(0)[0].equals("Nila Rahman"), "Correct employee retained");

        CoffeeShopData loaded = new CoffeeShopData();
        check(loaded.getMenu().size() == 8, "Menu persists after restart");
        check(loaded.login("maya_01", password), "Credentials persist after restart");
        check(loaded.getOrders().size() == 1, "Orders persist after restart");
        check(loaded.getOrders().get(0).getTotal() == 850, "Order total persists");
        check(loaded.getOrders().get(0).getItems().get(0).getQuantity() == 2, "Item count persists");
        check(loaded.getEmployees().size() == 1, "Team persists after restart");
        CoffeeOrder second = loaded.placeOrder("Guest", "Pay at counter", basket);
        check(second.getId() == 1002, "Order number advances");
        check(loaded.getTotalSales() == 1700, "Sales total updated");
        check(!Files.exists(folder.resolve("user_data.txt")), "Legacy plaintext user file not generated");
        System.out.println("PASS - " + checks + " data and validation checks");
        System.out.println("Test data folder: " + folder);
    }
}
