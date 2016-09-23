package com.shending.support;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * 二维码生成
 *
 * @author yin.weilong
 */
public class QRcode {

    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xFFFFFFFF;

    public static void main(String args[]) throws WriterException, IOException {
        QRcode.createQRcode("d:/123qr.png", "png", "http://www.aladingren.com/admin", 100, 100);
    }

    /**
     * 生成二位码文件
     *
     * @param fullPath
     * @param format
     * @param contents
     * @param width
     * @param height
     * @throws WriterException
     * @throws IOException
     */
    public static void createQRcode(String fullPath, String format, String contents, int width, int height) throws WriterException, IOException {
        BitMatrix matrix = QRcode.deleteWhite(new QRCodeWriter().encode(contents, BarcodeFormat.QR_CODE, width, height));
        File file = new File(fullPath);
        writeToFile(matrix, format, file);
    }

    /**
     * 去掉BitMatrix的白边
     *
     * @param matrix
     * @return
     */
    public static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }

    /**
     * 把二维码写入文件
     *
     * @param matrix
     * @param format
     * @param file
     * @throws IOException
     */
    public static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        ImageIO.write(image, format, file);
    }

    /**
     * 生成二维码内容
     *
     * @param matrix
     * @return
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) == true ? BLACK : WHITE);
            }
        }
        return image;
    }

}
