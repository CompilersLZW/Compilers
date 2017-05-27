package com.imagezoom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.imagezoom.easing.Cubic;
import com.imagezoom.easing.Easing;
import com.imagezoom.graphics.FastBitmapDrawable;
import com.imagezoom.utils.IDisposable;

/**
 * 对于管理图片的缩放、卷轴、裁剪的基础视图
 * @author binlee
 * @date 2016年11月28日
 */
public abstract class ImageViewTouchBase extends ImageView implements IDisposable {

    //版本名称
    public static final String VERSION = "1.0.5-SNAPSHOT";

    /**
     *  定义接口
     *  对于绘制改变时的监听
     */
    public interface OnDrawableChangeListener {

        /**
         * 当有一个绘制发生在视图上时
         * 要求调用返回监听
         * @param drawable  绘制
         */
        void onDrawableChanged(Drawable drawable);
    };

    public interface OnLayoutChangeListener {
        /**
         * 当布局范围内发生改变时唤醒调用
         * @param changed   是否改变
         * @param left
         * @param top
         * @param right
         * @param bottom
         */
        void onLayoutChanged(boolean changed, int left, int top, int right, int bottom);
    };

    /**
     * 使用这个去改变 {@link ImageViewTouchBase#setDisplayType(DisplayType)} 链接的
     * 视图
     * @author binlee
     *
     */
    public enum DisplayType {
        /**图片不是默认的尺寸比例*/
        NONE,
        /** 使用这个参数图片将经常跳转呈现出来 */
        FIT_TO_SCREEN,
        /** 如果图片过大，图片将自动适应当前视图 */
        FIT_IF_BIGGER
    };

    //视图名称
    public static final String       LOG_TAG                    = "ImageViewTouchBase";
    //图片是否被正确加载，默认是
    protected static final boolean   LOG_ENABLED                = true;

    //变焦距离，默认不可用
    public static final float        ZOOM_INVALID               = -1f;

    //绘制立方体
    protected Easing                 mEasing                    = new Cubic();
    //绘制基本矩形
    protected Matrix                 mBaseMatrix                = new Matrix();
    //绘制超级矩形
    protected Matrix                 mSuppMatrix                = new Matrix();
    //绘制下一步矩形，默认不绘制
    protected Matrix                 mNextMatrix;
    //绘制句柄
    protected Handler                mHandler                   = new Handler();

    //默认开始不运行
    protected Runnable               mLayoutRunnable            = null;
    //使用者操作图片比例是否修改，默认不修改
    protected boolean                mUserScaled                = false;

    private float                    mMaxZoom                   = ZOOM_INVALID;
    private float                    mMinZoom                   = ZOOM_INVALID;

    //有明确的最大最小的定义时，默认TRUE
    private boolean                  mMaxZoomDefined;
    private boolean                  mMinZoomDefined;

    //展示图片绘制
    protected final Matrix           mDisplayMatrix             = new Matrix();
    //绘制的初始值
    protected final float[]          mMatrixValues              = new float[9];

    //绘制的宽度
    private int                      mThisWidth                 = -1;
    //高度
    private int                      mThisHeight                = -1;
    //绘制起点
    private PointF                   mCenter                    = new PointF();

    //默认绘制的类型
    protected DisplayType            mScaleType                 = DisplayType.NONE;
    //所选类型是否改变
    private boolean                  mScaleTypeChanged;
    //位图是否已经改变
    private boolean                  mBitmapChanged;

    //持续控制动画默认值200
    final protected int              DEFAULT_ANIMATION_DURATION = 200;

    //绘制不同类型矩形
    protected RectF                  mBitmapRect                = new RectF();
    protected RectF                  mCenterRect                = new RectF();
    protected RectF                  mScrollRect                = new RectF();

    //绘制改变监听
    private OnDrawableChangeListener mDrawableChangeListener;
    private OnLayoutChangeListener   mOnLayoutChangeListener;

    /**
     *  @brief  图片视图按钮基本控件
     *  @param  背景
     */
    public ImageViewTouchBase(Context context) {
        this(context, null);
    }

