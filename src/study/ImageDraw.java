package study;

import javax.swing.*;
import java.awt.*;

public class ImageDraw extends JComponent {
    private Image capture;

    ImageDraw(Image capture) {
        this.capture = capture;
    }

    public void paintComponent(Graphics g) {
        // Прорисовка изображения
        g.drawImage(capture, 0, 0, this);
    }
}
