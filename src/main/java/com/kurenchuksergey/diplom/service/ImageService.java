package com.kurenchuksergey.diplom.service;

import com.kurenchuksergey.diplom.entity.TaskType;

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
    public static BufferedImage fromByteArray(byte[] array, String MIMEType) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(byteArrayInputStream);
        Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByMIMEType(MIMEType);
        if (!imageReaders.hasNext()) {
            throw new RuntimeException();
        }
        ImageReader reader = imageReaders.next();
        reader.setInput(imageInputStream);
        return reader.read(0);
    }

    public static byte[] toByteArray(BufferedImage image, String MIMEType) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(MIMEType);
        if (!imageWriters.hasNext()) {
            throw new RuntimeException();
        }
        ImageWriter imageWriter = imageWriters.next();
        imageWriter.setOutput(imageOutputStream);
        imageWriter.write(image);
        return outputStream.toByteArray();
    }

    public static BufferedImage filterBlack(BufferedImage image) {
        BufferedImage grayFrame = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        BufferedImageOp grayscaleConv =
                new ColorConvertOp(image.getColorModel().getColorSpace(),
                        grayFrame.getColorModel().getColorSpace(), null);
        return grayscaleConv.filter(image, grayFrame);
    }

    public static BufferedImage vertTransform(BufferedImage image) {
        BufferedImage mirrorFrame = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -image.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        op.filter(image, mirrorFrame);
        return mirrorFrame;
    }

    public static BufferedImage horTransform(BufferedImage image) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-image.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }

    public static BufferedImage filter(BufferedImage image, TaskType type) {
        switch (type) {
            case BLACK:
                return filterBlack(image);
            case VERTICAL:
                return vertTransform(image);
            case HORIZONTAL:
                return horTransform(image);
        }
        return image;
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
