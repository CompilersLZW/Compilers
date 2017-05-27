package com.customview;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.skykai.stickercamera.R;


/**
 * 首页标题栏
 * 布局继承自relativeLayout布局
 * 
 * @author ouyezi
 */
public class CommonTitleBar extends RelativeLayout {

    // 防重复点击时间
    /*
    设置参数BTN_LIMIT_TIME = 500；
     */
    private static final int BTN_LIMIT_TIME = 500;
    /*
    一些基本参数
     */
    private TextView         leftButton;
    private ImageView        leftButtonImg;
    private TextView         middleButton;
    private TextView         rightButton;
    private ImageView        rightButtonImg;
    private int              leftBtnIconId;
    private String           leftBtnStr;
    private String           titleTxtStr;
    private String           rightBtnStr;
    private int              rightBtnIconId;


    public CommonTitleBar(Context context) {
        super(context);
    }

    public CommonTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar);
        // 如果后续有文字按钮，可使用该模式设置
        /*
        左按钮得到左按钮显示文字，其他以此类推
         */
        leftBtnStr = arr.getString(R.styleable.CommonTitleBar_leftBtnTxt);
        leftBtnIconId = arr.getResourceId(R.styleable.CommonTitleBar_leftBtnIcon, 0);
        titleTxtStr = arr.getString(R.styleable.CommonTitleBar_titleTxt);
        rightBtnStr = arr.getString(R.styleable.CommonTitleBar_rightBtnTxt);
        rightBtnIconId = arr.getResourceId(R.styleable.CommonTitleBar_rightBtnIcon, 0);
        if (isInEditMode()) {
            LayoutInflater.from(context).inflate(R.layout.view_title_bar, this);
            return;
        }

        LayoutInflater.from(context).inflate(R.layout.view_title_bar, this);
        findViewById(R.id.title_out_frame).setBackgroundResource(R.color.blue);
        arr.recycle();
    }
/*
布局结束标志：判断是否已到达编辑最底部，若满足条件就返回
 */
    protected void onFinishInflate() {
        if (isInEditMode()) {
            return;
        }
        /*
        依次获得按钮的图片和文字
         */
        leftButtonImg = (ImageView) findViewById(R.id.title_left_btn);
        leftButton = (TextView) findViewById(R.id.title_left);
        middleButton = (TextView) findViewById(R.id.title_middle);
        rightButtonImg = (ImageView) findViewById(R.id.title_right_btn);
        rightButton = (TextView) findViewById(R.id.title_right);

        if (leftBtnIconId != 0) {
            leftButtonImg.setImageResource(leftBtnIconId);
            leftButtonImg.setVisibility(View.VISIBLE);
        } else {
            leftButtonImg.setVisibility(View.GONE);
        }
        if (rightBtnIconId != 0) {
            rightButtonImg.setImageResource(rightBtnIconId);
            rightButtonImg.setVisibility(View.VISIBLE);
        } else {
            rightButtonImg.setVisibility(View.GONE);
        }
        setLeftTxtBtn(leftBtnStr);
        setTitleTxt(titleTxtStr);
        setRightTxtBtn(rightBtnStr);
    }
    /*
           定义右按钮：
           如果他的·文字述叙是空的，就获得他的文字，并显示出来；
           否则，显示原有文字
            */
    public void setRightTxtBtn(String btnTxt) {
        if (!TextUtils.isEmpty(btnTxt)) {
            rightButton.setText(btnTxt);
            rightButton.setVisibility(View.VISIBLE);
        } else {
            rightButton.setVisibility(View.GONE);
        }
    }
    /*
           定义左按钮：
           如果他的·文字述叙是空的，就获得他的文字，并显示出来；
           否则，显示原有文字
            */
    public void setLeftTxtBtn(String leftBtnStr) {
        if (!TextUtils.isEmpty(leftBtnStr)) {
            leftButton.setText(leftBtnStr);
            leftButton.setVisibility(View.VISIBLE);
        } else {
            leftButton.setVisibility(View.GONE);
        }
    }
    /*
           定义标题，中间按钮：
           如果他的·文字述叙是空的，就获得他的文字，并显示出来；
           否则，显示原有文字
            */
    public void setTitleTxt(String title) {
        if (!TextUtils.isEmpty(title)) {
            middleButton.setText(title);
            middleButton.setVisibility(View.VISIBLE);
        } else {
            middleButton.setVisibility(View.GONE);
        }
    }
/*
隐藏左按钮，让他的文字，图片置空，且让他的左边空余区域不可以点击
 */
    public void hideLeftBtn() {
        leftButton.setVisibility(View.GONE);
        leftButtonImg.setVisibility(View.GONE);
        findViewById(R.id.title_left_area).setOnClickListener(null);
    }
    /*
    隐藏右按钮，让他的文字，图片置空，且让他的左边空余区域不可以点击
     */
    public void hideRightBtn() {
        rightButton.setVisibility(View.GONE);
        rightButtonImg.setVisibility(View.GONE);
        findViewById(R.id.title_right_area).setOnClickListener(null);
    }
/*
一个左按钮点击函数，当鼠标点击时，找到并监听所点击的时事件
 */
    public void setLeftBtnOnclickListener(OnClickListener listener) {
        OnClickListener myListener = new GlobalLimitClickOnClickListener(listener, BTN_LIMIT_TIME);
        findViewById(R.id.title_left_area).setOnClickListener(myListener);
    }
    /*
    一个右按钮点击函数，当鼠标点击时，找到并监听所点击的时事件
     */
    public void setRightBtnOnclickListener(OnClickListener listener) {
        OnClickListener myListener = new GlobalLimitClickOnClickListener(listener, BTN_LIMIT_TIME);
        findViewById(R.id.title_right_area).setOnClickListener(myListener);
    }

}