    /**
     *  @brief  图片视图按钮基本控件
     *  @param  背景
     *  @param  属性设置
     */
    public ImageViewTouchBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     *  @brief  图片视图按钮基本控件
     *  @param  背景
     *  @param  属性设置
     *  @param  默认类型
     */
    public ImageViewTouchBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //初始化
        init(context, attrs, defStyle);
    }

    /**
     *  @brief  绘制监听
     *  @param  监听者
     */
    public void setOnDrawableChangedListener(OnDrawableChangeListener listener) {
        mDrawableChangeListener = listener;
    }

    public void setOnLayoutChangeListener(OnLayoutChangeListener listener) {
        mOnLayoutChangeListener = listener;
    }

    protected void init(Context context, AttributeSet attrs, int defStyle) {
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(scaleType);
        } else {
            Log.w(LOG_TAG, "Unsupported scaletype. Only MATRIX can be used");
        }
    }

    /**
     * @brief   清除当前绘制
     */
    public void clear() {
        setImageBitmap(null);
    }

    /**
     * @brief   改变绘制类型
     * @param   展现类型
     */
    public void setDisplayType(DisplayType type) {
        if (type != mScaleType) {
            if (LOG_ENABLED) {
                Log.i(LOG_TAG, "setDisplayType: " + type);
            }
            mUserScaled = false;
            mScaleType = type;
            mScaleTypeChanged = true;
            requestLayout();
        }
    }

    /**
     *  @brief  得到展现的类型
     */
    public DisplayType getDisplayType() {
        return mScaleType;
    }

    /**
     *  @brief  这是最小的规格
     *  @param  设置值
     */
    protected void setMinScale(float value) {
        if (LOG_ENABLED) {
            Log.d(LOG_TAG, "setMinZoom: " + value);
        }

        mMinZoom = value;
    }

    /**
     *  @brief  这是最大的规格
     *  @param  设置值
     */
    protected void setMaxScale(float value) {
        if (LOG_ENABLED) {
            Log.d(LOG_TAG, "setMaxZoom: " + value);
        }
        mMaxZoom = value;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //System.out.println("=========> onLayout: " + changed + ", bitmapChanged: " + mBitmapChanged + ", scaleChanged: " + mScaleTypeChanged);
        //System.out.println("==========>onLayout  "+changed+"  "+getMeasuredWidth()+"   "+getMeasuredHeight());

        super.onLayout(changed, left, top, right, bottom);

        int deltaX = 0;
        int deltaY = 0;

        if (changed) {
            //改变前的尺寸
            int oldw = mThisWidth;
            int oldh = mThisHeight;

            mThisWidth = right - left;
            mThisHeight = bottom - top;

            deltaX = mThisWidth - oldw;
            deltaY = mThisHeight - oldh;

            // update center point
            mCenter.x = mThisWidth / 2f;
            mCenter.y = mThisHeight / 2f;
        }

        Runnable r = mLayoutRunnable;

        if (r != null) {
            mLayoutRunnable = null;
            r.run();
        }

        //是否可以绘制
        final Drawable drawable = getDrawable();

        if (drawable != null) {

            if (changed || mScaleTypeChanged || mBitmapChanged) {

                float scale = 1;

                // 返回改变前的规格
                float old_default_scale = getDefaultScale(mScaleType);
                float old_matrix_scale = getScale(mBaseMatrix);
                float old_scale = getScale();
                float old_min_scale = Math.min(1f, 1f / old_matrix_scale);

                getProperBaseMatrix(drawable, mBaseMatrix);

                float new_matrix_scale = getScale(mBaseMatrix);

                //给监听者输出改变的信息
                if (LOG_ENABLED) {
                    Log.d(LOG_TAG, "old matrix scale: " + old_matrix_scale);
                    Log.d(LOG_TAG, "new matrix scale: " + new_matrix_scale);
                    Log.d(LOG_TAG, "old min scale: " + old_min_scale);
                    Log.d(LOG_TAG, "old scale: " + old_scale);
                }

                // 1. 位图改变或图片类型的改变
                if (mBitmapChanged || mScaleTypeChanged) {

                    if (LOG_ENABLED) {
                        Log.d(LOG_TAG, "display type: " + mScaleType);
                        Log.d(LOG_TAG, "newMatrix: " + mNextMatrix);
                    }

                    if (mNextMatrix != null) {
                        mSuppMatrix.set(mNextMatrix);
                        mNextMatrix = null;
                        scale = getScale();
                    } else {
                        mSuppMatrix.reset();
                        scale = getDefaultScale(mScaleType);
                    }

                    setImageMatrix(getImageViewMatrix());

                    if (scale != getScale()) {  //尺寸改变不相等
                        zoomTo(scale);          //恢复默认设置
                    }

                } else if (changed) {

                    // 2. 默认尺寸改变

                    if (!mMinZoomDefined)
                        mMinZoom = ZOOM_INVALID;
                    if (!mMaxZoomDefined)
                        mMaxZoom = ZOOM_INVALID;

                    setImageMatrix(getImageViewMatrix());
                    postTranslate(-deltaX, -deltaY);

                    if (!mUserScaled) {
                        scale = getDefaultScale(mScaleType);
                        zoomTo(scale);
                    } else {
                        if (Math.abs(old_scale - old_min_scale) > 0.001) {
                            scale = (old_matrix_scale / new_matrix_scale) * old_scale;
                        }
                        zoomTo(scale);
                    }

                    if (LOG_ENABLED) {
                        Log.d(LOG_TAG, "old min scale: " + old_default_scale);
                        Log.d(LOG_TAG, "old scale: " + old_scale);
                        Log.d(LOG_TAG, "new scale: " + scale);
                    }

                }

                mUserScaled = false;

                if (scale > getMaxScale() || scale < getMinScale()) {
                    // 如果当前尺寸超出最大或最小范围
                    // 重新设置当前设置
                    zoomTo(scale);
                }

                //中心
                center(true, true);

                if (mBitmapChanged)
                    onDrawableChanged(drawable);
                if (changed || mBitmapChanged || mScaleTypeChanged)
                    onLayoutChanged(left, top, right, bottom);

                if (mScaleTypeChanged)
                    mScaleTypeChanged = false;
                if (mBitmapChanged)
                    mBitmapChanged = false;

                if (LOG_ENABLED) {
                    Log.d(LOG_TAG, "new scale: " + getScale());
                }
            }
        } else {              // 绘制为空
            //位图改变
            if (mBitmapChanged)
                onDrawableChanged(drawable);
            //尺寸改变
            if (changed || mBitmapChanged || mScaleTypeChanged)
                onLayoutChanged(left, top, right, bottom);

            if (mBitmapChanged)
                mBitmapChanged = false;
            if (mScaleTypeChanged)
                mScaleTypeChanged = false;
        }
    }

    /**
     * @brief   重置原来的显示
     */
    public void resetDisplay() {
        //改变标记，显示并没有改变
        mBitmapChanged = true;
        requestLayout();
    }

    /**
     * @brief   重置原来的矩形
     */
    public void resetMatrix() {
        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "resetMatrix");
        }
        mSuppMatrix = new Matrix();

        //得到默认尺寸
        float scale = getDefaultScale(mScaleType);
        setImageMatrix(getImageViewMatrix());

        if (LOG_ENABLED) {
            Log.d(LOG_TAG, "default scale: " + scale + ", scale: " + getScale());
        }
        //尺寸已改变改变
        if (scale != getScale()) {
            zoomTo(scale);
        }

        //刷新界面
        postInvalidate();
    }

    protected float getDefaultScale(DisplayType type) {
        if (type == DisplayType.FIT_TO_SCREEN) {
            // 经常适应的屏幕
            return 1f;
        } else if (type == DisplayType.FIT_IF_BIGGER) {
            // 正常尺寸过小，适应屏幕大小
            return Math.min(1f, 1f / getScale(mBaseMatrix));
        } else {
            // 无尺寸
            return 1f / getScale(mBaseMatrix);
        }
    }

    @Override
    /**
     * @brief   设置照片环境
     */
    public void setImageResource(int resId) {
        setImageDrawable(getContext().getResources().getDrawable(resId));
    }

    /**
     * 以下{@inheritDoc}的作用是说明一个方法实现一个interface
     * {@inheritDoc} 设置一张新的图片用于展示，并且重置位置中心
     * 
     * @param bitmap
     *           the {@link Bitmap} 用于展示
     * @see {@link ImageView#setImageBitmap(Bitmap)}
     */
    @Override
    public void setImageBitmap(final Bitmap bitmap) {
        setImageBitmap(bitmap, null, ZOOM_INVALID, ZOOM_INVALID);
    }

    /**
     * 以下@see的作用是链接目标
     * @see #setImageDrawable(Drawable, Matrix, float, float)
     * 
     * @param bitmap    位图
     * @param matrix    3*3的放射矩阵
     * @param min_zoom  最小尺寸
     * @param max_zoom  最大尺寸
     *
     */
    public void setImageBitmap(final Bitmap bitmap, Matrix matrix, float min_zoom, float max_zoom) {
        //用于输出显示测试输入的位图的大小是否符合要求
        //当输入图形为空时会报错
        //System.out.println("setImageBitmap=========>  "+bitmap.getWidth()+"  "+bitmap.getHeight());
        if (bitmap != null)
            setImageDrawable(new FastBitmapDrawable(bitmap), matrix, min_zoom, max_zoom);
        else
            setImageDrawable(null, matrix, min_zoom, max_zoom);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        setImageDrawable(drawable, null, ZOOM_INVALID, ZOOM_INVALID);
    }

    /**
     *
     * Note: 如果尺寸类型是适合当前屏幕的，则必须有 min_zoom <= 1 and max_zoom >= 1
     * 
     * @param drawable
     *           新的绘制
     * @param initial_matrix
     *           可选择初始显示的尺寸
     * @param min_zoom
     *           可选择最小比例，通过 {@link #ZOOM_INVALID} 链接到默认的min_zoom
     * @param max_zoom
     *           可选择最大比例，通过 {@link #ZOOM_INVALID} 链接到默认的max_zoom
     */
    public void setImageDrawable(final Drawable drawable, final Matrix initial_matrix,
                                 final float min_zoom, final float max_zoom) {

        final int viewWidth = getWidth();

        if (viewWidth <= 0) {
            mLayoutRunnable = new Runnable() {

                @Override
                //重写运行
                public void run() {
                    setImageDrawable(drawable, initial_matrix, min_zoom, max_zoom);
                }
            };
            return;
        }
        _setImageDrawable(drawable, initial_matrix, min_zoom, max_zoom);
    }

    protected void _setImageDrawable(final Drawable drawable, final Matrix initial_matrix,
                                     float min_zoom, float max_zoom) {

        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "_setImageDrawable");
        }

        if (drawable != null) {

            if (LOG_ENABLED) {
                Log.d(LOG_TAG,
                    "size: " + drawable.getIntrinsicWidth() + "x" + drawable.getIntrinsicHeight());
            }
            super.setImageDrawable(drawable);
        } else {
            mBaseMatrix.reset();
            super.setImageDrawable(null);
        }

        if (min_zoom != ZOOM_INVALID && max_zoom != ZOOM_INVALID) {
            min_zoom = Math.min(min_zoom, max_zoom);
            max_zoom = Math.max(min_zoom, max_zoom);

            mMinZoom = min_zoom;
            mMaxZoom = max_zoom;

            mMinZoomDefined = true;
            mMaxZoomDefined = true;

            //适合当前尺寸或者适合更大情况
            if (mScaleType == DisplayType.FIT_TO_SCREEN || mScaleType == DisplayType.FIT_IF_BIGGER) {

                //最小值不符合
                if (mMinZoom >= 1) {
                    mMinZoomDefined = false;
                    mMinZoom = ZOOM_INVALID;
                }

                //最大值不符合
                if (mMaxZoom <= 1) {
                    mMaxZoomDefined = true;
                    mMaxZoom = ZOOM_INVALID;
                }
            }
        } else {//置为默认值
            mMinZoom = ZOOM_INVALID;
            mMaxZoom = ZOOM_INVALID;

            //最大最小值未被定义
            mMinZoomDefined = false;
            mMaxZoomDefined = false;
        }

        if (initial_matrix != null) {       //初始化下一个矩形
            mNextMatrix = new Matrix(initial_matrix);
        }

        mBitmapChanged = true;
        requestLayout();
    }

    /**
     * 当一个新位图被设置时触发
     * 
     * @param drawable
     * @author binlee
     */
    protected void onDrawableChanged(final Drawable drawable) {
        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "onDrawableChanged");
        }
        fireOnDrawableChangeListener(drawable);
    }

    /**
     *@brief 触发监听
     *
     * @param   left
     * @param   top
     * @param   right
     * @param   buttom
     */
    protected void fireOnLayoutChangeListener(int left, int top, int right, int bottom) {
        if (null != mOnLayoutChangeListener) {
            mOnLayoutChangeListener.onLayoutChanged(true, left, top, right, bottom);
        }
    }

    protected void fireOnDrawableChangeListener(Drawable drawable) {
        if (null != mDrawableChangeListener) {
            mDrawableChangeListener.onDrawableChanged(drawable);
        }
    }

    /**
     * Called just after {@link #onLayout(boolean, int, int, int, int)}
     * if the view's bounds has changed or a new Drawable has been set
     * or the {@link DisplayType} has been modified 
     * 
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    protected void onLayoutChanged(int left, int top, int right, int bottom) {
        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "onLayoutChanged");
        }
        fireOnLayoutChangeListener(left, top, right, bottom);
    }

    /**
     * @brief   计算最大尺寸
     *
     * @return scale
     */
    protected float computeMaxZoom() {
        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return 1F;
        }

        float fw = (float) drawable.getIntrinsicWidth() / (float) mThisWidth;
        float fh = (float) drawable.getIntrinsicHeight() / (float) mThisHeight;
        float scale = Math.max(fw, fh) * 8;

        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "computeMaxZoom: " + scale);
        }
        return scale;
    }

    /**
     * @brief   计算最小尺寸
     *
     * @return scale
     */
    protected float computeMinZoom() {
        final Drawable drawable = getDrawable();

        if (drawable == null) {
            return 1F;
        }

        float scale = getScale(mBaseMatrix);
        scale = Math.min(1f, 1f / scale);

        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "computeMinZoom: " + scale);
        }

        return scale;
    }

    /**
     * 得到当前图片允许的最大规格
     * 
     * @return
     */
    public float getMaxScale() {
        if (mMaxZoom == ZOOM_INVALID) {
            mMaxZoom = computeMaxZoom();
        }
        return mMaxZoom;
    }

    /**
     * 得到当前图片允许的最小规格
     * 
     * @return
     */
    public float getMinScale() {
        if (mMinZoom == ZOOM_INVALID) {
            mMinZoom = computeMinZoom();
        }
        return mMinZoom;
    }

    /**
     * 得到当前视图的矩阵仿射变换
     * 
     * @return
     */
    public Matrix getImageViewMatrix() {
        return getImageViewMatrix(mSuppMatrix);
    }

    /**
     * 得到当前视图的矩阵仿射变换
     *
     * @param 支持的矩阵规格
     * @return
     */
    public Matrix getImageViewMatrix(Matrix supportMatrix) {
        //根据基本矩阵大小设置尺寸
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(supportMatrix);
        return mDisplayMatrix;
    }

    @Override
    public void setImageMatrix(Matrix matrix) {

        Matrix current = getImageMatrix();
        boolean needUpdate = false;

        if (matrix == null && !current.isIdentity() || matrix != null && !current.equals(matrix)) {
            needUpdate = true;
        }

        super.setImageMatrix(matrix);

        if (needUpdate)
            onImageMatrixChanged();
    }

    /**
     * 在一个新的矩阵被分配之后唤醒
     * 
     * @see {@link #setImageMatrix(Matrix)}
     */
    protected void onImageMatrixChanged() {
    }

    /**
     * Returns the current image display matrix.<br />
     * This matrix can be used in the next call to the
     * {@link #setImageDrawable(Drawable, Matrix, float, float)} to restore the same
     * view state of the previous {@link Bitmap}.<br />
     * Example:
     * 
     * <pre>
     * Matrix currentMatrix = mImageView.getDisplayMatrix();
     * mImageView.setImageBitmap( newBitmap, currentMatrix, ZOOM_INVALID, ZOOM_INVALID );
     * </pre>
     * 
     * @return 当前支持的矩阵
     */
    public Matrix getDisplayMatrix() {
        return new Matrix(mSuppMatrix);
    }

    /**
     * 为了图片的中心和尺寸适当，设置基本的矩阵
     * 
     * @param drawable
     * @param matrix
     */
    protected void getProperBaseMatrix(Drawable drawable, Matrix matrix) {
        float viewWidth = mThisWidth;
        float viewHeight = mThisHeight;

        if (LOG_ENABLED) {
            Log.d(LOG_TAG, "getProperBaseMatrix. view: " + viewWidth + "x" + viewHeight);
        }

        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();
        //屏幕的宽度和高度
        float widthScale, heightScale;
        matrix.reset();

        if (w > viewWidth || h > viewHeight) {  //绘制的尺寸大于屏幕的尺寸
            //将尺寸缩小
            widthScale = viewWidth / w;
            heightScale = viewHeight / h;
            float scale = Math.min(widthScale, heightScale);
            matrix.postScale(scale, scale);

            float tw = (viewWidth - w * scale) / 2.0f;
            float th = (viewHeight - h * scale) / 2.0f;
            matrix.postTranslate(tw, th);

        } else {
            widthScale = viewWidth / w;
            heightScale = viewHeight / h;
            float scale = Math.min(widthScale, heightScale);
            matrix.postScale(scale, scale);

            float tw = (viewWidth - w * scale) / 2.0f;
            float th = (viewHeight - h * scale) / 2.0f;
            matrix.postTranslate(tw, th);
        }

        if (LOG_ENABLED) {
            printMatrix(matrix);
        }
    }

    /**
     *
     * Setup the base matrix so that the image is centered and scaled properly.
     * 
     * @param bitmap
     * @param matrix
     */
    protected void getProperBaseMatrix2(Drawable bitmap, Matrix matrix) {

        float viewWidth = mThisWidth;
        float viewHeight = mThisHeight;

        float w = bitmap.getIntrinsicWidth();
        float h = bitmap.getIntrinsicHeight();

        matrix.reset();

        float widthScale = viewWidth / w;
        float heightScale = viewHeight / h;

        float scale = Math.min(widthScale, heightScale);

        matrix.postScale(scale, scale);
        matrix.postTranslate((viewWidth - w * scale) / 2.0f, (viewHeight - h * scale) / 2.0f);
    }

    /**
     *@brief 得到当前矩阵的值
     *
     * @param matrix
     * @param whichValue
     */
    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    /**
     *@brief 输出矩阵信息
     *
     * @param matrix
     */
    public void printMatrix(Matrix matrix) {
        float scalex = getValue(matrix, Matrix.MSCALE_X);
        float scaley = getValue(matrix, Matrix.MSCALE_Y);
        float tx = getValue(matrix, Matrix.MTRANS_X);
        float ty = getValue(matrix, Matrix.MTRANS_Y);
        Log.d(LOG_TAG, "matrix: { x: " + tx + ", y: " + ty + ", scalex: " + scalex + ", scaley: "
                       + scaley + " }");
    }

    /**
     * @brief 得到位图的矩形信息
     *
     * @return
     */
    public RectF getBitmapRect() {
        return getBitmapRect(mSuppMatrix);
    }

    /**
     * @brief 得到位图的矩形信息
     *
     * @param supportMatrix 支持的矩阵
     * @return
     */
    protected RectF getBitmapRect(Matrix supportMatrix) {
        final Drawable drawable = getDrawable();

        if (drawable == null)
            return null;
        Matrix m = getImageViewMatrix(supportMatrix);
        mBitmapRect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        m.mapRect(mBitmapRect);
        return mBitmapRect;
    }

    protected float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    @SuppressLint("Override")
    public float getRotation() {
        return 0;
    }

    /**
     * 得到当前图片的尺寸
     * 
     * @return
     */
    public float getScale() {
        return getScale(mSuppMatrix);
    }

    public float getBaseScale() {
        return getScale(mBaseMatrix);
    }

    /**
     * @brief 设置中心
     */
    public void center(boolean horizontal, boolean vertical) {
        final Drawable drawable = getDrawable();
        if (drawable == null)
            return;

        RectF rect = getCenter(mSuppMatrix, horizontal, vertical);

        if (rect.left != 0 || rect.top != 0) {

            if (LOG_ENABLED) {
                Log.i(LOG_TAG, "center");
            }
            postTranslate(rect.left, rect.top);
        }
    }

    /**
     * @brief  得到得到中心位置
     *
     * @param supportMatrix
     * @param horizontal    水平方向
     * @param vertical      垂直方向
     * @return
     */
    protected RectF getCenter(Matrix supportMatrix, boolean horizontal, boolean vertical) {
        final Drawable drawable = getDrawable();

        if (drawable == null)
            return new RectF(0, 0, 0, 0);

        mCenterRect.set(0, 0, 0, 0);
        RectF rect = getBitmapRect(supportMatrix);
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;
        if (vertical) {     //垂直方向
            int viewHeight = mThisHeight;
            if (height < viewHeight) {
                deltaY = (viewHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < viewHeight) {
                deltaY = mThisHeight - rect.bottom;
            }
        }
        if (horizontal) {       //水平方向
            int viewWidth = mThisWidth;
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
            }
        }
        mCenterRect.set(deltaX, deltaY, 0, 0);
        return mCenterRect;
    }

    /**
     * @brief   布置
     */
    public void postTranslate(float deltaX, float deltaY) {
        if (deltaX != 0 || deltaY != 0) {
            if (LOG_ENABLED) {
                Log.i(LOG_TAG, "postTranslate: " + deltaX + "x" + deltaY);
            }
            mSuppMatrix.postTranslate(deltaX, deltaY);
            setImageMatrix(getImageViewMatrix());
        }
    }

    /**
     * @brief 布置缩放
     */
    protected void postScale(float scale, float centerX, float centerY) {
        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "postScale: " + scale + ", center: " + centerX + "x" + centerY);
        }
        mSuppMatrix.postScale(scale, scale, centerX, centerY);
        setImageMatrix(getImageViewMatrix());
    }

    public PointF getCenter() {
        return mCenter;
    }

    /**
     * @brief 设置图片缩放级别
     *
     * @param scale
     **/
    protected void zoomTo(float scale) {
        if (LOG_ENABLED) {
            Log.i(LOG_TAG, "zoomTo: " + scale);
        }

        if (scale > getMaxScale())
            scale = getMaxScale();
        if (scale < getMinScale())
            scale = getMinScale();

        if (LOG_ENABLED) {
            Log.d(LOG_TAG, "sanitized scale: " + scale);
        }

        PointF center = getCenter();
        zoomTo(scale, center.x, center.y);
    }

    /**
     * 缩放到目标尺寸
     * 
     * @param scale
     *           the target zoom
     * @param durationMs
     *           动画持续
     */
    public void zoomTo(float scale, float durationMs) {
        PointF center = getCenter();
        zoomTo(scale, center.x, center.y, durationMs);
    }

    protected void zoomTo(float scale, float centerX, float centerY) {
        if (scale > getMaxScale())
            scale = getMaxScale();

        float oldScale = getScale();
        float deltaScale = scale / oldScale;
        postScale(deltaScale, centerX, centerY);
        onZoom(getScale());
        center(true, true);
    }

    protected void onZoom(float scale) {
    }

    protected void onZoomAnimationCompleted(float scale) {
    }

    /**
     * 将视图便宜x和y距离
     *
     * @param x
     * @param y
     */
    public void scrollBy(float x, float y) {
        panBy(x, y);
    }

    protected void panBy(double dx, double dy) {
        RectF rect = getBitmapRect();
        mScrollRect.set((float) dx, (float) dy, 0, 0);
        updateRect(rect, mScrollRect);
        //FIXME 贴纸移动到边缘次数多了以后会爆,原因不明朗  。后续需要好好重写ImageViewTouch
        postTranslate(mScrollRect.left, mScrollRect.top);
        center(true, true);
    }

    /**
     * 更新矩形
     *
     * @param bitmapRect 位图矩形
     * @param scrollRect 移动到的目标位置
     */
    protected void updateRect(RectF bitmapRect, RectF scrollRect) {
        if (bitmapRect == null)
            return;

        if (bitmapRect.top >= 0 && bitmapRect.bottom <= mThisHeight)
            scrollRect.top = 0;
        if (bitmapRect.left >= 0 && bitmapRect.right <= mThisWidth)
            scrollRect.left = 0;
        if (bitmapRect.top + scrollRect.top >= 0 && bitmapRect.bottom > mThisHeight)
            scrollRect.top = (int) (0 - bitmapRect.top);
        if (bitmapRect.bottom + scrollRect.top <= (mThisHeight - 0) && bitmapRect.top < 0)
            scrollRect.top = (int) ((mThisHeight - 0) - bitmapRect.bottom);
        if (bitmapRect.left + scrollRect.left >= 0)
            scrollRect.left = (int) (0 - bitmapRect.left);
        if (bitmapRect.right + scrollRect.left <= (mThisWidth - 0))
            scrollRect.left = (int) ((mThisWidth - 0) - bitmapRect.right);
    }

    public void scrollBy(float distanceX, float distanceY, final double durationMs) {
        final double dx = distanceX;
        final double dy = distanceY;
        final long startTime = System.currentTimeMillis();
        mHandler.post(new Runnable() {

            double old_x = 0;
            double old_y = 0;

            @Override
            public void run() {
                long now = System.currentTimeMillis();
                double currentMs = Math.min(durationMs, now - startTime);
                double x = mEasing.easeOut(currentMs, 0, dx, durationMs);
                double y = mEasing.easeOut(currentMs, 0, dy, durationMs);
                panBy((x - old_x), (y - old_y));
                old_x = x;
                old_y = y;
                if (currentMs < durationMs) {
                    mHandler.post(this);
                } else {
                    RectF centerRect = getCenter(mSuppMatrix, true, true);
                    if (centerRect.left != 0 || centerRect.top != 0)
                        scrollBy(centerRect.left, centerRect.top);
                }
            }
        });
    }

    protected void zoomTo(float scale, float centerX, float centerY, final float durationMs) {
        if (scale > getMaxScale())
            scale = getMaxScale();

        final long startTime = System.currentTimeMillis();
        final float oldScale = getScale();

        final float deltaScale = scale - oldScale;

        Matrix m = new Matrix(mSuppMatrix);
        m.postScale(scale, scale, centerX, centerY);
        RectF rect = getCenter(m, true, true);

        final float destX = centerX + rect.left * scale;
        final float destY = centerY + rect.top * scale;

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, now - startTime);
                float newScale = (float) mEasing.easeInOut(currentMs, 0, deltaScale, durationMs);
                zoomTo(oldScale + newScale, destX, destY);
                if (currentMs < durationMs) {
                    mHandler.post(this);
                } else {
                    center(true, true);
                    onZoomAnimationCompleted(getScale());
                }
            }
        });
    }

    @Override
    public void dispose() {
        clear();
    }
}