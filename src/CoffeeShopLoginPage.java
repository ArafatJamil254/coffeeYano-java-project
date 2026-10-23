import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class CoffeeShopLoginPage extends JFrame {
    private CoffeeShopData data;
    private JTextField userTextField;
    private JPasswordField passField;

    public CoffeeShopLoginPage(CoffeeShopData data) {
        super("CoffeeYano | Welcome");
        this.data = data;
        setSize(960, 620);
        setMinimumSize(new Dimension(760, 540));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));
        add(buildBrandPanel());
        add(buildLoginPanel());
    }

    private JPanel buildBrandPanel() {
        JPanel panel = CafeStyle.panel(CafeStyle.COFFEE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(28, 35, 28, 35));

        JPanel top = CafeStyle.panel(CafeStyle.COFFEE);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel name = CafeStyle.label("COFFEEYANO", 14, CafeStyle.HONEY, true);
        top.add(name);
        top.add(Box.createVerticalStrut(5));
        top.add(CafeStyle.label("BREWED WITH CARE  /  EST. TODAY", 10, new Color(223, 199, 177), false));
        panel.add(top, BorderLayout.NORTH);

        CoffeeArt art = new CoffeeArt(CafeStyle.COFFEE, 1);
        panel.add(art, BorderLayout.CENTER);

        JPanel bottom = CafeStyle.panel(CafeStyle.COFFEE);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        JLabel message = new JLabel("A little coffee.");
        message.setFont(CafeStyle.titleFont(38));
        message.setForeground(Color.WHITE);
        bottom.add(message);
        JLabel message2 = new JLabel("A better day.");
        message2.setFont(CafeStyle.titleFont(38));
        message2.setForeground(CafeStyle.HONEY);
        bottom.add(message2);
        bottom.add(Box.createVerticalStrut(13));
        bottom.add(CafeStyle.label("Browse the menu, build your order,", 13, new Color(232, 215, 198), false));
        bottom.add(CafeStyle.label("and enjoy a cozy corner of Java Swing.", 13, new Color(232, 215, 198), false));
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLoginPanel() {
        JPanel outer = CafeStyle.panel(CafeStyle.CREAM);
        outer.setLayout(new GridBagLayout());
        JPanel form = CafeStyle.panel(CafeStyle.CREAM);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(340, 470));

        JLabel small = CafeStyle.label("WELCOME BACK", 11, CafeStyle.RUST, true);
        small.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(small);
        form.add(Box.createVerticalStrut(14));
        JLabel title = new JLabel("Your usual cup?");
        title.setFont(CafeStyle.titleFont(33));
        title.setForeground(CafeStyle.ESPRESSO);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(11));
        JLabel hint = CafeStyle.label("Sign in, or look around as a guest.", 13, CafeStyle.MUTED, false);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(hint);
        form.add(Box.createVerticalStrut(28));

        form.add(formLabel("USERNAME"));
        form.add(Box.createVerticalStrut(7));
        userTextField = CafeStyle.input();
        addField(form, userTextField);
        form.add(Box.createVerticalStrut(15));
        form.add(formLabel("PASSWORD"));
        form.add(Box.createVerticalStrut(7));
        passField = CafeStyle.passwordInput();
        addField(form, passField);
        form.add(Box.createVerticalStrut(21));

        JButton login = CafeStyle.button("Sign in", CafeStyle.RUST, Color.WHITE);
        login.setAlignmentX(Component.LEFT_ALIGNMENT);
        login.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        login.addActionListener(e -> signIn());
        passField.addActionListener(e -> signIn());
        form.add(login);
        form.add(Box.createVerticalStrut(9));
        JButton guest = CafeStyle.button("Browse as guest", CafeStyle.SAND, CafeStyle.ESPRESSO);
        guest.setAlignmentX(Component.LEFT_ALIGNMENT);
        guest.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        guest.addActionListener(e -> openCustomer("Guest"));
        form.add(guest);
        form.add(Box.createVerticalStrut(16));

        JPanel links = CafeStyle.panel(CafeStyle.CREAM);
        links.setLayout(new GridLayout(1, 2, 10, 0));
        links.setAlignmentX(Component.LEFT_ALIGNMENT);
        links.setMaximumSize(new Dimension(Integer.MAX_VALUE, 37));
        JButton register = CafeStyle.button("Create account", CafeStyle.CREAM, CafeStyle.RUST);
        register.addActionListener(e -> {
            dispose();
            new RegistrationFrame(data).setVisible(true);
        });
        JButton staff = CafeStyle.button("Staff demo  →", CafeStyle.CREAM, CafeStyle.COFFEE);
        staff.addActionListener(e -> {
            dispose();
            new CoffeeShopAdminDashboard(data).setVisible(true);
        });
        links.add(register);
        links.add(staff);
        form.add(links);
        form.add(Box.createVerticalStrut(15));
        JLabel note = CafeStyle.label("Coursework demo · No live payments", 11, CafeStyle.MUTED, false);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(note);
        outer.add(form);
        return outer;
    }

    private JLabel formLabel(String text) {
        JLabel label = CafeStyle.label(text, 11, CafeStyle.COFFEE, true);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void addField(JPanel form, JComponent field) {
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        form.add(field);
    }

    private void signIn() {
        char[] password = passField.getPassword();
        String username = userTextField.getText().trim();
        try {
            if (data.login(username, password)) {
                openCustomer(username);
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect username or password.", "Sign in", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            Arrays.fill(password, '\0');
            passField.setText("");
        }
    }

    private void openCustomer(String name) {
        dispose();
        new CoffeeShopCustomerPanel(data, name, false).setVisible(true);
    }
}
