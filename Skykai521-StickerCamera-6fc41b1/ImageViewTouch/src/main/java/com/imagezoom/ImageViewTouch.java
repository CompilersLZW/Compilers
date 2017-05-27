package com.imagezoom;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ViewConfiguration;

/**
 * 快速绘制二值图，不支持状态，只支持
 * alpha 和 colormatrix
 * @author binlee
 * @date 2016年11月24日
 */

/**参考资料：
 * android的ScaleGestureDetector缩放类详解:
 * http://www.cnblogs.com/crazywenza/archive/2012/12/03/2799679.html
 * 实现触摸滑屏以及Scroller类详解:
 * http://blog.csdn.net/qinjuning/article/details/7419207
 * 手势识别：
 * https://yq.aliyun.com/articles/23781
 */
//  ImageViewTouch 继承于ImageViewTouchBase类
public class ImageViewTouch extends ImageViewTouchBase {

    /**卷积delta阈值*/
    static final float                        SCROLL_DELTA_THRESHOLD = 1.0f;
    /**触控缩放*/
    protected ScaleGestureDetector            mScaleDetector;
    /**手势监控*/
    protected GestureDetector                 mGestureDetector;
    /**触控超出*/
    protected int                             mTouchSlop;
    /**缩放因子*/
    protected float                           mScaleFactor;
    /**手指双击直接触发*/
    protected int                             mDoubleTapDirection;
    /**手势监听者*/
    protected OnGestureListener               mGestureListener;
    /**缩放监听者*/
    protected OnScaleGestureListener          mScaleListener;
    /**手指是否双击*/
    protected boolean                         mDoubleTapEnabled      = true;
    /**可以缩放*/
    protected boolean                         mScaleEnabled          = true;
    /**可以滚动*/
    protected boolean                         mScrollEnabled         = true;
    /**视图上手指双击监听*/
    private OnImageViewTouchDoubleTapListener mDoubleTapListener;
    /**视图上手指单击监听*/
    private OnImageViewTouchSingleTapListener mSingleTapListener;
    /**缩放监听*/
    private OnZoomAnimationListener           onZoomAnimationListener;

    /**
     * 图片视图上点击
     *
     * @param context
     */
    public ImageViewTouch(Context context) {
        super(context);
    }

    /**
     * 图片视图上点击
     *
     * @param context
     * @param attrs
     */
    public ImageViewTouch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 图片视图上点击
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ImageViewTouch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mGestureListener = getGestureListener();
        mScaleListener = getScaleListener();

        mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
        mGestureDetector = new GestureDetector(getContext(), mGestureListener, null, true);

