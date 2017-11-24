package com.dapi.suse.qrcodescanner;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Version;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.HashMap;
import java.util.Map;

public class CodeCreator {

	private static final String LOG_TAG = "CodeCreator";

	private static final int ENCODE_MARGIN = 4;

	/**
	 *
	 * 仅仅就是生成二维码
	 *
	 * @param url
	 * @param bgBitmap
	 * @param flag
     * @return
     */
	public static Bitmap createQRCodeByBitmap(String url,Bitmap bgBitmap,boolean flag) throws WriterException {
		if(bgBitmap == null){
			return null;
		}

		if (url == null || url.equals("")) {
			return null;
		}

		int width = bgBitmap.getWidth();
		int height = bgBitmap.getHeight();

		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, ENCODE_MARGIN);

		ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
		int quietZone = 4;
		if(hints != null) {
			ErrorCorrectionLevel code = (ErrorCorrectionLevel)hints.get(EncodeHintType.ERROR_CORRECTION);
			if(code != null) {
				errorCorrectionLevel = code;
			}

			Integer quietZoneInt = (Integer)hints.get(EncodeHintType.MARGIN);
			if(quietZoneInt != null) {
				quietZone = quietZoneInt.intValue();
			}
		}

		QRCode code1 = Encoder.encode(url, errorCorrectionLevel, hints);
		BitMatrix matrix = renderResult(code1, width, height, quietZone);

		/**
		 *
		 * 计算左上  右上 左下的特征点
		 *
		 */
		int[] topLeftOnBit = matrix.getTopLeftOnBit();
		Version version = code1.getVersion();
		int totalModelNum=(version.getVersionNumber()-1)*4+5+16;    //获取单边模块数
		int resultWidth=width - 2 * (topLeftOnBit[0]);
		int modelWidth=resultWidth / totalModelNum;

		int startModel=0;
		int endModel=7;

