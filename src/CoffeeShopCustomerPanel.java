import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CoffeeShopCustomerPanel extends JFrame {
    private CoffeeShopData data;
    private String customer;
    private boolean counterMode;
    private LinkedHashMap<Integer, Integer> cart = new LinkedHashMap<>();
    private ArrayList<Integer> cartIds = new ArrayList<>();
    private JPanel menuGrid;
    private JTextField searchField;
    private JComboBox<String> categoryBox;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel totalLabel, countLabel;

    public CoffeeShopCustomerPanel(CoffeeShopData data, String customer, boolean counterMode) {
        super("CoffeeYano | Menu");
        this.data = data;
        this.customer = customer;
        this.counterMode = counterMode;
        setSize(1260, 800);
        setMinimumSize(new Dimension(950, 620));
        setDefaultCloseOperation(counterMode ? JFrame.DISPOSE_ON_CLOSE : JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CafeStyle.CREAM);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildMain(), BorderLayout.CENTER);
        refreshMenu();
        refreshCart();
    }

    private JPanel buildHeader() {
        JPanel header = CafeStyle.panel(CafeStyle.ESPRESSO);
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(19, 29, 19, 29));
        JPanel brand = CafeStyle.panel(CafeStyle.ESPRESSO);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        JLabel shopName = CafeStyle.label("COFFEEYANO", 25, CafeStyle.HONEY, true);
        shopName.setFont(CafeStyle.titleFont(26));
        brand.add(shopName);
        brand.add(CafeStyle.label("FRESHLY BREWED  /  THE EVERYDAY MENU", 10, new Color(222, 190, 162), true));
        header.add(brand, BorderLayout.WEST);

        JPanel actions = CafeStyle.panel(CafeStyle.ESPRESSO);
        actions.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        actions.add(CafeStyle.label(counterMode ? "Counter order" : "Hello, " + customer, 13, CafeStyle.CREAM, true));
        JButton history = CafeStyle.button("My orders", CafeStyle.COFFEE, Color.WHITE);
        history.addActionListener(e -> showMyOrders());
        if (!counterMode && !customer.equals("Guest")) actions.add(history);
        JButton back = CafeStyle.button(counterMode ? "Close counter" : "Sign out", CafeStyle.HONEY, CafeStyle.ESPRESSO);
        back.addActionListener(e -> {
            dispose();
            if (!counterMode) new CoffeeShopLoginPage(data).setVisible(true);
        });
        actions.add(back);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMain() {
        JPanel content = CafeStyle.panel(CafeStyle.CREAM);
        content.setBorder(BorderFactory.createEmptyBorder(21, 27, 25, 27));
        content.setLayout(new BorderLayout(22, 0));

        JPanel browsing = CafeStyle.panel(CafeStyle.CREAM);
        browsing.setLayout(new BorderLayout(0, 15));
        JPanel top = CafeStyle.panel(CafeStyle.CREAM);
        top.setLayout(new BorderLayout(10, 13));
        JLabel title = CafeStyle.label("Pick your favorite cup", 28, CafeStyle.ESPRESSO, true);
        title.setFont(CafeStyle.titleFont(28));
        top.add(title, BorderLayout.NORTH);

        JPanel filters = CafeStyle.panel(CafeStyle.CREAM);
        filters.setLayout(new BorderLayout(10, 0));
        searchField = CafeStyle.input();
        searchField.setToolTipText("Search for coffee by name");
        JPanel searchArea = CafeStyle.panel(CafeStyle.CREAM);
        searchArea.setLayout(new BorderLayout(0, 5));
        searchArea.add(CafeStyle.label("SEARCH THE MENU", 10, CafeStyle.MUTED, true), BorderLayout.NORTH);
        searchArea.add(searchField, BorderLayout.CENTER);
        filters.add(searchArea, BorderLayout.CENTER);
        categoryBox = new JComboBox<>(new String[]{"All coffees", "Classic", "Milk Coffee", "Iced Coffee", "Specialty"});
        categoryBox.setBackground(CafeStyle.PAPER);
        categoryBox.setFont(CafeStyle.bodyFont(13));
        categoryBox.setPreferredSize(new Dimension(155, 40));
        filters.add(categoryBox, BorderLayout.EAST);
        top.add(filters, BorderLayout.SOUTH);
        browsing.add(top, BorderLayout.NORTH);

        menuGrid = CafeStyle.panel(CafeStyle.CREAM);
        menuGrid.setLayout(new GridLayout(0, 2, 15, 15));
        JScrollPane scroll = new JScrollPane(menuGrid);
        scroll.getViewport().setBackground(CafeStyle.CREAM);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        browsing.add(scroll, BorderLayout.CENTER);
        content.add(browsing, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshMenu(); }
            public void removeUpdate(DocumentEvent e) { refreshMenu(); }
            public void changedUpdate(DocumentEvent e) { refreshMenu(); }
        });
        categoryBox.addActionListener(e -> refreshMenu());
        content.add(buildCart(), BorderLayout.EAST);
        return content;
    }

    private JPanel buildCart() {
        JPanel side = CafeStyle.card();
        side.setPreferredSize(new Dimension(333, 0));
        side.setLayout(new BorderLayout(0, 15));

        JPanel top = CafeStyle.panel(CafeStyle.PAPER);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel title = CafeStyle.label("Your basket", 25, CafeStyle.ESPRESSO, true);
        title.setFont(CafeStyle.titleFont(25));
        top.add(title);
        top.add(Box.createVerticalStrut(6));
        countLabel = CafeStyle.label("0 items selected", 12, CafeStyle.MUTED, false);
        top.add(countLabel);
        side.add(top, BorderLayout.NORTH);

        cartModel = new DefaultTableModel(new Object[]{"Coffee", "Qty", "Tk"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartModel);
        CafeStyle.table(cartTable);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.getColumnModel().getColumn(1).setMaxWidth(52);
        cartTable.getColumnModel().getColumn(2).setMaxWidth(85);
        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.setBorder(BorderFactory.createLineBorder(CafeStyle.LINE));
        side.add(scroll, BorderLayout.CENTER);

        JPanel bottom = CafeStyle.panel(CafeStyle.PAPER);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        JPanel changes = CafeStyle.panel(CafeStyle.PAPER);
        changes.setLayout(new GridLayout(1, 3, 7, 0));
        JButton plus = CafeStyle.button("+ 1", CafeStyle.SAND, CafeStyle.COFFEE);
        JButton minus = CafeStyle.button("- 1", CafeStyle.SAND, CafeStyle.COFFEE);
        JButton remove = CafeStyle.button("Remove", CafeStyle.CREAM, CafeStyle.RUST);
        plus.addActionListener(e -> changeQuantity(1));
        minus.addActionListener(e -> changeQuantity(-1));
        remove.addActionListener(e -> removeSelected());
        changes.add(minus); changes.add(plus); changes.add(remove);
        bottom.add(changes);
        bottom.add(Box.createVerticalStrut(18));
        JPanel amount = CafeStyle.panel(CafeStyle.PAPER);
        amount.setLayout(new BorderLayout());
        amount.add(CafeStyle.label("ORDER TOTAL", 12, CafeStyle.MUTED, true), BorderLayout.WEST);
        totalLabel = CafeStyle.label("Tk 0", 22, CafeStyle.ESPRESSO, true);
        amount.add(totalLabel, BorderLayout.EAST);
        bottom.add(amount);
        bottom.add(Box.createVerticalStrut(18));
        JButton checkout = CafeStyle.button("Continue to checkout  →", CafeStyle.RUST, Color.WHITE);
        checkout.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 49));
        checkout.addActionListener(e -> checkout());
        JPanel checkoutRow = CafeStyle.panel(CafeStyle.PAPER);
        checkoutRow.setLayout(new BorderLayout());
        checkoutRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        checkoutRow.add(checkout, BorderLayout.CENTER);
        bottom.add(checkoutRow);
        bottom.add(Box.createVerticalStrut(10));
        JLabel note = CafeStyle.label("Demo only · No real payments", 10, CafeStyle.MUTED, false);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottom.add(note);
        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }

    private void refreshMenu() {
        if (menuGrid == null || searchField == null || categoryBox == null) return;
        menuGrid.removeAll();
        String search = searchField.getText().trim().toLowerCase();
        String category = (String) categoryBox.getSelectedItem();
        int visible = 0;
        for (CoffeeItem item : data.getMenu()) {
            if (!item.isAvailable()) continue;
            if (!item.getName().toLowerCase().contains(search)) continue;
            if (!"All coffees".equals(category) && !item.getCategory().equals(category)) continue;
            menuGrid.add(createCoffeeCard(item));
            visible++;
        }
        if (visible == 0) {
            JPanel empty = CafeStyle.card();
            empty.add(CafeStyle.label("No drinks found. Try another search.", 13, CafeStyle.MUTED, false));
            menuGrid.add(empty);
        }
        menuGrid.revalidate();
        menuGrid.repaint();
    }

    private JPanel createCoffeeCard(CoffeeItem item) {
        JPanel card = CafeStyle.card();
        card.setLayout(new BorderLayout(0, 8));
        Color[] shades = {
                new Color(141, 102, 81), new Color(174, 110, 85), new Color(117, 136, 107),
                new Color(166, 130, 94), new Color(144, 106, 121)
        };
        CoffeeArt picture = new CoffeeArt(shades[item.getId() % shades.length], item.getId());
        picture.setPreferredSize(new Dimension(200, 125));
        card.add(picture, BorderLayout.NORTH);

        JPanel words = CafeStyle.panel(CafeStyle.PAPER);
        words.setLayout(new BorderLayout(4, 5));
        JLabel name = CafeStyle.label(item.getName(), 18, CafeStyle.ESPRESSO, true);
        name.setFont(CafeStyle.titleFont(20));
        words.add(name, BorderLayout.NORTH);
        words.add(CafeStyle.label(item.getCategory() + "  ·  Tk " + item.getPrice(), 12, CafeStyle.MUTED, false), BorderLayout.CENTER);
        card.add(words, BorderLayout.CENTER);

        JButton add = CafeStyle.button("Add to basket   +", CafeStyle.SAND, CafeStyle.COFFEE);
        add.addActionListener(e -> addToCart(item.getId()));
        card.add(add, BorderLayout.SOUTH);
        return card;
    }

    private void addToCart(int coffeeId) {
        int oldCount = cart.containsKey(coffeeId) ? cart.get(coffeeId) : 0;
        if (oldCount >= 100) {
            JOptionPane.showMessageDialog(this, "You can add up to 100 of a coffee to one order.");
            return;
        }
        cart.put(coffeeId, oldCount + 1);
        refreshCart();
    }

    private void changeQuantity(int change) {
        int row = cartTable.getSelectedRow();
        if (row < 0 || row >= cartIds.size()) {
            JOptionPane.showMessageDialog(this, "Select a coffee from your basket first.");
            return;
        }
        int id = cartIds.get(row);
        int next = cart.get(id) + change;
        if (next <= 0) cart.remove(id);
        else if (next <= 100) cart.put(id, next);
        refreshCart();
    }

    private void removeSelected() {
        int row = cartTable.getSelectedRow();
        if (row < 0 || row >= cartIds.size()) return;
        cart.remove(cartIds.get(row));
        refreshCart();
    }

    private ArrayList<CoffeeOrder.Item> getBasketItems() {
        ArrayList<CoffeeOrder.Item> items = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            CoffeeItem coffee = data.getCoffee(entry.getKey());
            if (coffee != null && coffee.isAvailable())
                items.add(new CoffeeOrder.Item(coffee.getName(), coffee.getPrice(), entry.getValue()));
        }
        return items;
    }

    private int getCartTotal() {
        int total = 0;
        for (CoffeeOrder.Item item : getBasketItems()) total += item.getSubtotal();
        return total;
    }

    private void refreshCart() {
        if (cartModel == null) return;
        cartModel.setRowCount(0);
        cartIds.clear();
        int qty = 0;
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            CoffeeItem coffee = data.getCoffee(entry.getKey());
            if (coffee == null || !coffee.isAvailable()) continue;
            cartIds.add(entry.getKey());
            int count = entry.getValue();
            qty += count;
            cartModel.addRow(new Object[]{coffee.getName(), count, count * coffee.getPrice()});
        }
        totalLabel.setText("Tk " + getCartTotal());
        countLabel.setText(qty + (qty == 1 ? " item selected" : " items selected"));
    }

    private void checkout() {
        ArrayList<CoffeeOrder.Item> items = getBasketItems();
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your basket is still empty.");
            return;
        }
        PaymentGatewayGUI dialog = new PaymentGatewayGUI(this, getCartTotal());
        String method = dialog.confirmCheckout();
        if (method == null) return;
        try {
            CoffeeOrder order = data.placeOrder(customer, method, items);
            cart.clear();
            refreshCart();
            JOptionPane.showMessageDialog(this, "Demo order #" + order.getId() + " placed successfully!");
            new ReceiptFrame(order).setVisible(true);
        } catch (IOException | IllegalArgumentException ex) {
            CafeStyle.error(this, ex);
        }
    }

    private void showMyOrders() {
        StringBuilder history = new StringBuilder();
        for (CoffeeOrder order : data.getOrders()) {
            if (order.getCustomer().equalsIgnoreCase(customer)) {
                history.append("#").append(order.getId()).append("  •  ");
                history.append(order.getDate()).append("  •  Tk ").append(order.getTotal()).append("\n");
            }
        }
        if (history.length() == 0) history.append("No orders yet. Time for coffee?");
        JTextArea text = new JTextArea(history.toString());
        text.setFont(CafeStyle.bodyFont(13));
        text.setRows(11);
        text.setColumns(42);
        text.setEditable(false);
        text.setBackground(CafeStyle.PAPER);
        text.setMargin(new Insets(12, 12, 12, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(text), "Your demo orders", JOptionPane.INFORMATION_MESSAGE);
    }
}
