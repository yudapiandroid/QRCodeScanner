package com.dapi.suse.scannercreterlib;

import android.graphics.Bitmap;

import com.google.zxing.WriterException;

/**
 * Created by Administrator on 2017/11/24.
 */
public class QrCodeCreater {

    /**
     *
     * 根据字符窜生成 二维码
     *
     * @param message
     *
     * @return
     *
     *
     */
    public static Bitmap creatorCodeImageByString(String message, int width, int height){
        try {
            return CodeCreator.createQRCode(message,width,height);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
