package com.imagezoom.easing;

/**
* 	@brief	线性效果
*	@author:binlee
*	@date 2016年12月24日
*/
public class Linear implements Easing {

	/*
	* 	@brief	线性缓出初始
	* */
	public double easeNone( double time, double start, double end, double duration ) {
		return end * time / duration + start;
	}

	@Override
	public double easeOut( double time, double start, double end, double duration ) {
		return end * time / duration + start;
	}

	@Override
	public double easeIn( double time, double start, double end, double duration ) {
		return end * time / duration + start;
	}

	@Override
	public double easeInOut( double time, double start, double end, double duration ) {
		return end * time / duration + start;
	}

}