        mDoubleTapDirection = 1;
    }

    /**
     *设置双击监听
     *
     * @param listener
     */
    public void setDoubleTapListener(OnImageViewTouchDoubleTapListener listener) {
        mDoubleTapListener = listener;
    }

    /**
     *设置点击监听
     *
     * @param listener
     */
    public void setSingleTapListener(OnImageViewTouchSingleTapListener listener) {
        mSingleTapListener = listener;
    }

    /**
     *设置双击监听
     *
     * @param value  双击是否发生
     */
    public void setDoubleTapEnabled(boolean value) {
        mDoubleTapEnabled = value;
    }

    /**
     *设置点击监听
     *
     * @param value  单击是否发生是否
     */
    public void setScaleEnabled(boolean value) {
        mScaleEnabled = value;
    }

    /**
     *设置滚动是否发生
     *
     * @param value
     */
    public void setScrollEnabled(boolean value) {
        mScrollEnabled = value;
    }

    /**
     *获得双击是否发生
     *
     *@return
     */
    public boolean getDoubleTapEnabled() {return mDoubleTapEnabled;}

    /**
     *获得单击是否发生
     *
     * @return
     */
    protected OnGestureListener getGestureListener() {return new GestureListener();}

    protected OnScaleGestureListener getScaleListener() {return new ScaleListener();}

    @Override
    protected void _setImageDrawable(final Drawable drawable, final Matrix initial_matrix,
                                     float min_zoom, float max_zoom) {
        super._setImageDrawable(drawable, initial_matrix, min_zoom, max_zoom);
        mScaleFactor = getMaxScale() / 3;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        if (!mScaleDetector.isInProgress()) {
            mGestureDetector.onTouchEvent(event);
        }

        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                return onUp(event);
        }
        return true;
    }


    @Override
    protected void onZoomAnimationCompleted(float scale) {

        if (LOG_ENABLED) {
            Log.d(LOG_TAG, "onZoomAnimationCompleted. scale: " + scale + ", minZoom: "
                           + getMinScale());
        }

        if (scale < getMinScale()) {
            zoomTo(getMinScale(), 50);
        }
        if (onZoomAnimationListener != null) {
            onZoomAnimationListener.onZoomAnimEnd(scale);
        }
    }

    protected float onDoubleTapPost(float scale, float maxZoom) {
        if (mDoubleTapDirection == 1) {
            if ((scale + (mScaleFactor * 2)) <= maxZoom) {
                return scale + mScaleFactor;
            } else {
                mDoubleTapDirection = -1;
                return maxZoom;
            }
        } else {
            mDoubleTapDirection = 1;
            return 1f;
        }
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (getScale() == 1f)
            return false;
        mUserScaled = true;
        scrollBy(-distanceX, -distanceY);
        invalidate();
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(velocityX) > 800 || Math.abs(velocityY) > 800) {
            mUserScaled = true;
            scrollBy(diffX / 2, diffY / 2, 300);
            invalidate();
            return true;
        }
        return false;
    }

    public boolean onDown(MotionEvent e) {
        return true;
    }

    public boolean onUp(MotionEvent e) {
        if (getScale() < getMinScale()) {
            zoomTo(getMinScale(), 50);
        }
        return true;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    /**
     * Determines whether this ImageViewTouch can be scrolled.
     * 
     * @param direction
     *            - positive direction value means scroll from right to left,
     *            negative value means scroll from left to right
     * 
     * @return true if there is some more place to scroll, false - otherwise.
     */
    public boolean canScroll(int direction) {
        RectF bitmapRect = getBitmapRect();
        updateRect(bitmapRect, mScrollRect);
        Rect imageViewRect = new Rect();
        getGlobalVisibleRect(imageViewRect);

        if (null == bitmapRect) {
            return false;
        }

        if (bitmapRect.right >= imageViewRect.right) {
            if (direction < 0) {
                return Math.abs(bitmapRect.right - imageViewRect.right) > SCROLL_DELTA_THRESHOLD;
            }
        }

        double bitmapScrollRectDelta = Math.abs(bitmapRect.left - mScrollRect.left);
        return bitmapScrollRectDelta > SCROLL_DELTA_THRESHOLD;
    }

    /**
     * 设置缩放动画监听
     * @param onZoomAnimationListener
     */
    public void setOnZoomAnimationListener(OnZoomAnimationListener onZoomAnimationListener) {
        this.onZoomAnimationListener = onZoomAnimationListener;
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (null != mSingleTapListener) {
                mSingleTapListener.onSingleTapConfirmed();
            }

            return ImageViewTouch.this.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(LOG_TAG, "onDoubleTap. double tap enabled? " + mDoubleTapEnabled);
            if (mDoubleTapEnabled) {
                mUserScaled = true;
                float scale = getScale();
                float targetScale = scale;
                targetScale = onDoubleTapPost(scale, getMaxScale());
                targetScale = Math.min(getMaxScale(), Math.max(targetScale, getMinScale()));
                zoomTo(targetScale, e.getX(), e.getY(), DEFAULT_ANIMATION_DURATION);
                invalidate();
            }

            if (null != mDoubleTapListener) {
                mDoubleTapListener.onDoubleTap();
            }

            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (isLongClickable()) {
                if (!mScaleDetector.isInProgress()) {
                    setPressed(true);
                    performLongClick();
                }
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!mScrollEnabled)
                return false;
            if (e1 == null || e2 == null)
                return false;
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
                return false;
            if (mScaleDetector.isInProgress())
                return false;
            return ImageViewTouch.this.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!mScrollEnabled)
                return false;
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
                return false;
            if (mScaleDetector.isInProgress())
                return false;
            if (getScale() == 1f)
                return false;
            return ImageViewTouch.this.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return ImageViewTouch.this.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return ImageViewTouch.this.onDown(e);
        }
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        protected boolean mScaled = false;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float span = detector.getCurrentSpan() - detector.getPreviousSpan();
            float targetScale = getScale() * detector.getScaleFactor();

            if (mScaleEnabled) {
                if (mScaled && span != 0) {
                    mUserScaled = true;
                    targetScale = Math.min(getMaxScale(),
                        Math.max(targetScale, getMinScale() - 0.1f));
                    zoomTo(targetScale, detector.getFocusX(), detector.getFocusY());
                    mDoubleTapDirection = 1;
                    invalidate();
                    return true;
                }

                //阻止第一次在图片缩放故障的发生
                if (!mScaled)
                    mScaled = true;
            }
            return true;
        }

    }

    public interface OnImageViewTouchDoubleTapListener {
        void onDoubleTap();
    }

    public interface OnImageViewTouchSingleTapListener {

        void onSingleTapConfirmed();
    }

    /**
     * 缩放动画监听
     * @author binlee
     */
    public interface OnZoomAnimationListener {
        void onZoomAnimEnd(float scale);
    }
}