		//得到三个基准点的起始与终点
		int topEndX=topLeftOnBit[0]+modelWidth * endModel;
		int topStartX=topLeftOnBit[0]+modelWidth * startModel;
		int topStartY=topLeftOnBit[0]+modelWidth * startModel;
		int topEndY=topLeftOnBit[0]+modelWidth * endModel;
		int rightStartX=(totalModelNum - endModel)*modelWidth+topLeftOnBit[0];
		int rightEndX=width-modelWidth * startModel -topLeftOnBit[0];
		int leftStartY=height-modelWidth * endModel - topLeftOnBit[1];
		int leftEndY=height-modelWidth * startModel - topLeftOnBit[1];

		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					if(x>=topStartX&&x<topEndX&&y>=topStartY&&y<topEndY){
						//左上角颜色
						pixels[y * width + x] = 0xff000000;
					}else if(x<rightEndX&&x>=rightStartX&&y>=topStartY&&y<topEndY){
						//右上角颜色
						pixels[y * width + x] = 0xff000000;
					}else if(x>=topStartX&&x<topEndX&&y>=leftStartY&&y<leftEndY){
						pixels[y * width + x] = 0xff000000;

					}else{
						int tempColor = bgBitmap.getPixel(x,y);
						int tempR = (int) (Color.red(tempColor) * 0.81);
						int tempG = (int) (Color.green(tempColor) * 0.81);
						int tempB = (int) (Color.blue(tempColor) * 0.81);
						/*if(tempR > 170 && tempG > 170 && tempB > 170){
							pixels[y * width + x] = 0xff000000;
						}else{*/
							pixels[y * width + x ] = Color.rgb(tempR,tempG,tempB);
						//}
					}
				}
			}//end for
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;

	}


	private static BitMatrix renderResult(QRCode code, int width, int height, int quietZone) {
		ByteMatrix input = code.getMatrix();
		if(input == null) {
			throw new IllegalStateException();
		} else {
			int inputWidth = input.getWidth();
			int inputHeight = input.getHeight();
			int qrWidth = inputWidth + (quietZone << 1);
			int qrHeight = inputHeight + (quietZone << 1);
			int outputWidth = Math.max(width, qrWidth);
			int outputHeight = Math.max(height, qrHeight);
			int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
			int leftPadding = (outputWidth - inputWidth * multiple) / 2;
			int topPadding = (outputHeight - inputHeight * multiple) / 2;
			BitMatrix output = new BitMatrix(outputWidth, outputHeight);
			int inputY = 0;

			for(int outputY = topPadding; inputY < inputHeight; outputY += multiple) {
				int inputX = 0;

				for(int outputX = leftPadding; inputX < inputWidth; outputX += multiple) {
					if(input.get(inputX, inputY) == 1) {
						output.setRegion(outputX, outputY, multiple, multiple);
					}

					++inputX;
				}

				++inputY;
			}

			return output;
		}
	}


	public static Bitmap createQRCodeByBitmap(String url,Bitmap bgBitmap) throws WriterException {
		if(bgBitmap == null){
			return null;
		}

		if (url == null || url.equals("")) {
			return null;
		}

		int w = bgBitmap.getWidth();
		int h = bgBitmap.getHeight();

		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 1);

		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(url,
				BarcodeFormat.QR_CODE, w, h,hints);

		int width = matrix.getWidth();
		int height = matrix.getHeight();


		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		int[] topLeftOnBit = matrix.getTopLeftOnBit();
		int[] enclosingRectangle = matrix.getEnclosingRectangle();
		int[] bottomRightOnBit = matrix.getBottomRightOnBit();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					if((x < topLeftOnBit[0] * 3 || x > width - topLeftOnBit[0] * 3 ) && (y < topLeftOnBit[1] * 3 || y > height - topLeftOnBit[1] * 3)){
						pixels[y * width + x] = 0xff000000;
					}else{
						int tempColor = bgBitmap.getPixel(x,y);
						int tempR = (int) (Color.red(tempColor) * 0.71);
						int tempG = (int) (Color.green(tempColor) * 0.71);
						int tempB = (int) (Color.blue(tempColor) * 0.71);
						if(tempR > 180 && tempG > 180 && tempB > 180){
							pixels[y * width + x] = 0xff000000;
						}else{
							pixels[y * width + x ] = Color.rgb(tempR,tempG,tempB);
						}
					}
				}
			}//end for
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}


	public static Bitmap createQrCodeWithIcon(String url,int width,int height,Bitmap iconBitmap) throws WriterException {
		if(iconBitmap == null){
			return null;
		}

		if (url == null || url.equals("")) {
			return null;
		}

		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, ENCODE_MARGIN);

		ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
		int quietZone = 4;
		if(hints != null) {
			ErrorCorrectionLevel code = (ErrorCorrectionLevel)hints.get(EncodeHintType.ERROR_CORRECTION);
			if(code != null) {
				errorCorrectionLevel = code;
			}

			Integer quietZoneInt = (Integer)hints.get(EncodeHintType.MARGIN);
			if(quietZoneInt != null) {
				quietZone = quietZoneInt.intValue();
			}
		}

		QRCode code1 = Encoder.encode(url, errorCorrectionLevel, hints);
		BitMatrix matrix = renderResult(code1, width, height, quietZone);

		/**
		 *
		 * 计算icon的 中间的坐标
		 *
		 */
		int left = width / 2 - iconBitmap.getWidth() / 2;
		int right = width / 2 + iconBitmap.getWidth() / 2;
		int top = height / 2 - iconBitmap.getHeight() / 2;
		int bottom = height / 2 + iconBitmap.getHeight() / 2;

		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x ] = 0xff000000;
				}

				if(x > left - 8 && x < right + 8 && y > top - 8 && y < bottom + 8){
					pixels[y * width + x ] = 0;
				}

				if(x > left && x < right && y > top && y < bottom){
					int tempX = x - left;
					int tempY = y - top;
					pixels[y * width + x ] = iconBitmap.getPixel(tempX,tempY);
				}
			}//end for
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}


	public static Bitmap createQRCode(String url,int w,int h,Bitmap centerBitmap) throws WriterException {

		if (url == null || url.equals("")) {
			return null;
		}

		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(url,
				BarcodeFormat.QR_CODE, w, h);

		int width = matrix.getWidth();
		int height = matrix.getHeight();

		int centerWidth = centerBitmap.getWidth();
		int centerHeight = centerBitmap.getHeight();

		int centerMinX = width / 2  - centerWidth / 2;
		int centerMinY = height / 2 - centerHeight / 2;

		int centerMaxX = width / 2 + centerWidth / 2;
		int centerMaxY = height / 2 + centerHeight / 2;

		int tempMinX = w / 10;
		int tempMaxX = w / 10 * 9;
		int tempMinY = h / 10;
		int tempMaxY = h / 10 * 9;

		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		int[] topLeftOnBit = matrix.getTopLeftOnBit();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					if(x > centerMinX && x < centerMaxX && y > centerMinY && y < centerMaxY){
						if((x < topLeftOnBit[0] * 3 || x > tempMaxX - 50 ) && (y < topLeftOnBit[1] * 3 || y > tempMaxY - 50)){
							pixels[y * width + x] = 0xff000000;
						}else{
							int tempColor = centerBitmap.getPixel(x - centerMinX,y - centerMinY);
							int tempR = (int) (Color.red(tempColor) * 0.71);
							int tempG = (int) (Color.green(tempColor) * 0.71);
							int tempB = (int) (Color.blue(tempColor) * 0.71);
							if(tempR > 180 && tempG > 180 && tempB > 180){
								pixels[y * width + x] = 0xff000000;
							}else{
								pixels[y * width + x ] = Color.rgb(tempR,tempG,tempB);
							}
						}
					}else{
						pixels[y * width + x] = 0xff000000;
					}
				}
			}//end for
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}


	public static Bitmap createQRCodeByStep(String url,int w,int h,int step) throws WriterException {

		if (url == null || url.equals("")) {
			return null;
		}

		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(url,
				BarcodeFormat.QR_CODE, w, h);

		int width = matrix.getWidth();
		int height = matrix.getHeight();

		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y += step) {
			for (int x = 0; x < width; x += step) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				}
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;

	}

	/**
	 * 生成QRCode（二维码）
	 * 
	 * @return
	 * @throws WriterException
	 */
	public static Bitmap createQRCode(String url,int w,int h) throws WriterException {

		if (url == null || url.equals("")) {
			return null;
		}

		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, ENCODE_MARGIN);
		BitMatrix matrix = new MultiFormatWriter().encode(url,
				BarcodeFormat.QR_CODE, w, h,hints);

		int width = matrix.getWidth();
		int height = matrix.getHeight();

		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				}
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

}
