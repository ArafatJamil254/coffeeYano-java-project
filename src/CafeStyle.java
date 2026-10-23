import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class CafeStyle {
    public static final Color CREAM = new Color(250, 246, 238);
    public static final Color PAPER = new Color(255, 253, 248);
    public static final Color SAND = new Color(235, 220, 199);
    public static final Color ESPRESSO = new Color(53, 37, 31);
    public static final Color COFFEE = new Color(91, 59, 45);
    public static final Color RUST = new Color(188, 90, 59);
    public static final Color CORAL = new Color(235, 145, 112);
    public static final Color HONEY = new Color(246, 207, 152);
    public static final Color SAGE = new Color(113, 133, 104);
    public static final Color MUTED = new Color(117, 103, 94);
    public static final Color LINE = new Color(230, 218, 202);

    public static Font titleFont(int size) { return new Font("Serif", Font.BOLD, size); }
    public static Font bodyFont(int size) { return new Font("SansSerif", Font.PLAIN, size); }
    public static Font boldFont(int size) { return new Font("SansSerif", Font.BOLD, size); }

    public static JLabel label(String text, int size, Color color, boolean bold) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(bold ? boldFont(size) : bodyFont(size));
        return label;
    }

    public static JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(boldFont(13));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorder(BorderFactory.createEmptyBorder(11, 17, 11, 17));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setOpaque(true);
        return button;
    }

    public static JTextField input() {
        JTextField field = new JTextField();
        field.setFont(bodyFont(14));
        field.setBackground(PAPER);
        field.setForeground(ESPRESSO);
        field.setCaretColor(RUST);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                BorderFactory.createEmptyBorder(10, 11, 10, 11)));
        return field;
    }

    public static JPasswordField passwordInput() {
        JPasswordField field = new JPasswordField();
        field.setFont(bodyFont(14));
        field.setBackground(PAPER);
        field.setForeground(ESPRESSO);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE),
                BorderFactory.createEmptyBorder(10, 11, 10, 11)));
        return field;
    }

    public static JPanel panel(Color color) {
        JPanel panel = new JPanel();
        panel.setBackground(color);
        return panel;
    }

    public static JPanel card() {
        JPanel panel = panel(PAPER);
        Border line = BorderFactory.createLineBorder(LINE);
        panel.setBorder(BorderFactory.createCompoundBorder(line, BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    public static void table(JTable table) {
        table.setFont(bodyFont(13));
        table.setRowHeight(38);
        table.setGridColor(LINE);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(249, 226, 205));
        table.setSelectionForeground(ESPRESSO);
        table.setBackground(PAPER);
        table.setForeground(ESPRESSO);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(boldFont(12));
        table.getTableHeader().setBackground(new Color(241, 229, 211));
        table.getTableHeader().setForeground(ESPRESSO);
    }

    public static void error(Component parent, Exception e) {
        JOptionPane.showMessageDialog(parent, e.getMessage(), "Please check", JOptionPane.WARNING_MESSAGE);
    }

    public static void applyLookAndFeel() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { }
        UIManager.put("OptionPane.background", CREAM);
        UIManager.put("Panel.background", CREAM);
        UIManager.put("OptionPane.messageForeground", ESPRESSO);
    }
}
