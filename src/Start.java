import javax.swing.*;

public class Start {
    public static void main(String[] args) {
        CafeStyle.applyLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            try {
                CoffeeShopData data = new CoffeeShopData();
                CoffeeShopLoginPage login = new CoffeeShopLoginPage(data);
                login.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Could not start CoffeeYano:\n" + e.getMessage(),
                        "Startup error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
