import javax.swing.*;
import java.awt.*;

// Checkout confirmation only. The project does not accept real payments.
public class PaymentGatewayGUI extends JDialog {
    private String paymentMethod;

    public PaymentGatewayGUI(JFrame parent, int amount) {
        super(parent, "CoffeeYano | Demo checkout", true);
        setSize(465, 405);
        setMinimumSize(new Dimension(420, 380));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = CafeStyle.panel(CafeStyle.CREAM);
        panel.setBorder(BorderFactory.createEmptyBorder(26, 30, 25, 30));
        panel.setLayout(new BorderLayout(0, 15));
        add(panel);

        JPanel header = CafeStyle.panel(CafeStyle.CREAM);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(CafeStyle.label("LAST STEP", 11, CafeStyle.RUST, true));
        JLabel title = new JLabel("Ready to check out?");
        title.setFont(CafeStyle.titleFont(27));
        title.setForeground(CafeStyle.ESPRESSO);
        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(CafeStyle.label("Total due: Tk " + amount, 17, CafeStyle.COFFEE, true));
        panel.add(header, BorderLayout.NORTH);

        JPanel methods = CafeStyle.card();
        methods.setLayout(new BoxLayout(methods, BoxLayout.Y_AXIS));
        JRadioButton cash = new JRadioButton("Cash on pickup", true);
        JRadioButton counter = new JRadioButton("Pay at counter (demo)");
        JRadioButton card = new JRadioButton("Card payment (demo)");
        ButtonGroup group = new ButtonGroup();
        for (JRadioButton method : new JRadioButton[]{cash, counter, card}) {
            method.setBackground(CafeStyle.PAPER);
            method.setFont(CafeStyle.bodyFont(14));
            method.setForeground(CafeStyle.ESPRESSO);
            group.add(method);
            methods.add(method);
            methods.add(Box.createVerticalStrut(12));
        }
        JLabel note = CafeStyle.label("Simulation only. Do not enter card details.", 11, CafeStyle.MUTED, false);
        methods.add(note);
        panel.add(methods, BorderLayout.CENTER);

        JPanel actions = CafeStyle.panel(CafeStyle.CREAM);
        actions.setLayout(new GridLayout(1, 2, 10, 0));
        JButton cancel = CafeStyle.button("Go back", CafeStyle.SAND, CafeStyle.COFFEE);
        cancel.addActionListener(e -> dispose());
        JButton confirm = CafeStyle.button("Place demo order", CafeStyle.RUST, Color.WHITE);
        confirm.addActionListener(e -> {
            if (cash.isSelected()) paymentMethod = "Cash on pickup";
            else if (counter.isSelected()) paymentMethod = "Pay at counter";
            else paymentMethod = "Card demo";
            dispose();
        });
        actions.add(cancel);
        actions.add(confirm);
        panel.add(actions, BorderLayout.SOUTH);
    }

    public String confirmCheckout() {
        setVisible(true);
        return paymentMethod;
    }
}
