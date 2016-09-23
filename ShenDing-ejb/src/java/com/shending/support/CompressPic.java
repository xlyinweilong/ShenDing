/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.util.Iterator;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Administrator
 */
public class CompressPic {

    /**
     * *****************************************************************************
     * 缩略图类（通用） 本java类能将jpg、bmp、png、gif图片文件，进行等比或非等比的大小转换。 具体使用方法
     * compressPic(大图片路径,生成小图片路径,大图片文件名,生成小图片文名,生成小图片宽度,生成小图片高度,是否等比缩放(默认为true))
     */
    private File file = null; // 文件对象
    private String inputDir; // 输入图路径
    private String outputDir; // 输出图路径
    private String inputFileName; // 输入图文件名
    private String outputFileName; // 输出图文件名
    private int outputWidth = 100; // 默认输出图片宽
    private int outputHeight = 100; // 默认输出图片高
    private boolean proportion = true; // 是否等比缩放标记(默认为等比缩放)

    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public void setOutputWidth(int outputWidth) {
        this.outputWidth = outputWidth;
    }

    public void setOutputHeight(int outputHeight) {
        this.outputHeight = outputHeight;
    }

    public void setWidthAndHeight(int width, int height) {
        this.outputWidth = width;
        this.outputHeight = height;
    }

    /*
     * 获得图片大小 传入参数 String path ：图片路径
     */
    public long getPicSize(String path) {
        file = new File(path);
        return file.length();
    }

    // 图片处理
    public String compressPic() {
        try {
            // 获得源文件
            file = new File(inputDir);
            //System.out.println(inputDir + inputFileName);
            if (!file.exists()) {
                //throw new Exception("文件不存在");
            }
            Image img = ImageIO.read(file);
            // 判断图片格式是否正确
            if (img.getWidth(null) == -1) {
                System.out.println(" can't read,retry!" + "<BR>");
                return "no";
            } else {
                int newWidth;
                int newHeight;
                // 判断是否是等比缩放
                if (this.proportion == true) {
                    // 为等比缩放计算输出的图片宽度及高度
                    double rate1 = ((double) img.getWidth(null))
                            / (double) outputWidth + 0.1;
                    double rate2 = ((double) img.getHeight(null))
                            / (double) outputHeight + 0.1;
                    // 根据缩放比率大的进行缩放控制
                    double rate = rate1 > rate2 ? rate1 : rate2;
                    newWidth = (int) (((double) img.getWidth(null)) / rate);
                    newHeight = (int) (((double) img.getHeight(null)) / rate);
                } else {
                    newWidth = outputWidth; // 输出的图片宽度
                    newHeight = outputHeight; // 输出的图片高度
                }
                BufferedImage tag = new BufferedImage((int) newWidth,
                        (int) newHeight, BufferedImage.TYPE_INT_RGB);

                /*
                 * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
                 */
                tag.getGraphics().drawImage(
                        img.getScaledInstance(newWidth, newHeight,
                                Image.SCALE_SMOOTH), 0, 0, null);
                File f = new File(outputDir);
                if (!f.exists()) {
                    f.mkdirs();
                }
                FileOutputStream out = new FileOutputStream(outputDir
                        + outputFileName);
                // JPEGImageEncoder可适用于其他图片类型的转换
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                encoder.encode(tag);
                out.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "ok";
    }

    public String compressPic(String outputDir, String inputFileName,
            String outputFileName) {
        // 输出图路径
        this.outputDir = outputDir;
        // 输入图文件名
        this.inputFileName = inputFileName;
        // 输出图文件名
        this.outputFileName = outputFileName;
        return compressPic();
    }

    public String compressPic(String inputDir, String outputDir,
            String inputFileName, String outputFileName, int width, int height,
            boolean gp) {
        // 输入图路径
        this.inputDir = inputDir;
        // 输出图路径
        this.outputDir = outputDir;
        // 输入图文件名
        this.inputFileName = inputFileName;
        // 输出图文件名
        this.outputFileName = outputFileName;
        // 设置图片长宽
        setWidthAndHeight(width, height);
        // 是否是等比缩放 标记
        this.proportion = gp;
        return compressPic();
    }

    /**
     * 从中间裁剪图片按照传入的比例
     *
     * @param srcFile
     * @param dstFile
     * @param widthRange
     * @param heightRange
     */
    public static void cutSquare(String srcFile, String dstFile, int widthRange,
            int heightRange, int width, int height) {
        int x = 0;
        int y = 0;
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(new File(srcFile));
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(iis);
            ImageReader reader = (ImageReader) iterator.next();
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            int oldWidth = reader.getWidth(0);
            int oldHeight = reader.getHeight(0);
            int newWidth, newHeight;
            if (width <= oldWidth && height <= oldHeight) {
                newWidth = oldHeight * widthRange / heightRange;
                if (newWidth < oldWidth) {
                    newHeight = oldHeight;
                    x = (oldWidth - newWidth) / 2;
                } else {
                    newWidth = oldWidth;
                    newHeight = oldWidth * heightRange / widthRange;
                    y = (oldHeight - newHeight) / 2;
                }
                Rectangle rectangle = new Rectangle(x, y, newWidth, newHeight);
                param.setSourceRegion(rectangle);
                BufferedImage bi = reader.read(0, param);
                BufferedImage tag = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
                tag.getGraphics().drawImage(bi.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
                File file = new File(dstFile);
                ImageIO.write(tag, reader.getFormatName(), file);
            } else {
                BufferedImage bi = reader.read(0, param);
                BufferedImage tag = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = tag.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, tag.getWidth(), tag.getHeight());
                g2d.drawImage(bi.getScaledInstance(bi.getWidth(), bi.getHeight(), Image.SCALE_SMOOTH), (width - bi.getWidth()) / 2, (height - bi.getHeight()) / 2, null);
                g2d.dispose();
                File file = new File(dstFile);
                ImageIO.write(tag, reader.getFormatName(), file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cut(String srcFile, String dstFile, int widthRange,
            int heightRange) {
        int x = 0;
        int y = 0;
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(new File(srcFile));
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(iis);
            ImageReader reader = (ImageReader) iterator.next();
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            int oldWidth = reader.getWidth(0);
            int oldHeight = reader.getHeight(0);
            int newWidth, newHeight;
            newWidth = oldHeight * widthRange / heightRange;
            if (newWidth < oldWidth) {
                newHeight = oldHeight;
                x = (oldWidth - newWidth) / 2;
            } else {
                newWidth = oldWidth;
                newHeight = oldWidth * heightRange / widthRange;
                y = (oldHeight - newHeight) / 2;
            }
            Rectangle rectangle = new Rectangle(x, y, newWidth, newHeight);
            param.setSourceRegion(rectangle);
            BufferedImage bi = reader.read(0, param);
            File file = new File(dstFile);
            ImageIO.write(bi, reader.getFormatName(), file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws IOException {
        String s = "f:\\2.png";
        String s2 = "f:\\234.jpg";
    }
}
