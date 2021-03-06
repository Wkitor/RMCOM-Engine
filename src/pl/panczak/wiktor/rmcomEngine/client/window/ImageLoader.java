package pl.panczak.wiktor.rmcomEngine.client.window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageLoader {
    public static BufferedImage loadImage(String path){
        BufferedImage image = null;
        try {
            image = ImageIO.read(ImageLoader.class.getResource(path));
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return image;
    }
}
