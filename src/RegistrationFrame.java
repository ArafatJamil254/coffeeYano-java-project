import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class RegistrationFrame extends JFrame {
    private CoffeeShopData data;
    private JTextField userTextField;
    private JPasswordField passField, confirmPassField;

    public RegistrationFrame(CoffeeShopData data) {
        super("CoffeeYano | Create account");
        this.data = data;
        setSize(810, 610);
        setMinimumSize(new Dimension(680, 540));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(brandPanel(), BorderLayout.WEST);
        add(registrationPanel(), BorderLayout.CENTER);
    }

    private JPanel brandPanel() {
        JPanel panel = CafeStyle.panel(CafeStyle.SAGE);
        panel.setPreferredSize(new Dimension(255, 0));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 22, 25, 22));
        panel.add(CafeStyle.label("COFFEEYANO", 14, Color.WHITE, true), BorderLayout.NORTH);
        panel.add(new CoffeeArt(CafeStyle.SAGE, 2), BorderLayout.CENTER);
        JLabel bottom = CafeStyle.label("GOOD COFFEE, GOOD COMPANY", 11, Color.WHITE, true);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel registrationPanel() {
        JPanel outer = CafeStyle.panel(CafeStyle.CREAM);
        outer.setLayout(new GridBagLayout());
        JPanel form = CafeStyle.panel(CafeStyle.CREAM);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(380, 448));

        JLabel title = new JLabel("Come on in.");
        title.setFont(CafeStyle.titleFont(35));
        title.setForeground(CafeStyle.ESPRESSO);
        form.add(title);
        form.add(Box.createVerticalStrut(8));
        form.add(CafeStyle.label("Create a local demo account.", 13, CafeStyle.MUTED, false));
        form.add(Box.createVerticalStrut(25));

        form.add(CafeStyle.label("USERNAME", 11, CafeStyle.COFFEE, true));
        form.add(Box.createVerticalStrut(6));
        userTextField = CafeStyle.input();
        addField(form, userTextField);
        form.add(Box.createVerticalStrut(14));

        form.add(CafeStyle.label("PASSWORD", 11, CafeStyle.COFFEE, true));
        form.add(Box.createVerticalStrut(6));
        passField = CafeStyle.passwordInput();
        addField(form, passField);
        form.add(Box.createVerticalStrut(14));
        form.add(CafeStyle.label("CONFIRM PASSWORD", 11, CafeStyle.COFFEE, true));
        form.add(Box.createVerticalStrut(6));
        confirmPassField = CafeStyle.passwordInput();
        addField(form, confirmPassField);
        form.add(Box.createVerticalStrut(20));

        JButton registerButton = CafeStyle.button("Create my account", CafeStyle.RUST, Color.WHITE);
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        registerButton.addActionListener(e -> register());
        form.add(registerButton);
        form.add(Box.createVerticalStrut(9));
        JButton backButton = CafeStyle.button("Back to sign in", CafeStyle.SAND, CafeStyle.COFFEE);
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        backButton.addActionListener(e -> back());
        form.add(backButton);
        form.add(Box.createVerticalStrut(10));
        form.add(CafeStyle.label("6+ password characters. Demo use only.", 11, CafeStyle.MUTED, false));
        outer.add(form);
        return outer;
    }

    private void addField(JPanel form, JComponent field) {
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 41));
        form.add(field);
    }

    private void register() {
        char[] password = passField.getPassword();
        char[] confirm = confirmPassField.getPassword();
        try {
            if (!Arrays.equals(password, confirm)) throw new IllegalArgumentException("Passwords do not match.");
            data.register(userTextField.getText().trim(), password);
            JOptionPane.showMessageDialog(this, "Account created. You can now sign in.");
            back();
        } catch (IllegalArgumentException | IOException ex) {
            CafeStyle.error(this, ex);
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirm, '\0');
            passField.setText("");
            confirmPassField.setText("");
        }
    }

    private void back() {
        dispose();
        new CoffeeShopLoginPage(data).setVisible(true);
    }
}
