package util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

// 图片工具类
public class ImageUtil {

    // 创建指定尺寸的纯色图片
    public static ImageIcon createColorImage(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 填充背景色
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);

        g2d.dispose();

        return new ImageIcon(image);
    }
    public static ImageIcon createColorImage(int width, int height, int r, int g, int b) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 填充背景色
        g2d.setColor(new Color(r, g, b));
        g2d.fillRect(0, 0, width, height);

        g2d.dispose();

        return new ImageIcon(image);
    }

    // 创建默认的白色拼图图片(105*105像素)
    public static ImageIcon createDefaultPuzzleImage() {
        return createColorImage(250, 250, 255,255,255);//243, 220, 123
    }
}