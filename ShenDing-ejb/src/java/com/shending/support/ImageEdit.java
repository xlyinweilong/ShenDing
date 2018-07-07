/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shending.support;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

/**
 *
 * @author yin
 */
public class ImageEdit {

    public static void main(String[] a) {

        ImageEdit.createStringMark("e://1.jpg", "e://2.jpg", "小明", "web123", "888", "2018-05-19", "2022-07-08");
    }

    /**
     * @param filePath 源图片路径
     * @param markContent 图片中添加内容
     * @param outPath 输出图片路径 字体颜色等在函数内部实现的
     */
    //给jpg添加文字
    public static boolean createStringMark(String filePath, String outPath, String name, String wecatCode, String contractCode, String start, String end) {
        System.out.println("****************************1");
        System.out.println("filePath="+filePath);
        System.out.println("outPath="+outPath);
        System.out.println(name);
        ImageIcon imgIcon = new ImageIcon(filePath);
        Image theImg = imgIcon.getImage();
        int width = theImg.getWidth(null) == -1 ? 100 : theImg.getWidth(null);
        int height = theImg.getHeight(null) == -1 ? 100 : theImg.getHeight(null);
        System.out.println("****************************2");
        System.out.println(width);
        System.out.println(height);
        System.out.println(theImg);
        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bimage.createGraphics();

        Color mycolor = Color.BLACK;
        g.setColor(mycolor);
//        g.setBackground(Color.red);
        g.drawImage(theImg, 0, 0, null);
        g.setFont(new Font("黑体", Font.PLAIN, 20)); //字体、字型、字号 
        g.drawString(name, 275, 322);
        g.drawString(wecatCode, 485, 322);
        g.drawString(contractCode, 350, 415);
        g.drawString(start, 286, 382);
        g.drawString(end, 423, 382);
        System.out.println("****************************3");
        g.dispose();
        System.out.println("****************************4");
        try {
             // 输出图片  
            FileOutputStream outImgStream = new FileOutputStream(outPath);
            ImageIO.write(bimage, "jpg", outImgStream);
            System.out.println("添加水印完成");
            outImgStream.flush();
            outImgStream.close();
            
//            FileOutputStream out = new FileOutputStream(outPath); //先用一个特定的输出文件名 
//            System.out.println("****************************5");
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//            System.out.println("****************************6");
//            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bimage);
//            System.out.println("****************************7");
//            param.setQuality(50, true);  //
//            encoder.encode(bimage, param);
//            out.close();
            System.out.println("****************************8");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
