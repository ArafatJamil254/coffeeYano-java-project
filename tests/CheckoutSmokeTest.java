import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import javax.imageio.ImageIO;

public class CheckoutSmokeTest {
    private static JButton findButton(Container root, String text) {
        for (Component c : root.getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().contains(text)) return (JButton) c;
            if (c instanceof Container) {
                JButton button = findButton((Container) c, text);
                if (button != null) return button;
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        CafeStyle.applyLookAndFeel();
        SwingUtilities.invokeAndWait(() -> {
            JFrame owner = new JFrame("Preview window");
            owner.setSize(650, 520);
            owner.setLocationRelativeTo(null);
            owner.setVisible(true);

            PaymentGatewayGUI dialog = new PaymentGatewayGUI(owner, 850);
            Timer timer = new Timer(600, e -> {
                try {
                    ImageIO.write(new Robot().createScreenCapture(dialog.getBounds()), "png", Paths.get(args[0]).toFile());
                    JButton confirm = findButton(dialog, "Place demo order");
                    if (confirm == null) throw new AssertionError("Checkout button missing");
                    confirm.doClick();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            timer.setRepeats(false);
            timer.start();

            String method = dialog.confirmCheckout();
            if (!"Cash on pickup".equals(method)) throw new AssertionError("Wrong default checkout method");
            owner.dispose();
        });
        System.out.println("Checkout dialog interaction passed");
    }
}
