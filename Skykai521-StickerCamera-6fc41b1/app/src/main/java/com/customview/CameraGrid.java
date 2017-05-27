package com.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 照相机井字线
 */
public class CameraGrid extends View {
    /**
     * 设置变量topBannerWidth
     */
    private int topBannerWidth = 0;
    private Paint mPaint;

    public CameraGrid(Context context) {
        this(context,null);
    }

    public CameraGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化函数
     */
    private void init(){
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(120);
        mPaint.setStrokeWidth(1f);
    }


    /**
     * 画一个井字,上下画两条灰边，中间为正方形
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 获得控件宽度
         */
        int width = this.getWidth();
        /**
         * 获得控件高度·
         */
        int height = this.getHeight();
        if (width < height) {
            topBannerWidth = height - width;
        }
        if (showGrid) {
            /**
             * 在用户端划线起点坐标为起点的1/3，0,终点为屏幕宽度的1/3,0
             */
            canvas.drawLine(width / 3, 0, width / 3, height, mPaint);
            /**
             * 在用户端划线起点坐标为起点的2/3，0,终点为屏幕宽度的2/3,0
             */
            canvas.drawLine(width * 2 / 3, 0, width * 2 / 3, height, mPaint);
            /**
             * 在用户端划线起点坐标为起点的1/3，0,终点为屏幕宽度的1/3,0
             */
            canvas.drawLine(0, height / 3, width, height / 3, mPaint);
            /**
             * 在用户端划线起点坐标为0，起点的2/3,终点为屏幕宽度，屏幕搞定的2/3,0
             */
            canvas.drawLine(0, height * 2 / 3, width, height * 2 / 3, mPaint);
        }
    }

    /**
     * 设置变量showGrid为true
     */
    private boolean showGrid = true;

    /**
     *返回showGrid的值
     */
    public boolean isShowGrid() {
        return showGrid;
    }

    /**
     *赋值函数，将showGrid设置为传入的参数
     */
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    /**
     *获的控件的高度
     */
    public int getTopWidth() {
        return topBannerWidth;
    }
}
