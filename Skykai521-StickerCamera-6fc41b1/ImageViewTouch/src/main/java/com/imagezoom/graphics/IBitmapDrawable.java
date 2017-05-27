package com.imagezoom.graphics;

import android.graphics.Bitmap;

import com.imagezoom.ImageViewTouchBase;

/**
 * Base interface used in the {@link ImageViewTouchBase} view
 * @author binlee
 */
public interface IBitmapDrawable {

	/*
	* 	@brief	得到位图
	* */
	Bitmap getBitmap();
}
