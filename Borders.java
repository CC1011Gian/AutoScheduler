
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.AbstractBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

public class Borders {

    public void setRedBorder(JScrollPane scrollPane) {
        Border redLine = BorderFactory.createLineBorder(Color.RED, 2);
        scrollPane.setBorder(redLine);
    }

    public void setBottomBorder(JScrollPane scrollPane, Color color, int thickness) {
        Border bottomBorder = new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(color);
                g2d.fillRect(x, y + height - thickness, width, thickness);
                g2d.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c, Insets insets) {
                insets.bottom = thickness;
                return insets;
            }

            @Override
            public boolean isBorderOpaque() {
                return true;
            }
        };
        scrollPane.setBorder(bottomBorder);
    }
}