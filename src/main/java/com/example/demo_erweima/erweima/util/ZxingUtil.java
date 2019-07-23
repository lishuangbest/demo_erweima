package com.example.demo_erweima.erweima.util;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class ZxingUtil {

    /**
     * 生成二维码
     * @param response
     * @param width 宽
     * @param height 高
     * @param format 格式
     * @param content 内容
     * @throws WriterException
     * @throws IOException
     */
    public static void createZxing(HttpServletResponse response,int width,int height,int margin,String level
            ,String format,String content) throws WriterException, IOException {
        ServletOutputStream stream = null;
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.valueOf(level));// 纠错等级L,M,Q,H
            hints.put(EncodeHintType.MARGIN, margin); // 边距
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE,height, width, hints);
            stream = response.getOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
        } catch (WriterException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                stream.flush();
                stream.close();
            }
        }
    }

    /**
     * 读取二维码
     * @throws IOException
     * @throws NotFoundException
     */
    public static String readZxing(String qrcodeUrl) throws IOException, NotFoundException {
        MultiFormatReader read = new MultiFormatReader();
        URL url = new URL(qrcodeUrl);
        HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
        httpUrl.connect();
        BufferedImage image = ImageIO.read(httpUrl.getInputStream());
        Binarizer binarizer = new HybridBinarizer(new BufferedImageLuminanceSource(image));
        BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
        Result res = read.decode(binaryBitmap);
        return res.toString();
    }
}
