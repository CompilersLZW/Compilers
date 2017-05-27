package com.imagezoom.easing;

/**
* 	@brief	立方体效果
*	@author:binlee
 *	@date 2016年12月24日
*/
public class Cubic implements Easing {

	@Override
	public double easeOut( double time, double start, double end, double duration ) {
		return end * ( ( time = time / duration - 1.0 ) * time * time + 1.0 ) + start;
	}

	@Override
	public double easeIn( double time, double start, double end, double duration ) {
		return end * ( time /= duration ) * time * time + start;
	}

	@Override
	public double easeInOut( double time, double start, double end, double duration ) {
		if ( ( time /= duration / 2.0 ) < 1.0 ) return end / 2.0 * time * time * time + start;
		return end / 2.0 * ( ( time -= 2.0 ) * time * time + 2.0 ) + start;
	}
}
