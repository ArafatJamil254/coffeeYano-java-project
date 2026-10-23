import javax.swing.*;
import java.awt.*;

public class ReceiptFrame extends JFrame {
    public ReceiptFrame(CoffeeOrder order) {
        super("CoffeeYano | Order #" + order.getId());
        setSize(485, 590);
        setMinimumSize(new Dimension(400, 440));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        setLayout(new BorderLayout());

        JPanel top = CafeStyle.panel(CafeStyle.COFFEE);
        top.setBorder(BorderFactory.createEmptyBorder(18, 23, 18, 23));
        JLabel title = CafeStyle.label("A fresh receipt", 23, Color.WHITE, true);
        title.setFont(CafeStyle.titleFont(23));
        top.add(title);
        add(top, BorderLayout.NORTH);

        JTextArea receipt = new JTextArea(order.getReceipt());
        receipt.setFont(new Font("Monospaced", Font.PLAIN, 13));
        receipt.setEditable(false);
        receipt.setBackground(CafeStyle.PAPER);
        receipt.setForeground(CafeStyle.ESPRESSO);
        receipt.setMargin(new Insets(25, 21, 25, 15));
        add(new JScrollPane(receipt), BorderLayout.CENTER);

        JPanel footer = CafeStyle.panel(CafeStyle.CREAM);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        footer.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton print = CafeStyle.button("Print", CafeStyle.RUST, Color.WHITE);
        print.addActionListener(e -> {
            try { receipt.print(); }
            catch (Exception ex) { CafeStyle.error(this, ex); }
        });
        JButton close = CafeStyle.button("Close", CafeStyle.SAND, CafeStyle.COFFEE);
        close.addActionListener(e -> dispose());
        footer.add(print); footer.add(close);
        add(footer, BorderLayout.SOUTH);
    }
}
