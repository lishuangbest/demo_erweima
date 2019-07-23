package com.example.demo_erweima.erweima.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;

/**
 * @author 李爽
 * @date 2019/7/13 14:41
 */
public class CreateQRcode {
    public static void main(String[] args) {

        int width=300;
        int height=300;
        String format="png";
        String content="http://guopengli.cn";

        //定义二维码的参数
        HashMap hints= new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);

        try {
            BitMatrix bitMatrix  ;
            bitMatrix=new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE,width,height,hints);
            /*Path file = new File("D:/code/img.png").toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, format, file);*/
            System.out.println(bitMatrix.toString());

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
