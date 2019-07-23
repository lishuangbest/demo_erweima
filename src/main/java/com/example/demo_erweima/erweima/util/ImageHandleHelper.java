package com.example.demo_erweima.erweima.util;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * @Description:图片处理工具
 * @author:liuyc
 * @time:2016年5月27日 上午10:18:00
 */
public class ImageHandleHelper {

    /**
     * @param 、startAcross 开始截取位置横坐标、StartEndlong开始截图位置纵坐标、width截取的长，hight截取的高
     * @Description:截图
     * @author:liuyc
     * @time:2016年5月27日 上午10:18:23
     */
    public static void cutImage(String srcFile, String targetFile, int startAcross, int StartEndlong, int width,
                                int hight) throws Exception {
        // 取得图片读入器
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
        ImageReader reader = readers.next();
        // 取得图片读入流
        InputStream source = new FileInputStream(srcFile);
        ImageInputStream iis = ImageIO.createImageInputStream(source);
        reader.setInput(iis, true);
        // 图片参数对象
        ImageReadParam param = reader.getDefaultReadParam();
        Rectangle rect = new Rectangle(startAcross, StartEndlong, width, hight);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0, param);
        ImageIO.write(bi, targetFile.split("\\.")[1], new File(targetFile));
    }

    /**
     * @param files 要拼接的文件列表
     * @Description:图片拼接 （注意：必须两张图片长宽一致哦）
     * @author:liuyc
     * @time:2016年5月27日 下午5:52:24
     */
    public static void mergeImage(String[] files, int type, String targetFile) {
        int len = files.length;
        if (len < 1) {
            throw new RuntimeException("图片数量小于1");
        }
        File[] src = new File[len];
        BufferedImage[] images = new BufferedImage[len];
        int[][] ImageArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            try {
                src[i] = new File(files[i]);
                images[i] = ImageIO.read(src[i]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            int width = images[i].getWidth();
            int height = images[i].getHeight();
            ImageArrays[i] = new int[width * height];
            ImageArrays[i] = images[i].getRGB(0, 0, width, height, ImageArrays[i], 0, width);
        }
        int newHeight = 0;
        int newWidth = 0;
        for (int i = 0; i < images.length; i++) {
            // 横向
            if (type == 1) {
                newHeight = newHeight > images[i].getHeight() ? newHeight : images[i].getHeight();
                newWidth += images[i].getWidth();
            } else if (type == 2) {// 纵向
                newWidth = newWidth > images[i].getWidth() ? newWidth : images[i].getWidth();
                newHeight += images[i].getHeight();
            }
        }
        if (type == 1 && newWidth < 1) {
            return;
        }
        if (type == 2 && newHeight < 1) {
            return;
        }

        // 生成新图片
        try {
            BufferedImage ImageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            int height_i = 0;
            int width_i = 0;
            for (int i = 0; i < images.length; i++) {
                if (type == 1) {
                    ImageNew.setRGB(width_i, 0, images[i].getWidth(), newHeight, ImageArrays[i], 0,
                            images[i].getWidth());
                    width_i += images[i].getWidth();
                } else if (type == 2) {
                    ImageNew.setRGB(0, height_i, newWidth, images[i].getHeight(), ImageArrays[i], 0, newWidth);
                    height_i += images[i].getHeight();
                }
            }
            //输出想要的图片
            ImageIO.write(ImageNew, targetFile.split("\\.")[1], new File(targetFile));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:小图片贴到大图片形成一张图(合成)
     * @author:liuyc
     * @time:2016年5月27日 下午5:51:20
     */
    public static final void overlapImage(String bigPath, String smallPath, String outFile) {
        try {
            URL url = new URL(bigPath);
            //打开链接
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inputio = conn.getInputStream();

            //创建一个输出流
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while( (len=inputio.read(buffer)) != -1 ){
                // 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                byteOutStream.write(buffer, 0, len);
            }
            // 关闭输入流
            inputio.close();

            // 得到图片的二进制数据，以二进制封装得到数据，具有通用性
            byte[] data = byteOutStream.toByteArray();
            // 依据指定目录文件new一个文件对象用来保存图片
            File imageFile = new File("D://code/3.jpg");
            // 创建输出流
            FileOutputStream outStream = new FileOutputStream(imageFile);
            // 写入数据
            outStream.write(data);
            // 关闭输出流
            outStream.close();
            BufferedImage big = ImageIO.read(imageFile);
            BufferedImage big1 = ImageIO.read(imageFile);
            BufferedImage small = ImageIO.read(new File(smallPath));
            Graphics2D g = big.createGraphics();
            BufferedImage whiteImg = ImageIO.read(new File("D://code/图片1.png"));
            int a = (big.getWidth() - whiteImg.getWidth()) / 2;
            int b = (big.getHeight() - whiteImg.getHeight())*4 / 9;
            g.drawImage(whiteImg, a, b, whiteImg.getWidth(), (int)Math.round((float) whiteImg.getHeight()*1.1   ), null);
            int c = (big1.getWidth() - big1.getWidth()/5)/2;
            int d = big1.getHeight() / 4;
            g.drawImage(big1, c, d, big1.getWidth()/5, big1.getHeight()/5, null);
            int x = (big.getWidth() - small.getWidth()) / 2;
            int y = (big.getHeight() - small.getHeight())* 4 / 5;
            g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);
            g.dispose();
            ImageIO.write(big, outFile.split("\\.")[1], new File(outFile));
            imageFile.delete();
            new File(smallPath).delete();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
