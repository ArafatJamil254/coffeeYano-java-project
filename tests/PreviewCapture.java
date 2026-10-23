import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.ArrayList;

public class PreviewCapture {
    private static void save(JFrame frame, String file) throws Exception {
        Thread.sleep(800);
        Rectangle area = frame.getBounds();
        BufferedImage shot = new Robot().createScreenCapture(area);
        ImageIO.write(shot, "png", Paths.get(file).toFile());
    }

    private static JButton findButton(Container root, String name) {
        for (Component c : root.getComponents()) {
            if (c instanceof JButton && ((JButton)c).getText().contains(name)) return (JButton)c;
            if (c instanceof Container) {
                JButton result = findButton((Container)c, name);
                if (result != null) return result;
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("coffeeyano.data.dir", Files.createTempDirectory("coffeeyano-preview-").toString());
        CafeStyle.applyLookAndFeel();
        CoffeeShopData data = new CoffeeShopData();
        data.register("student_demo", "example123".toCharArray());
        ArrayList<CoffeeOrder.Item> basket = new ArrayList<>();
        basket.add(new CoffeeOrder.Item("Latte", 250, 2));
        basket.add(new CoffeeOrder.Item("Cappuccino", 250, 1));
        data.placeOrder("student_demo", "Cash on pickup", basket);
        data.addEmployee("Maya Rahman", "Barista");

        final CoffeeShopLoginPage[] login = new CoffeeShopLoginPage[1];
        SwingUtilities.invokeAndWait(() -> {
            login[0] = new CoffeeShopLoginPage(data);
            login[0].setVisible(true);
        });
        save(login[0], args[0] + "/login.png");
        SwingUtilities.invokeAndWait(() -> login[0].dispose());

        final CoffeeShopCustomerPanel[] customer = new CoffeeShopCustomerPanel[1];
        SwingUtilities.invokeAndWait(() -> {
            customer[0] = new CoffeeShopCustomerPanel(data, "student_demo", false);
            customer[0].setVisible(true);
            JButton add = findButton(customer[0], "Add to basket");
            if (add == null) throw new AssertionError("No Add to basket button");
            add.doClick();
            add.doClick();
        });
        save(customer[0], args[0] + "/menu.png");
        SwingUtilities.invokeAndWait(() -> customer[0].dispose());

        final CoffeeShopAdminDashboard[] admin = new CoffeeShopAdminDashboard[1];
        SwingUtilities.invokeAndWait(() -> {
            admin[0] = new CoffeeShopAdminDashboard(data);
            admin[0].setVisible(true);
        });
        save(admin[0], args[0] + "/dashboard.png");
        SwingUtilities.invokeAndWait(() -> {
            JButton nav = findButton(admin[0], "Coffee Menu");
            if (nav == null) throw new AssertionError("Menu management button missing");
            nav.doClick();
        });
        save(admin[0], args[0] + "/management.png");
        SwingUtilities.invokeAndWait(() -> admin[0].dispose());
        System.out.println("GUI capture completed");
    }
}
