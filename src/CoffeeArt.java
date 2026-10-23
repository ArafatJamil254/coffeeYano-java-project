import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class CoffeeArt extends JPanel {
    private Color background;
    private int design;

    public CoffeeArt(Color background, int design) {
        this.background = background;
        this.design = design;
        setBackground(background);
        setPreferredSize(new Dimension(180, 115));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g.setColor(background);
        g.fillRect(0, 0, w, h);
        g.translate(w / 2.0, h / 2.0);
        double scale = Math.min(w / 210.0, h / 155.0);
        g.scale(scale, scale);
        g.translate(-105, -78);

        g.setColor(new Color(255, 255, 255, 45));
        g.fillOval(22, 1, 169, 150);
        g.setStroke(new BasicStroke(1.4f));
        g.setColor(new Color(255, 255, 255, 90));
        g.drawOval(35, 9, 142, 130);

        Color cup = design % 3 == 0 ? new Color(255, 244, 217) :
                (design % 3 == 1 ? new Color(250, 220, 196) : new Color(255, 250, 238));
        Color drink = design % 4 == 1 ? new Color(110, 65, 43) : new Color(159, 98, 64);
        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(cup);
        g.drawArc(131, 54, 44, 54, -75, 240);

        g.setColor(new Color(71, 41, 31, 42));
        g.fillRoundRect(46, 123, 117, 8, 8, 8);
        g.setColor(cup);
        g.fillRoundRect(48, 53, 111, 71, 12, 30);
        g.setColor(new Color(231, 204, 172));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(48, 53, 111, 71, 12, 30);
        g.setColor(drink);
        g.fillOval(48, 47, 112, 24);
        g.setColor(new Color(255, 228, 190));
        g.setStroke(new BasicStroke(2.2f));
        g.drawArc(78, 53, 56, 11, 10, 160);
        g.drawArc(83, 57, 39, 8, 205, 135);

        g.setColor(new Color(244, 237, 210));
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 3; i++) {
            int x = 77 + i * 27;
            Path2D.Double steam = new Path2D.Double();
            steam.moveTo(x, 43);
            steam.curveTo(x - 12, 33, x + 14, 25, x, 13);
            g.draw(steam);
        }
        g.dispose();
    }
}
