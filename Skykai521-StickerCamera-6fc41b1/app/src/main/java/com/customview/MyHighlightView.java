package com.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.customview.drawable.EditableDrawable;
import com.customview.drawable.FeatherDrawable;
import com.github.skykai.stickercamera.R;
import com.imagezoom.ImageViewTouch;
import com.stickercamera.App;
import com.stickercamera.app.camera.util.Point2D;
import com.stickercamera.app.camera.util.UIUtils;

/**
 * 声明一个类MyHighlight
 */
public class MyHighlightView implements EditableDrawable.OnSizeChange {

    static final String LOG_TAG = "drawable-view";

    public static enum AlignModeV {
        Top, Bottom, Center
    };

    public interface OnDeleteClickListener {
        void onDeleteClick();
    }

    private int                   STATE_NONE                 = 1 << 0;
    //设置状态空变量
    private int                   STATE_SELECTED             = 1 << 1;
    //设置状态选择
    private int                   STATE_FOCUSED              = 1 << 2;
    //设置状态聚焦
    private OnDeleteClickListener mDeleteClickListener;
    //设置删除响应
    public static final int       NONE                       = 1 << 0;
    // 1设定空
    public static final int       GROW_LEFT_EDGE             = 1 << 1;
    // 2设定左边界
    public static final int       GROW_RIGHT_EDGE            = 1 << 2;
    // 4设定右边界
    public static final int       GROW_TOP_EDGE              = 1 << 3;
    // 8设定上边界
    public static final int       GROW_BOTTOM_EDGE           = 1 << 4;
    // 16设定下边界
    public static final int       ROTATE                     = 1 << 5;
    // 32设定旋转中心
    public static final int       MOVE                       = 1 << 6;
    // 64设置移动

    public static final int       GROW                       = GROW_TOP_EDGE | GROW_BOTTOM_EDGE
                                                               | GROW_LEFT_EDGE | GROW_RIGHT_EDGE;
    //设置成长值

    private static final float    HIT_TOLERANCE              = 40f;
    //设置点击量

    private boolean               mHidden;
    //设置小隐藏
    private int                   mMode;
    //设置模式
    private int                   mState                     = STATE_NONE;
    //设置状态
    private RectF                 mDrawRect;
    //设置回话重定位
    private final RectF           mTempRect                  = new RectF();
    //设置临时重定位
    private RectF                 mCropRect;
    //设置重定位
    private Matrix                mMatrix;
    private FeatherDrawable       mContent;
    //设置状态Content
    private EditableDrawable      mEditableContent;
    //设置编辑窗口
    private Drawable              mAnchorRotate;
    //设置固定旋转中心
    private Drawable              mAnchorDelete;
    private Drawable              mBackgroundDrawable;
    //设置背景
    private int                   mAnchorRotateWidth;
    //设置固定旋转宽度
    private int                   mAnchorRotateHeight;
    //设置固定旋转高度
    private int                   mAnchorDeleteHeight;
    //设置固定删除高度
    private int                   mAnchorDeleteWidth;
    //设置股东删除宽度
    private int                   mResizeEdgeMode;

    private boolean               mRotateEnabled;
    //设定能否旋转
    private boolean               mScaleEnabled;
    //设置角度
    private boolean               mMoveEnabled;
    //设置能否移动
    private float                 mRotation                  = 0;
    //设置旋转中心
    private float                 mRatio                     = 1f;
    //设置Ratio
    private Matrix                mRotateMatrix              = new Matrix();
    private final float           fpoints[]                  = new float[] { 0, 0 };

    private int                   mPadding                   = 0;
    private boolean               mShowAnchors               = true;
    private AlignModeV            mAlignVerticalMode         = AlignModeV.Center;
    private ImageViewTouch        mContext;

    private static final int[]    STATE_SET_NONE             = new int[] {};
    //设置静态变量 设置空
    private static final int[]    STATE_SET_SELECTED         = new int[] { android.R.attr.state_selected };
    //设置静态变量 设置选择
    private static final int[]    STATE_SET_SELECTED_PRESSED = new int[] {
            android.R.attr.state_selected, android.R.attr.state_pressed };
    //设置静态变量 设置选择重置
    private static final int[]    STATE_SET_SELECTED_FOCUSED = new int[] { android.R.attr.state_focused };
    //设置静态变量 设置选择重选
    private final Paint outlinePaint = new Paint();
    private Path outlinePath;
    //设置输出路径
    public void setMoveable(boolean moveable) {
        this.mMoveEnabled = moveable;
        //设置能否移动
    }

