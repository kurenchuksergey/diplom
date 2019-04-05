package com.kurenchuksergey.diplom.service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class ImageService {

    /* TODO новое исключение */
    public static BufferedImage fromByteArray(byte[] array, String  MIMEType) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(byteArrayInputStream);
        Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByMIMEType(MIMEType);
        if(!imageReaders.hasNext()){
            throw new RuntimeException();
        }
        ImageReader reader = imageReaders.next();
        reader.setInput(imageInputStream);
        return reader.read(0);
    }

    public static byte[] toByteArray(BufferedImage image, String  MIMEType) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(MIMEType);
        if(!imageWriters.hasNext()){
            throw new RuntimeException();
        }
        ImageWriter imageWriter = imageWriters.next();
        imageWriter.setOutput(imageOutputStream);
        imageWriter.write(image);
        return outputStream.toByteArray();
    }

    public static BufferedImage filter(BufferedImage image){
        BufferedImage grayFrame = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        BufferedImageOp grayscaleConv =
                new ColorConvertOp(image.getColorModel().getColorSpace(),
                        grayFrame.getColorModel().getColorSpace(), null);
        return grayscaleConv.filter(image, grayFrame);
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
