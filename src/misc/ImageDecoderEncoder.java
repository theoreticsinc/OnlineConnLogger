package misc;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import api.OnlineAuthConnection;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author theor
 */
public class ImageDecoderEncoder {

    public static BufferedImage decodeToImage(String imageString) {
        BufferedImage image = null;
        byte[] imageByte;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            imageByte = decoder.decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception ex) {
            Logger.getLogger(ImageDecoderEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }

    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            Base64.Encoder encoder = Base64.getEncoder();
            imageString = encoder.encodeToString(imageBytes);

            bos.close();
        } catch (IOException ex) {
            Logger.getLogger(ImageDecoderEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imageString;
    }

    public BufferedImage convertNSaveString2BuffImage(String base64String) {
        BufferedImage img = null;
        try {
            //convert base64 string to binary data
            byte[] data = DatatypeConverter.parseBase64Binary(base64String);

            InputStream is = new ByteArrayInputStream(data);
            img = ImageIO.read(is);

//            Path target = Paths.get("C:\\NETSHARE\\new.png");
            Graphics2D g = img.createGraphics();
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.WHITE);
//            g.drawString("Plate", 50, 50);

            // save it
//            ImageIO.write(img, "png", target.toFile());
//            String path = "C:\\NETSHARE\\test_imageX." + extension;
//            File file = new File(path);
//            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
//                outputStream.write(data);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            File outputfile = new File("C:\\SNAPSHOTS\\test_image.jpg");
            ImageIO.write(img, "jpg", outputfile);

        } catch (Exception ex) {
            Logger.getLogger(ImageDecoderEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }

        return img;
    }

    public BufferedImage convertString2BuffImage(String base64String) {
        BufferedImage img = null;
        try {
            //convert base64 string to binary data
            byte[] data = DatatypeConverter.parseBase64Binary(base64String);

            InputStream is = new ByteArrayInputStream(data);
            img = ImageIO.read(is);

//            Path target = Paths.get("C:\\NETSHARE\\new.png");
            Graphics2D g = img.createGraphics();
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.WHITE);
//            g.drawString("Plate", 50, 50);

            // save it
//            ImageIO.write(img, "png", target.toFile());
//            String path = "C:\\NETSHARE\\test_imageX." + extension;
//            File file = new File(path);
//            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
//                outputStream.write(data);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            File outputfile = new File("C:\\NETSHARE\\test_imageX.jpg");
//            ImageIO.write(img, "jpg", outputfile);

        } catch (Exception ex) {
            Logger.getLogger(ImageDecoderEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }

        return img;
    }

    public String rollingSnapshots(BufferedImage buf) {
        XMLreader xr = new XMLreader();
        try {

            int zoomFactor = Integer.parseInt(xr.getElementValue("C:/JTerminals/zoom.xml", "zoomFactor"));
            int zoomSize = Integer.parseInt(xr.getElementValue("C:/JTerminals/zoom.xml", "zoomSize"));

            //BufferedImage buf = convertStringFromFile2Image("C:\\NETSHARE\\intercom.json");
            if (zoomFactor >= 3) {
                int Height = buf.getHeight() / zoomFactor;
                int Width = buf.getWidth() / zoomFactor;
                //buf = ImgResizer.cropImage(buf, buf.getWidth() / zoom - (zoom * 10), buf.getHeight() / zoom - (zoom * 10), (buf.getWidth() / zoom) * (zoom - 1), (buf.getHeight() / zoom) * (zoom - 1));
                //buf = ImgResizer.cropImage(buf, 150, 50, (buf.getWidth() / zoom) * (zoom - 1), (buf.getHeight() / zoom) * (zoom - 1));
                try {
                    //buf = ImgResizer.cropImage(buf, Width, Height, Width + zoomSize, Height + zoomSize);
                    buf = ImgResizer.cropImage(buf, Width, 50, 250, 150);

                } catch (Exception ex) {
                }
            }
            ImageDecoderEncoder ide = new ImageDecoderEncoder();
            String s = ide.encodeToString(buf, "jpg");
            ide.convertString2BuffImage(s);
//            ide.convertNSaveString2BuffImage(s);
            return s;
//            System.out.println("buf: " + s);
        } catch (Exception ex) {
            Logger.getLogger(OnlineAuthConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