    public void setScaleable(boolean scaleable) {
        //设置旋转角度
        this.mScaleEnabled = scaleable;
        if (scaleable) {
            mAnchorRotate = App.getApp().getResources().getDrawable(R.drawable.aviary_resize_knob);
        } else {
            mAnchorRotate = null;
        }
    }

    public void setDeleteable(boolean deleteable) {

        //设置能否删除
        if (deleteable) {
            mAnchorDelete = App.getApp().getResources().getDrawable(R.drawable.aviary_delete_knob);
        } else {
            mAnchorDelete = null;
        }
    }

    public MyHighlightView(ImageView context, int styleId, FeatherDrawable content) {
        mContent = content;
    //设置高亮窗口
        if (content instanceof EditableDrawable) {
            mEditableContent = (EditableDrawable) content;
            mEditableContent.setOnSizeChangeListener(this);
        } else {
            mEditableContent = null;
        }

        float minSize = -1f;

        Log.i(LOG_TAG, "DrawableHighlightView. styleId: " + styleId);
        mMoveEnabled = true;
        //设置能否移动为真
        mRotateEnabled = true;
        //设置能否旋转为真
        mScaleEnabled = true;
        //设置能否衡量

        mAnchorRotate = App.getApp().getResources().getDrawable(R.drawable.aviary_resize_knob);
        mAnchorDelete = App.getApp().getResources().getDrawable(R.drawable.aviary_delete_knob);

        //如果mAnchorRotate 不为空则给将它的长和宽的数值取出
        if (null != mAnchorRotate) {
            mAnchorRotateWidth = mAnchorRotate.getIntrinsicWidth() / 2;
            mAnchorRotateHeight = mAnchorRotate.getIntrinsicHeight() / 2;
        }
        //如果mAnchorDelete就给mAnchorDeleteWidth和mAnchorDeleteHeight赋值
        if (null != mAnchorDelete) {
            mAnchorDeleteWidth = mAnchorDelete.getIntrinsicWidth() / 2;
            mAnchorDeleteHeight = mAnchorDelete.getIntrinsicHeight() / 2;
        }

        updateRatio();
        //如果最小值大于零则设置最小值
        if (minSize > 0) {
            setMinSize(minSize);
        }
    }
    //设置mAlignVerticalMode
    public void setAlignModeV(AlignModeV mode) {
        mAlignVerticalMode = mode;
    }
    //返回窗口视图
    protected RectF computeLayout() {
        return getDisplayRect(mMatrix, mCropRect);
    }
    //变量的初始值
    public void dispose() {
        mDeleteClickListener = null;
        mContext = null;
        mContent = null;
        mEditableContent = null;
    }
    //复制边界
    public void copyBounds(RectF outRect) {
        outRect.set(mDrawRect);
        outRect.inset(-mPadding, -mPadding);
    }
    //绘制函数
    public void draw(final Canvas canvas) {
        if (mHidden)
            return;

        copyBounds(mTempRect);

        final int saveCount = canvas.save();
        canvas.concat(mRotateMatrix);

        if (null != mBackgroundDrawable) {
            mBackgroundDrawable.setBounds((int) mTempRect.left, (int) mTempRect.top,
                (int) mTempRect.right, (int) mTempRect.bottom);
            mBackgroundDrawable.draw(canvas);
        }
        //设置选择和焦点的值
        boolean is_selected = isSelected();
        boolean is_focused = isFocused();
        //如果视图不为空则在其中设置边界
        if (mEditableContent != null) {
            mEditableContent.setBounds(mDrawRect.left, mDrawRect.top, mDrawRect.right,
                mDrawRect.bottom);
        } else {
            mContent.setBounds((int) mDrawRect.left, (int) mDrawRect.top, (int) mDrawRect.right,
                (int) mDrawRect.bottom);
        }
        //绘制图像
        mContent.draw(canvas);
        //如果被选择且为焦点即is_selected和is_focused为1则考试绘制图像
        if (is_selected || is_focused) {

            if (mShowAnchors) {
                //绘制边框
                outlinePath.reset();
                outlinePath.addRect(mTempRect, Path.Direction.CW);
                outlinePaint.setColor(Color.WHITE);
                outlinePaint.setStrokeWidth(App.getApp().dp2px(1));
                canvas.drawPath(outlinePath, outlinePaint);
                //设置左边界的值
                final int left = (int) (mTempRect.left);
                //设置右边界的值
                final int right = (int) (mTempRect.right);
                //设置顶部的值
                final int top = (int) (mTempRect.top);
                //设置底部的值
                final int bottom = (int) (mTempRect.bottom);
                //如果旋转中心不为空则则绘制边框
                if (mAnchorRotate != null) {
                    mAnchorRotate.setBounds(right - mAnchorRotateWidth, bottom
                                                                        - mAnchorRotateHeight,
                        right + mAnchorRotateWidth, bottom + mAnchorRotateHeight);
                    mAnchorRotate.draw(canvas);
                }
                //如果删除位置不为空则在删除位置绘制图像
                if (mAnchorDelete != null) {
                    mAnchorDelete.setBounds(left - mAnchorDeleteWidth, top - mAnchorDeleteHeight,
                        left + mAnchorDeleteWidth, top + mAnchorDeleteHeight);
                    mAnchorDelete.draw(canvas);
                }


            }
        }

        canvas.restoreToCount(saveCount);
    }
    //显示位置返回mShowAnchors的值
    public void showAnchors(boolean value) {
        mShowAnchors = value;
    }
    //绘制
    public void draw(final Canvas canvas, final Matrix source) {

        final Matrix matrix = new Matrix(source);
        matrix.invert(matrix);

        final int saveCount = canvas.save();
        canvas.concat(matrix);
        canvas.concat(mRotateMatrix);

        mContent.setBounds((int) mDrawRect.left, (int) mDrawRect.top, (int) mDrawRect.right,
            (int) mDrawRect.bottom);
        mContent.draw(canvas);

        canvas.restoreToCount(saveCount);
    }
    //生成一个矩形并返回
    public Rect getCropRect() {
        return new Rect((int) mCropRect.left, (int) mCropRect.top, (int) mCropRect.right,
            (int) mCropRect.bottom);
    }
    //返回对象中矩形的值
    public RectF getCropRectF() {
        return mCropRect;
    }
    //的获得一个可以旋转的矩形
    public Matrix getCropRotationMatrix() {
        final Matrix m = new Matrix();
        m.postTranslate(-mCropRect.centerX(), -mCropRect.centerY());
        m.postRotate(mRotation);
        m.postTranslate(mCropRect.centerX(), mCropRect.centerY());
        return m;
    }
    //生成一个用于展示的矩形并返回
    public RectF getDisplayRect(final Matrix m, final RectF supportRect) {
        final RectF r = new RectF(supportRect);
        m.mapRect(r);
        return r;
    }

