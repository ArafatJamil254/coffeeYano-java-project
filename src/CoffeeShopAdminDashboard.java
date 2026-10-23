import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CoffeeShopAdminDashboard extends JFrame {
    private CoffeeShopData data;
    private JPanel mainPanel;
    private String selectedPage = "Overview";
    private JLabel currentSection;

    public CoffeeShopAdminDashboard(CoffeeShopData data) {
        super("CoffeeYano | Staff dashboard");
        this.data = data;
        setSize(1220, 780);
        setMinimumSize(new Dimension(960, 625));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);

        mainPanel = CafeStyle.panel(CafeStyle.CREAM);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(26, 27, 26, 27));
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        showPage("Overview");
    }

    private JPanel buildHeader() {
        JPanel header = CafeStyle.panel(CafeStyle.ESPRESSO);
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(16, 26, 16, 26));
        JLabel brand = CafeStyle.label("COFFEEYANO   /   BACK OFFICE", 17, CafeStyle.HONEY, true);
        brand.setFont(CafeStyle.titleFont(20));
        header.add(brand, BorderLayout.WEST);
        header.add(CafeStyle.label("STAFF DEMO  ·  LOCAL DATA", 11, new Color(223, 196, 174), true), BorderLayout.EAST);
        return header;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = CafeStyle.panel(CafeStyle.COFFEE);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 14, 20, 14));
        sidebar.setLayout(new BorderLayout());

        JPanel nav = CafeStyle.panel(CafeStyle.COFFEE);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        JLabel small = CafeStyle.label("WORKSPACE", 11, CafeStyle.HONEY, true);
        small.setAlignmentX(Component.LEFT_ALIGNMENT);
        nav.add(small);
        nav.add(Box.createVerticalStrut(19));
        for (String page : new String[]{"Overview", "Coffee Menu", "Orders", "Customers", "Team"}) {
            JButton button = CafeStyle.button(page + "   →", CafeStyle.COFFEE, Color.WHITE);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            button.addActionListener(e -> showPage(page));
            nav.add(button);
            nav.add(Box.createVerticalStrut(7));
        }
        nav.add(Box.createVerticalStrut(15));
        JButton counter = CafeStyle.button("+ Open counter", CafeStyle.HONEY, CafeStyle.ESPRESSO);
        counter.setAlignmentX(Component.LEFT_ALIGNMENT);
        counter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        counter.setHorizontalAlignment(SwingConstants.LEFT);
        counter.addActionListener(e -> new CoffeeShopCustomerPanel(data, "Counter", true).setVisible(true));
        nav.add(counter);
        sidebar.add(nav, BorderLayout.NORTH);

        JPanel bottom = CafeStyle.panel(CafeStyle.COFFEE);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.add(CafeStyle.label("A JAVA SWING PROJECT", 10, new Color(232, 201, 180), true));
        bottom.add(Box.createVerticalStrut(10));
        JButton signOut = CafeStyle.button("← Sign out", CafeStyle.SAGE, Color.WHITE);
        signOut.setAlignmentX(Component.LEFT_ALIGNMENT);
        signOut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        signOut.addActionListener(e -> {
            dispose();
            new CoffeeShopLoginPage(data).setVisible(true);
        });
        bottom.add(signOut);
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private void showPage(String page) {
        selectedPage = page;
        if (mainPanel == null) return;
        mainPanel.removeAll();
        JPanel content;
        switch (page) {
            case "Coffee Menu": content = buildMenuPage(); break;
            case "Orders": content = buildOrdersPage(); break;
            case "Customers": content = buildCustomerPage(); break;
            case "Team": content = buildTeamPage(); break;
            default: content = buildOverviewPage();
        }
        mainPanel.add(content, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel page(String title, String subtitle) {
        JPanel body = CafeStyle.panel(CafeStyle.CREAM);
        body.setLayout(new BorderLayout(0, 18));
        JPanel headings = CafeStyle.panel(CafeStyle.CREAM);
        headings.setLayout(new BoxLayout(headings, BoxLayout.Y_AXIS));
        JLabel heading = CafeStyle.label(title, 30, CafeStyle.ESPRESSO, true);
        heading.setFont(CafeStyle.titleFont(30));
        headings.add(heading);
        headings.add(Box.createVerticalStrut(6));
        headings.add(CafeStyle.label(subtitle, 13, CafeStyle.MUTED, false));
        body.add(headings, BorderLayout.NORTH);
        return body;
    }

    private JPanel buildOverviewPage() {
        JPanel body = page("The daily grind", "A simple overview of your local demo coffee shop.");
        JPanel contents = CafeStyle.panel(CafeStyle.CREAM);
        contents.setLayout(new BorderLayout(0, 18));

        JPanel metrics = CafeStyle.panel(CafeStyle.CREAM);
        metrics.setLayout(new GridLayout(1, 3, 13, 0));
        metrics.add(metricCard("COFFEES ON MENU", String.valueOf(data.getMenu().size()), "Customizable menu"));
        metrics.add(metricCard("ORDERS RECORDED", String.valueOf(data.getOrders().size()), "Saved demo receipts"));
        metrics.add(metricCard("ORDER TOTALS", "Tk " + data.getTotalSales(), "Simulation only"));
        contents.add(metrics, BorderLayout.NORTH);

        JPanel lower = CafeStyle.panel(CafeStyle.CREAM);
        lower.setLayout(new BorderLayout(16, 0));
        JPanel ordersPanel = CafeStyle.card();
        ordersPanel.setLayout(new BorderLayout(0, 12));
        JLabel ordersLabel = CafeStyle.label("Recent orders", 20, CafeStyle.ESPRESSO, true);
        ordersLabel.setFont(CafeStyle.titleFont(23));
        ordersPanel.add(ordersLabel, BorderLayout.NORTH);
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"#", "Customer", "Items", "Tk"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ArrayList<CoffeeOrder> orders = data.getOrders();
        for (int i = orders.size() - 1; i >= 0 && i >= orders.size() - 8; i--) {
            CoffeeOrder order = orders.get(i);
            tableModel.addRow(new Object[]{order.getId(), order.getCustomer(), order.getItemSummary(), order.getTotal()});
        }
        JTable table = new JTable(tableModel);
        CafeStyle.table(table);
        ordersPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        lower.add(ordersPanel, BorderLayout.CENTER);

        JPanel right = CafeStyle.card();
        right.setPreferredSize(new Dimension(270, 200));
        right.setLayout(new BorderLayout(0, 10));
        right.add(new CoffeeArt(CafeStyle.SAGE, 5), BorderLayout.CENTER);
        JPanel actions = CafeStyle.panel(CafeStyle.PAPER);
        actions.setLayout(new GridLayout(2, 1, 0, 8));
        JButton counter = CafeStyle.button("New counter order  →", CafeStyle.RUST, Color.WHITE);
        counter.addActionListener(e -> new CoffeeShopCustomerPanel(data, "Counter", true).setVisible(true));
        JButton menu = CafeStyle.button("Edit the coffee menu", CafeStyle.SAND, CafeStyle.COFFEE);
        menu.addActionListener(e -> showPage("Coffee Menu"));
        actions.add(counter); actions.add(menu);
        right.add(actions, BorderLayout.SOUTH);
        lower.add(right, BorderLayout.EAST);
        contents.add(lower, BorderLayout.CENTER);
        body.add(contents, BorderLayout.CENTER);
        return body;
    }

    private JPanel metricCard(String caption, String value, String note) {
        JPanel panel = CafeStyle.card();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(CafeStyle.label(caption, 11, CafeStyle.RUST, true));
        panel.add(Box.createVerticalStrut(10));
        JLabel figure = new JLabel(value);
        figure.setFont(CafeStyle.titleFont(30));
        figure.setForeground(CafeStyle.ESPRESSO);
        panel.add(figure);
        panel.add(Box.createVerticalStrut(6));
        panel.add(CafeStyle.label(note, 11, CafeStyle.MUTED, false));
        return panel;
    }

    private JPanel buildMenuPage() {
        JPanel body = page("Coffee menu", "Add, edit, or pause drinks. Changes are saved locally.");
        JPanel content = CafeStyle.panel(CafeStyle.CREAM);
        content.setLayout(new BorderLayout(15, 0));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Coffee", "Category", "Price (Tk)", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (CoffeeItem item : data.getMenu())
            model.addRow(new Object[]{item.getId(), item.getName(), item.getCategory(), item.getPrice(), item.isAvailable() ? "Available" : "Paused"});
        JTable table = new JTable(model);
        CafeStyle.table(table);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel editor = CafeStyle.card();
        editor.setPreferredSize(new Dimension(300, 0));
        editor.setLayout(new BorderLayout(0, 15));
        JPanel fields = CafeStyle.panel(CafeStyle.PAPER);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        JLabel heading = CafeStyle.label("Edit a coffee", 23, CafeStyle.ESPRESSO, true);
        heading.setFont(CafeStyle.titleFont(23));
        fields.add(heading);
        fields.add(Box.createVerticalStrut(12));
        JTextField nameField = CafeStyle.input();
        JTextField priceField = CafeStyle.input();
        JComboBox<String> categoryField = new JComboBox<>(new String[]{"Classic", "Milk Coffee", "Iced Coffee", "Specialty"});
        categoryField.setFont(CafeStyle.bodyFont(13));
        categoryField.setBackground(CafeStyle.PAPER);
        JCheckBox availableBox = new JCheckBox("Available on menu", true);
        availableBox.setBackground(CafeStyle.PAPER);
        availableBox.setForeground(CafeStyle.ESPRESSO);
        addField(fields, "COFFEE NAME", nameField);
        addField(fields, "CATEGORY", categoryField);
        addField(fields, "PRICE (TK)", priceField);
        fields.add(Box.createVerticalStrut(13));
        fields.add(availableBox);
        fields.add(Box.createVerticalStrut(13));
        fields.add(CafeStyle.label("Select a row to edit it.", 12, CafeStyle.MUTED, false));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || table.getSelectedRow() < 0) return;
            int row = table.convertRowIndexToModel(table.getSelectedRow());
            nameField.setText(String.valueOf(model.getValueAt(row, 1)));
            categoryField.setSelectedItem(String.valueOf(model.getValueAt(row, 2)));
            priceField.setText(String.valueOf(model.getValueAt(row, 3)));
            availableBox.setSelected("Available".equals(model.getValueAt(row, 4)));
        });

        JPanel buttons = CafeStyle.panel(CafeStyle.PAPER);
        buttons.setLayout(new GridLayout(4, 1, 0, 9));
        JButton add = CafeStyle.button("+ Add new coffee", CafeStyle.RUST, Color.WHITE);
        JButton update = CafeStyle.button("Save selected", CafeStyle.SAGE, Color.WHITE);
        JButton delete = CafeStyle.button("Remove selected", CafeStyle.SAND, CafeStyle.RUST);
        JButton clear = CafeStyle.button("Clear form", CafeStyle.CREAM, CafeStyle.COFFEE);
        add.addActionListener(e -> {
            try {
                data.addCoffee(nameField.getText(), String.valueOf(categoryField.getSelectedItem()), Integer.parseInt(priceField.getText().trim()));
                showPage("Coffee Menu");
            } catch (Exception ex) { CafeStyle.error(this, ex); }
        });
        update.addActionListener(e -> {
            try {
                int id = selectedCoffeeId(table, model);
                data.updateCoffee(id, nameField.getText(), String.valueOf(categoryField.getSelectedItem()),
                        Integer.parseInt(priceField.getText().trim()), availableBox.isSelected());
                showPage("Coffee Menu");
            } catch (Exception ex) { CafeStyle.error(this, ex); }
        });
        delete.addActionListener(e -> {
            try {
                int id = selectedCoffeeId(table, model);
                if (JOptionPane.showConfirmDialog(this, "Remove this coffee from the menu?", "Confirm",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    data.deleteCoffee(id);
                    showPage("Coffee Menu");
                }
            } catch (Exception ex) { CafeStyle.error(this, ex); }
        });
        clear.addActionListener(e -> {
            table.clearSelection(); nameField.setText(""); priceField.setText(""); availableBox.setSelected(true);
        });
        buttons.add(add); buttons.add(update); buttons.add(delete); buttons.add(clear);
        JPanel formContents = CafeStyle.panel(CafeStyle.PAPER);
        formContents.setLayout(new BoxLayout(formContents, BoxLayout.Y_AXIS));
        formContents.setPreferredSize(new Dimension(240, 530));
        fields.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContents.add(fields);
        formContents.add(Box.createVerticalStrut(14));
        formContents.add(buttons);
        JScrollPane editorScroll = new JScrollPane(formContents);
        editorScroll.setBorder(null);
        editorScroll.getViewport().setBackground(CafeStyle.PAPER);
        editorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        editorScroll.getVerticalScrollBar().setUnitIncrement(14);
        editor.add(editorScroll, BorderLayout.CENTER);
        content.add(editor, BorderLayout.EAST);
        body.add(content, BorderLayout.CENTER);
        return body;
    }

    private int selectedCoffeeId(JTable table, DefaultTableModel model) {
        if (table.getSelectedRow() < 0) throw new IllegalArgumentException("Select a coffee from the table first.");
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        return (Integer) model.getValueAt(row, 0);
    }

    private void addField(JPanel form, String title, JComponent input) {
        form.add(Box.createVerticalStrut(12));
        form.add(CafeStyle.label(title, 11, CafeStyle.COFFEE, true));
        form.add(Box.createVerticalStrut(6));
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        form.add(input);
    }

    private JPanel buildOrdersPage() {
        JPanel body = page("Orders & receipts", "Completed demo orders, stored on this computer.");
        JPanel section = CafeStyle.card();
        section.setLayout(new BorderLayout(0, 13));
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Order #", "Placed on", "Customer", "Checkout", "Items", "Total (Tk)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ArrayList<CoffeeOrder> orders = data.getOrders();
        for (int i = orders.size() - 1; i >= 0; i--) {
            CoffeeOrder order = orders.get(i);
            model.addRow(new Object[]{order.getId(), order.getDate(), order.getCustomer(), order.getPaymentMethod(), order.getItemSummary(), order.getTotal()});
        }
        JTable table = new JTable(model);
        CafeStyle.table(table);
        table.setAutoCreateRowSorter(true);
        section.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel controls = CafeStyle.panel(CafeStyle.PAPER);
        controls.setLayout(new BorderLayout(12, 0));
        JTextField search = CafeStyle.input();
        search.setToolTipText("Search orders by date, customer or method");
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        search.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(search.getText()))); }
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });
        controls.add(search, BorderLayout.CENTER);
        JButton receipt = CafeStyle.button("View receipt", CafeStyle.RUST, Color.WHITE);
        receipt.addActionListener(e -> {
            if (table.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "Select an order first.");
                return;
            }
            int row = table.convertRowIndexToModel(table.getSelectedRow());
            int id = (Integer) model.getValueAt(row, 0);
            for (CoffeeOrder order : data.getOrders()) {
                if (order.getId() == id) new ReceiptFrame(order).setVisible(true);
            }
        });
        controls.add(receipt, BorderLayout.EAST);
        section.add(controls, BorderLayout.NORTH);
        body.add(section, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildCustomerPage() {
        JPanel body = page("Local customers", "Registered demo usernames (passwords are never displayed).");
        JPanel section = CafeStyle.card();
        section.setLayout(new BorderLayout(0, 15));
        DefaultTableModel model = new DefaultTableModel(new Object[]{"#", "Username"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        int index = 1;
        for (String username : data.getUsernames()) model.addRow(new Object[]{index++, username});
        JTable table = new JTable(model);
        CafeStyle.table(table);
        section.add(CafeStyle.label("CUSTOMER ACCOUNTS  ·  " + data.getUsernames().size(), 12, CafeStyle.RUST, true), BorderLayout.NORTH);
        section.add(new JScrollPane(table), BorderLayout.CENTER);
        body.add(section, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildTeamPage() {
        JPanel body = page("The crew", "A small employee directory for the cafe demo.");
        JPanel content = CafeStyle.panel(CafeStyle.CREAM);
        content.setLayout(new BorderLayout(15, 0));
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Name", "Role"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String[] employee : data.getEmployees()) model.addRow(new Object[]{employee[0], employee[1]});
        JTable table = new JTable(model);
        CafeStyle.table(table);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = CafeStyle.card();
        form.setPreferredSize(new Dimension(300, 0));
        form.setLayout(new BorderLayout(0, 15));
        JPanel fields = CafeStyle.panel(CafeStyle.PAPER);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        JLabel heading = CafeStyle.label("Add a teammate", 22, CafeStyle.ESPRESSO, true);
        heading.setFont(CafeStyle.titleFont(23));
        fields.add(heading);
        JTextField name = CafeStyle.input();
        JComboBox<String> role = new JComboBox<>(new String[]{"Barista", "Cashier", "Manager", "Service"});
        role.setFont(CafeStyle.bodyFont(13));
        role.setBackground(CafeStyle.PAPER);
        addField(fields, "NAME", name);
        addField(fields, "ROLE", role);
        form.add(fields, BorderLayout.NORTH);
        JPanel buttons = CafeStyle.panel(CafeStyle.PAPER);
        buttons.setLayout(new GridLayout(2, 1, 0, 10));
        JButton add = CafeStyle.button("+ Add team member", CafeStyle.RUST, Color.WHITE);
        JButton remove = CafeStyle.button("Remove selected", CafeStyle.SAND, CafeStyle.RUST);
        add.addActionListener(e -> {
            try {
                data.addEmployee(name.getText(), String.valueOf(role.getSelectedItem()));
                showPage("Team");
            } catch (Exception ex) { CafeStyle.error(this, ex); }
        });
        remove.addActionListener(e -> {
            try {
                if (table.getSelectedRow() < 0) throw new IllegalArgumentException("Select a team member first.");
                if (JOptionPane.showConfirmDialog(this, "Remove this team member?", "Confirm",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    data.removeEmployee(table.convertRowIndexToModel(table.getSelectedRow()));
                    showPage("Team");
                }
            } catch (Exception ex) { CafeStyle.error(this, ex); }
        });
        buttons.add(add); buttons.add(remove);
        form.add(buttons, BorderLayout.SOUTH);
        content.add(form, BorderLayout.EAST);
        body.add(content, BorderLayout.CENTER);
        return body;
    }
}
