package com.example.demo_erweima.erweima.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author 李爽
 * @date 2019/7/16 14:45
 */
public class PicUtils {

    public static void main(String[] args) {
        String sourceFilePath = "D://code/1.jpg";
        String waterFilePath = "D://code/2.jpg";
        String saveFilePath = "D://code/overlyingImageNew.jpg";
        try {
            BufferedImage bufferImage1 = getBufferedImage(sourceFilePath);
            BufferedImage bufferImage2 = getBufferedImage(waterFilePath);

            // 构建叠加层
            BufferedImage buffImg = overlyingImage(bufferImage1, bufferImage2, 0, 0, 1.0f);
            // 输出水印图片
            generateSaveFile(buffImg, saveFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * description: 将图片转成BufferedImage
     *
     * @return java.awt.image.BufferedImage
     * @author 李爽
     * @date 2019/7/16 14:57
     */
    public static BufferedImage getBufferedImage(String fileUrl){
        File f = new File(fileUrl);
        try {
            return ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * description: 将远程图片转换成BufferedImage
     *
     * @param destUrl 远程图片地址
     * @return java.awt.image.BufferedImage
     * @author 李爽
     * @date 2019/7/16 15:00
     */
    public static BufferedImage getBufferedImageDestUrl(String destUrl){
        HttpURLConnection connection = null;
        BufferedImage image = null;

        try {
            URL url = new URL(destUrl);
            connection = (HttpURLConnection) url.openConnection();
            if(connection.getResponseCode() == 200){
                image = ImageIO.read(connection.getInputStream());
                return image;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * description: 输出图片
     *
     * @param buffImg 图像拼接叠加之后的BufferedImage对象
     * @param savePath 图像拼接叠加之后保存路径
     * @return void
     * @author 李爽
     * @date 2019/7/16 15:04
     */
    public static void generateSaveFile(BufferedImage buffImg, String savePath){
        int temp = savePath.lastIndexOf(".") + 1;
        try {
            File outFile = new File(savePath);
            if(!outFile.exists()){
                outFile.createNewFile();
            }
            ImageIO.write(buffImg, savePath.substring(temp), outFile);
            System.out.println("ImageIO write---------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @Title: 构造图片
     * @Description: 生成水印并返回java.awt.image.BufferedImage
     * @param buffImg 源文件(BufferedImage)
     * @param waterImg 水印文件(BufferedImage)
     * @param x 距离右下角的X偏移量
     * @param y  距离右下角的Y偏移量
     * @param alpha  透明度, 选择值从0.0~1.0: 完全透明~完全不透明
     * @return BufferedImage
     * @throws IOException
     */
    public static BufferedImage overlyingImage(BufferedImage buffImg, BufferedImage waterImg, int x, int y, float alpha) throws IOException {

        // 创建Graphics2D对象，用在底图对象上绘图
        Graphics2D g2d = buffImg.createGraphics();
        int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
        int waterImgHeight = waterImg.getHeight();// 获取层图的高度
        // 在图形和图像中实现混合和透明效果
//        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        // 绘制
        g2d.drawImage(waterImg, x, y, waterImgWidth, waterImgHeight, null);
        g2d.dispose();// 释放图形上下文使用的系统资源
        return buffImg;
    }

    /**
     * 待合并的两张图必须满足这样的前提，如果水平方向合并，则高度必须相等；如果是垂直方向合并，宽度必须相等。
     * mergeImage方法不做判断，自己判断。
     * @param img1 待合并的第一张图
     * @param img2 带合并的第二张图
     * @param isHorizontal 为true时表示水平方向合并，为false时表示垂直方向合并
     * @return 返回合并后的BufferedImage对象
     * @throws IOException
     */
    public static BufferedImage mergeImage(BufferedImage img1,
                                           BufferedImage img2, boolean isHorizontal) throws IOException {
        int w1 = img1.getWidth();
        int h1 = img1.getHeight();
        int w2 = img2.getWidth();
        int h2 = img2.getHeight();

        // 从图片中读取RGB
        int[] ImageArrayOne = new int[w1 * h1];
        ImageArrayOne = img1.getRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 逐行扫描图像中各个像素的RGB到数组中
        int[] ImageArrayTwo = new int[w2 * h2];
        ImageArrayTwo = img2.getRGB(0, 0, w2, h2, ImageArrayTwo, 0, w2);

        // 生成新图片
        BufferedImage DestImage = null;
        if (isHorizontal) { // 水平方向合并
            DestImage = new BufferedImage(w1+w2, h1, BufferedImage.TYPE_INT_RGB);
            DestImage.setRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            DestImage.setRGB(w1, 0, w2, h2, ImageArrayTwo, 0, w2);
        } else { // 垂直方向合并
            DestImage = new BufferedImage(w1, h1 + h2, BufferedImage.TYPE_INT_RGB);
            DestImage.setRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            DestImage.setRGB(0, h1, w2, h2, ImageArrayTwo, 0, w2); // 设置下半部分的RGB
        }

        return DestImage;
    }


}