    public RectF getDisplayRectF() {
        final RectF r = new RectF(mDrawRect);
        mRotateMatrix.mapRect(r);
        return r;
    }
   //获得对象中mDrawRect的值
    public RectF getDrawRect() {
        return mDrawRect;
    }
    //
    public int getHit(float x, float y) {
        //生成一个新的矩形并将对象中的数值输入
        final RectF rect = new RectF(mDrawRect);
        rect.inset(-mPadding, -mPadding);
        //生成一个float型的数组保存坐标点
        final float pts[] = new float[] { x, y };

        final Matrix rotateMatrix = new Matrix();
        rotateMatrix.postTranslate(-rect.centerX(), -rect.centerY());
        rotateMatrix.postRotate(-mRotation);
        rotateMatrix.postTranslate(rect.centerX(), rect.centerY());
        rotateMatrix.mapPoints(pts);
        //给x和y赋值
        x = pts[0];
        y = pts[1];

        int retval = NONE;
        //检查检查水平位置和垂直位置是否变化
        final boolean verticalCheck = (y >= (rect.top - HIT_TOLERANCE))
                                      && (y < (rect.bottom + HIT_TOLERANCE));
        final boolean horizCheck = (x >= (rect.left - HIT_TOLERANCE))
                                   && (x < (rect.right + HIT_TOLERANCE));

        // if horizontal and vertical checks are good then
        // at least the move edge is selected
        if (verticalCheck && horizCheck) {
            retval = MOVE;
        }

        if (mScaleEnabled) {
            //设置能否被衡量
            Log.d(LOG_TAG, "scale enabled");
            //在后台打印“能被衡量”
            if ((Math.abs(rect.left - x) < HIT_TOLERANCE) && verticalCheck
                && UIUtils.checkBits(mResizeEdgeMode, GROW_LEFT_EDGE)) {
                //打印到后台的数据“left
                Log.d(LOG_TAG, "left");
                retval |= GROW_LEFT_EDGE;
                //左边界
            }
            if ((Math.abs(rect.right - x) < HIT_TOLERANCE) && verticalCheck
                && UIUtils.checkBits(mResizeEdgeMode, GROW_RIGHT_EDGE)) {
                Log.d(LOG_TAG, "right");
                //打印到后台的数据；right
                retval |= GROW_RIGHT_EDGE;
                //右边界
            }
            if ((Math.abs(rect.top - y) < HIT_TOLERANCE) && horizCheck
                && UIUtils.checkBits(mResizeEdgeMode, GROW_TOP_EDGE)) {
                Log.d(LOG_TAG, "top");
                //打印到后台的数据top
                retval |= GROW_TOP_EDGE;
                //上边界
            }
            if ((Math.abs(rect.bottom - y) < HIT_TOLERANCE) && horizCheck
                && UIUtils.checkBits(mResizeEdgeMode, GROW_BOTTOM_EDGE)) {
                Log.d(LOG_TAG, "bottom");
                //打印到后台的数据bottom
                retval |= GROW_BOTTOM_EDGE;
                //下边界
            }
        }

        if ((mRotateEnabled || mScaleEnabled) && (Math.abs(rect.right - x) < HIT_TOLERANCE)
            && (Math.abs(rect.bottom - y) < HIT_TOLERANCE) && verticalCheck && horizCheck) {
            retval = ROTATE;
        }

        if (mMoveEnabled && (retval == NONE) && rect.contains((int) x, (int) y)) {
            retval = MOVE;
        }

        Log.d(LOG_TAG, "retValue: " + retval);

        return retval;
    }

