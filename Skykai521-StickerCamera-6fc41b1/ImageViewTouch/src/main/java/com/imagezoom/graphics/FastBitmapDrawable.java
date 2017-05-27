package com.imagezoom.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import java.io.InputStream;

/**
 * 快速绘制二值图，不支持状态，仅支持
 * alpha and colormatrix
 * @author binlee
 * @date 2016年11月24日
 */
public class FastBitmapDrawable extends Drawable implements IBitmapDrawable {

	//绘制的位图
	protected Bitmap mBitmap;
	//绘制的点
	protected Paint mPaint;

	public FastBitmapDrawable( Bitmap b ) {
		mBitmap = b;
		mPaint = new Paint();
		mPaint.setDither( true );
		mPaint.setFilterBitmap( true );
	}
	
	public FastBitmapDrawable( Resources res, InputStream is ){
		this(BitmapFactory.decodeStream(is));
	}

	@Override
	//图像绘制
	public void draw( Canvas canvas ) {
		canvas.drawBitmap( mBitmap, 0.0f, 0.0f, mPaint );
	}

	@Override
	//获得透明度
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	//设置透明度
	public void setAlpha( int alpha ) {
		mPaint.setAlpha( alpha );
	}

	@Override
	//设置滤镜效果
	public void setColorFilter( ColorFilter cf ) {
		mPaint.setColorFilter( cf );
	}

	@Override
	//获得图像资源的快读
	public int getIntrinsicWidth() {
		return mBitmap.getWidth();
	}

	@Override
	//获得图像资源的高度
	public int getIntrinsicHeight() {
		return mBitmap.getHeight();
	}

	@Override
	//获得图像中最小的宽度
	public int getMinimumWidth() {
		return mBitmap.getWidth();
	}

	@Override
	//获得图像中最小的高度
	public int getMinimumHeight() {
		return mBitmap.getHeight();
	}
	
	public void setAntiAlias( boolean value ){
		//图像实现抗锯齿防抖动
		//参照：http://blog.csdn.net/lovexieyuan520/article/details/50732023
		mPaint.setAntiAlias( value );
		invalidateSelf();
	}

	@Override
	//得到类中的位图
	public Bitmap getBitmap() {
		return mBitmap;
	}
}