package uk.co.alumeni.prism.services.helpers.processors;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.exceptions.PrismBadRequestException;

@Component
public class InstitutionLogoProcessor implements ImageDocumentProcessor {

    @Override
    public byte[] process(byte[] content, String contentType) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(content));
            if (image == null) {
                throw new PrismBadRequestException("Uploaded file is not valid image file");
            }

            final int WIDTH = 320;
            final int HEIGHT = 250;
            boolean fitToWidth = (float) image.getWidth() / WIDTH >= (float) image.getHeight() / HEIGHT;
            if (fitToWidth) {
                image = Scalr.resize(image, Scalr.Mode.FIT_TO_WIDTH, WIDTH, HEIGHT);
            } else {
                image = Scalr.resize(image, Scalr.Mode.FIT_TO_HEIGHT, WIDTH, HEIGHT);
            }

            BufferedImage paddedImage = new BufferedImage(WIDTH, HEIGHT, image.getType());
            Graphics graphics = paddedImage.getGraphics();
            graphics.fillRect(0, 0, WIDTH, HEIGHT);
            graphics.drawImage(image, (WIDTH - image.getWidth()) / 2, (HEIGHT - image.getHeight()) / 2, null);
            graphics.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(paddedImage, contentType.replaceAll("image/", ""), baos);
            baos.flush();
            content = baos.toByteArray();
            baos.close();
            return content;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