    public void onSingleTapConfirmed(float x, float y) {
        final RectF rect = new RectF(mDrawRect);
        rect.inset(-mPadding, -mPadding);

        final float pts[] = new float[] { x, y };

        final Matrix rotateMatrix = new Matrix();
        //定义常数性变量 旋转矩阵
        rotateMatrix.postTranslate(-rect.centerX(), -rect.centerY());
        //旋转的坐标
        rotateMatrix.postRotate(-mRotation);
        //旋转的中心
        rotateMatrix.postTranslate(rect.centerX(), rect.centerY());
        //旋转中心的X，Y坐标
        rotateMatrix.mapPoints(pts);
        //旋转矩阵的地图位置

        x = pts[0];
        y = pts[1];

        // mContext.invalidate();

        final boolean verticalCheck = (y >= (rect.top - HIT_TOLERANCE))
                                      && (y < (rect.bottom + HIT_TOLERANCE));
        //设置垂直检查，y应大于top-hit 且 y 应小于bottom + hit
        final boolean horizCheck = (x >= (rect.left - HIT_TOLERANCE))
                                   && (x < (rect.right + HIT_TOLERANCE));
        //设置水平检查，y应大于left-hit 且 y 应小于right + hit
        if (mAnchorDelete != null) {
            //如果固定删除不为空
            if ((Math.abs(rect.left - x) < HIT_TOLERANCE)
                && (Math.abs(rect.top - y) < HIT_TOLERANCE) && verticalCheck && horizCheck) {
                if (mDeleteClickListener != null) {
                    mDeleteClickListener.onDeleteClick();
                }
            }
        }
    }

    RectF mInvalidateRectF = new RectF();
    Rect  mInvalidateRect  = new Rect();

