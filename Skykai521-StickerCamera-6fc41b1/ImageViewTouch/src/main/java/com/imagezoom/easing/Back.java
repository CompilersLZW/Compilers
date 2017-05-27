package com.imagezoom.easing;

/**
* 	@brief	图片背景
* 	@author:binlee
*   @date 2016年11月24日
*/

public class Back implements Easing {

	@Override
	public double easeOut( double time, double start, double end, double duration ) {
		return easeOut( time, start, end, duration, 0 );
	}

	@Override
	public double easeIn( double time, double start, double end, double duration ) {
		return easeIn( time, start, end, duration, 0 );
	}

	@Override
	public double easeInOut( double time, double start, double end, double duration ) {
		return easeInOut( time, start, end, duration, 0.9 );
	}

	//	@param	s	修正参数
	public double easeIn( double t, double b, double c, double d, double s ) {
		if ( s == 0 ) s = 1.70158;
		return c * ( t /= d ) * t * ( ( s + 1 ) * t - s ) + b;
	}

	//	@param	s	修正参数
	public double easeOut( double t, double b, double c, double d, double s ) {
		if ( s == 0 ) s = 1.70158;
		return c * ( ( t = t / d - 1 ) * t * ( ( s + 1 ) * t + s ) + 1 ) + b;
	}

	//	@param	s	修正参数
	public double easeInOut( double t, double b, double c, double d, double s ) {
		if ( s == 0 ) s = 1.70158;
		if ( ( t /= d / 2 ) < 1 ) return c / 2 * ( t * t * ( ( ( s *= ( 1.525 ) ) + 1 ) * t - s ) ) + b;
		return c / 2 * ( ( t -= 2 ) * t * ( ( ( s *= ( 1.525 ) ) + 1 ) * t + s ) + 2 ) + b;
	}
}
