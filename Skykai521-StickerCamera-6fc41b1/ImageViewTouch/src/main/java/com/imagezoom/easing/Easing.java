package com.imagezoom.easing;

/**
*	@brief	图片缓出接口
*	@author:binlee
*	@date 2016年12月24日
*/
public interface Easing {

	/*
	* 	@brief	图片缓出
	* 	@param	当前时间
	* 	@param	开始
	* 	@param	结束
	* 	@param	持续时间
	* */
	double easeOut(double time, double start, double end, double duration);

	/*
	* 	@brief	图片缓入
	* 	@param	当前时间
	* 	@param	开始
	* 	@param	结束
	* 	@param	持续时间
	* */
	double easeIn(double time, double start, double end, double duration);

	/*
	* 	@brief	图片缓入缓出
	* 	@param	当前时间
	* 	@param	开始
	* 	@param	结束
	* 	@param	持续时间
	* */
	double easeInOut(double time, double start, double end, double duration);
}