    public Rect getInvalidationRect() {
        mInvalidateRectF.set(mDrawRect);
        mInvalidateRectF.inset(-mPadding, -mPadding);
        mRotateMatrix.mapRect(mInvalidateRectF);

        mInvalidateRect.set((int) mInvalidateRectF.left, (int) mInvalidateRectF.top,
            (int) mInvalidateRectF.right, (int) mInvalidateRectF.bottom);

        int w = Math.max(mAnchorRotateWidth, mAnchorDeleteWidth);
        int h = Math.max(mAnchorRotateHeight, mAnchorDeleteHeight);

        mInvalidateRect.inset(-w * 2, -h * 2);
        return mInvalidateRect;
    }

    public Matrix getMatrix() {
        return mMatrix;
        //get矩阵
    }

    public int getMode() {
        return mMode;
        //getMode模式
    }

    public float getRotation() {
        return mRotation;
        //get旋转中心
    }

    public Matrix getRotationMatrix() {
        return mRotateMatrix;
        //旋转矩阵
    }

    protected void growBy(final float dx) {
        growBy(dx, dx / mRatio, true);
    }

    protected void growBy(final float dx, final float dy, boolean checkMinSize) {
        if (!mScaleEnabled)
            return;

        final RectF r = new RectF(mCropRect);

        if (mAlignVerticalMode == AlignModeV.Center) {
            r.inset(-dx, -dy);
        } else if (mAlignVerticalMode == AlignModeV.Top) {
            r.inset(-dx, 0);
            r.bottom += dy * 2;
        } else {
            r.inset(-dx, 0);
            r.top -= dy * 2;
        }

        RectF testRect = getDisplayRect(mMatrix, r);

        if (!mContent.validateSize(testRect) && checkMinSize) {
            return;
        }

        mCropRect.set(r);
        invalidate();//使之失效
    }

    public void onMouseMove(int edge, MotionEvent event2, float dx, float dy) {
        //如果边界为空则直接返回
        if (edge == NONE) {
            return;
        }
        //设置坐标纸
        fpoints[0] = dx;
        fpoints[1] = dy;

        float xDelta;
        float yDelta;

        if (edge == MOVE) {
            moveBy(dx * (mCropRect.width() / mDrawRect.width()),
                dy * (mCropRect.height() / mDrawRect.height()));
        } else if (edge == ROTATE) {
            dx = fpoints[0];
            dy = fpoints[1];
            xDelta = dx * (mCropRect.width() / mDrawRect.width());
            yDelta = dy * (mCropRect.height() / mDrawRect.height());
            rotateBy(event2.getX(), event2.getY(), dx, dy);

            invalidate();
            // mContext.invalidate( getInvalidationRect() );
        } else {

            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(-mRotation);
            rotateMatrix.mapPoints(fpoints);
            dx = fpoints[0];
            dy = fpoints[1];

            if (((GROW_LEFT_EDGE | GROW_RIGHT_EDGE) & edge) == 0)
                dx = 0;
            if (((GROW_TOP_EDGE | GROW_BOTTOM_EDGE) & edge) == 0)
                dy = 0;

            xDelta = dx * (mCropRect.width() / mDrawRect.width());
            yDelta = dy * (mCropRect.height() / mDrawRect.height());

            boolean is_left = UIUtils.checkBits(edge, GROW_LEFT_EDGE);
            boolean is_top = UIUtils.checkBits(edge, GROW_TOP_EDGE);

            float delta;

            if (Math.abs(xDelta) >= Math.abs(yDelta)) {
                delta = xDelta;
                if (is_left) {
                    delta *= -1;
                }
            } else {
                delta = yDelta;
                if (is_top) {
                    delta *= -1;
                }
            }

            Log.d(LOG_TAG, "x: " + xDelta + ", y: " + yDelta + ", final: " + delta);

            growBy(delta);

            invalidate();
            // mContext.invalidate( getInvalidationRect() );
        }
    }
    //设置移动函数
    void onMove(float dx, float dy) {
        moveBy(dx * (mCropRect.width() / mDrawRect.width()),
            dy * (mCropRect.height() / mDrawRect.height()));
    }
    //设置出现错误时的处理函数
    public void invalidate() {
        mDrawRect = computeLayout(); // true
        Log.d(LOG_TAG, "computeLayout: " + mDrawRect);

        if (mDrawRect != null && mDrawRect.left > 1200) {
            Log.e(LOG_TAG, "computeLayout: " + mDrawRect);
        }
        mRotateMatrix.reset();
        mRotateMatrix.postTranslate(-mDrawRect.centerX(), -mDrawRect.centerY());
        mRotateMatrix.postRotate(mRotation);
        mRotateMatrix.postTranslate(mDrawRect.centerX(), mDrawRect.centerY());
    }
    //绘制移动函数
    void moveBy(final float dx, final float dy) {
        //如果可以移动则进行移动
        if (mMoveEnabled) {
            mCropRect.offset(dx, dy);
            //对错误进行判断和处理
            invalidate();
        }
    }
    //图形的旋转函数
    void rotateBy(final float dx, final float dy, float diffx, float diffy) {
        //如果不可旋转或者旋转角为0则直接返回
        if (!mRotateEnabled && !mScaleEnabled)
            return;

        final float pt1[] = new float[] { mDrawRect.centerX(), mDrawRect.centerY() };
        final float pt2[] = new float[] { mDrawRect.right, mDrawRect.bottom };
        final float pt3[] = new float[] { dx, dy };
        //计算旋转角度
        final double angle1 = Point2D.angleBetweenPoints(pt2, pt1);
        final double angle2 = Point2D.angleBetweenPoints(pt3, pt1);
         //如果可以旋转则将计算出的角度保存在对象中
        if (mRotateEnabled) {
            mRotation = -(float) (angle2 - angle1);
        }

        if (mScaleEnabled) {

            final Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(-mRotation);

            final float points[] = new float[] { diffx, diffy };
            rotateMatrix.mapPoints(points);

            diffx = points[0];
            diffy = points[1];

            final float xDelta = diffx * (mCropRect.width() / mDrawRect.width());
            final float yDelta = diffy * (mCropRect.height() / mDrawRect.height());

            final float pt4[] = new float[] { mDrawRect.right + xDelta, mDrawRect.bottom + yDelta };
            final double distance1 = Point2D.distance(pt1, pt2);
            final double distance2 = Point2D.distance(pt1, pt4);
            final float distance = (float) (distance2 - distance1);
            growBy(distance);
        }

    }
    //进行图形旋转操作
    void onRotateAndGrow(double angle, float scaleFactor) {

        if (!mRotateEnabled)
            mRotation -= (float) (angle);

        if (mRotateEnabled) {
            mRotation -= (float) (angle);
            growBy(scaleFactor * (mCropRect.width() / mDrawRect.width()));
        }
        //错误的判断和处理
        invalidate();
    }
    //设置影藏值
    public void setHidden(final boolean hidden) {
        mHidden = hidden;
    }
    //设置最小值
    public void setMinSize(final float size) {
        if (mRatio >= 1) {
            mContent.setMinSize(size, size / mRatio);
        } else {
            mContent.setMinSize(size * mRatio, size);
        }
    }
    //设置模型
    public void setMode(final int mode) {
        Log.i(LOG_TAG, "setMode: " + mode);
        if (mode != mMode) {
            mMode = mode;
            updateDrawableState();
        }
    }
    //判断时候进行按压操作
    public boolean isPressed() {
        return isSelected() && mMode != NONE;
    }
    //更新绘图状态
    protected void updateDrawableState() {
        if (null == mBackgroundDrawable)
            return;

        boolean is_selected = isSelected();
        boolean is_focused = isFocused();

        if (is_selected) {
            if (mMode == NONE) {
                if (is_focused) {
                    mBackgroundDrawable.setState(STATE_SET_SELECTED_FOCUSED);
                } else {
                    mBackgroundDrawable.setState(STATE_SET_SELECTED);
                }
            } else {
                mBackgroundDrawable.setState(STATE_SET_SELECTED_PRESSED);
            }

        } else {
            // normal state
            mBackgroundDrawable.setState(STATE_SET_NONE);
        }
    }
    //删除对对象的监听
    public void setOnDeleteClickListener(final OnDeleteClickListener listener) {
        mDeleteClickListener = listener;
    }
    //设置对象的选择状态
    public void setSelected(final boolean selected) {
        Log.d(LOG_TAG, "setSelected: " + selected);
        boolean is_selected = isSelected();
        if (is_selected != selected) {
            mState ^= STATE_SELECTED;
            updateDrawableState();
        }
    }

    //判断视图对象是否处于选择状态
    public boolean isSelected() {
        return (mState & STATE_SELECTED) == STATE_SELECTED;
    }

    //设置视图获得焦点
    public void setFocused(final boolean value) {
        Log.i(LOG_TAG, "setFocused: " + value);
        boolean is_focused = isFocused();
        if (is_focused != value) {
            mState ^= STATE_FOCUSED;

            if (null != mEditableContent) {
                if (value) {
                    mEditableContent.beginEdit();
                } else {
                    mEditableContent.endEdit();
                }
            }
            updateDrawableState();
        }
    }

    //判断视图是否获得焦点
    public boolean isFocused() {
        return (mState & STATE_FOCUSED) == STATE_FOCUSED;
    }

    //初始化函数，初始化整个视图窗口
    public void setup(final Context context, final Matrix m, final Rect imageRect,
                      final RectF cropRect, final boolean maintainAspectRatio) {
        //声明一个矩阵
        mMatrix = new Matrix(m);
        mRotation = 0;
        mRotateMatrix = new Matrix();
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setAntiAlias(true);
        outlinePath = new Path();
        mCropRect = cropRect;
        setMode(NONE);
        invalidate();
    }
    //初始化函数
    public void setup(final Context context, final Matrix m, final Rect imageRect,
                      final RectF cropRect, final boolean maintainAspectRatio, final float rotation) {
        mMatrix = new Matrix(m);
        mRotation = rotation;
        mRotateMatrix = new Matrix();
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setAntiAlias(true);
        outlinePath = new Path();
        mCropRect = cropRect;
        setMode(NONE);
        invalidate();
    }
    //视图更新函数
    public void update(final Matrix imageMatrix, final Rect imageRect) {
        setMode(NONE);
        mMatrix = new Matrix(imageMatrix);
        mRotation = 0;
        mRotateMatrix = new Matrix();
        invalidate();
    }

    public FeatherDrawable getContent() {
        return mContent;
    }
    //更新长宽比
    private void updateRatio() {
        final float w = mContent.getCurrentWidth();
        final float h = mContent.getCurrentHeight();
        mRatio = w / h;
    }
    //强制更新视图函数
    public boolean forceUpdate() {
        Log.i(LOG_TAG, "forceUpdate");

        RectF cropRect = getCropRectF();
        RectF drawRect = getDrawRect();

        if (mEditableContent != null) {
            //获得当前宽度
            final float textWidth = mContent.getCurrentWidth();
            //获得当前高度
            final float textHeight = mContent.getCurrentHeight();
            //更新长宽比
            updateRatio();

            RectF textRect = new RectF(cropRect);
            getMatrix().mapRect(textRect);

            float dx = textWidth - textRect.width();
            float dy = textHeight - textRect.height();

            float[] fpoints = new float[] { dx, dy };

            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(-mRotation);

            dx = fpoints[0];
            dy = fpoints[1];

            float xDelta = dx * (cropRect.width() / drawRect.width());
            float yDelta = dy * (cropRect.height() / drawRect.height());

            if (xDelta != 0 || yDelta != 0) {
                growBy(xDelta / 2, yDelta / 2, false);
            }

            invalidate();
            return true;
        }
        return false;
    }
    //设置内边界大小
    public void setPadding(int value) {
        mPadding = value;
    }

    @Override
    public void onSizeChanged(EditableDrawable content, float left, float top, float right,
                              float bottom) {
        Log.i(LOG_TAG, "onSizeChanged: " + left + ", " + top + ", " + right + ", " + bottom);
        if (content.equals(mEditableContent) && null != mContext) {

            if (mDrawRect.left != left || mDrawRect.top != top || mDrawRect.right != right
                || mDrawRect.bottom != bottom) {
                if (forceUpdate()) {
                    mContext.invalidate(getInvalidationRect());
                } else {
                    mContext.postInvalidate();
                }
            }
        }
    }
}
