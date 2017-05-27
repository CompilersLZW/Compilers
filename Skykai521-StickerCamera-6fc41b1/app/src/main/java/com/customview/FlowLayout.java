/*
 * Copyright 2013 Blaz Solar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.github.skykai.stickercamera.R;

import java.util.ArrayList;
import java.util.List;

/**
 * FlowLayout will arrange child elements horizontally one next to another. If there is not enough
 * space for next view new line will be added.
 */

/**
 * 从系统中获得视频信息
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
/**
 * 声明继承ViewGroup的FlowLayout类
 */
public class FlowLayout extends ViewGroup {

	/**
	 * 申明参数控件的基本设置信息
	 */
	private int mGravity = (isIcs() ? Gravity.START : Gravity.LEFT) | Gravity.TOP;

    private final List<List<View>> mLines = new ArrayList<List<View>>();
    private final List<Integer> mLineHeights = new ArrayList<Integer>();
    private final List<Integer> mLineMargins = new ArrayList<Integer>();

	/**
	 *申明FlowLayout不同的构造方法
	 */
	public FlowLayout(Context context) {
		this(context, null);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		/**
		 * 调用父类中的函数获取控件attrs和 defStyle
		 */
		super(context, attrs, defStyle);
		/**
		 * 声明一个TypedArray的对象a并赋值
		 */
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FlowLayout, defStyle, 0);
		/**
		 * 从a中取出存放的数据
		 */
        try {
            int index = a.getInt(R.styleable.FlowLayout_android_gravity, -1);
            if(index > 0) {
                setGravity(index);
            }
        } finally {
			/**
			 * 回收TypedArray
			 */
            a.recycle();
        }

	}

    /**
     * {@inheritDoc}
     */
    @Override
	/**
	 * 重写onMeasure方法对视图的大小位置进行测量安排子视图的位置
	 */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/**
		 * 调用父类中的onMeasure测量视图的大小
		 */
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		/**\
		 * 声明控件宽度并设置值为总宽度减去左边界和右边界的宽度
		 */
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
		/**
		 * 声明控件高度并设置初始值
		 */
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = getPaddingTop() + getPaddingBottom();

        int lineWidth = 0;
        int lineHeight = 0;
		/**
		 * 声明变量childCount表示自视图的数量
		 */
        int childCount = getChildCount();

        for(int i = 0; i < childCount; i++) {
		/**
		 *依次获得每一个子视图对象的引用
		 */
            View child = getChildAt(i);
            boolean lastChild = i == childCount - 1;

            if(child.getVisibility() == View.GONE) {
			/**
			 * 判断如果是最后一个子视图则在此时计算出视图的宽度和高度
			 */
                if(lastChild) {
                    width = Math.max(width, lineWidth);
                    height += lineHeight;
                }

                continue;
            }
			/**
			 *测量时考虑把 margin 及  padding 也作为子视图大小的一部分
			 */
            measureChildWithMargins(child, widthMeasureSpec, lineWidth, heightMeasureSpec, height);
			/**
			 * 通过getLayoutParams获得子视图的参数
			 */
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
			/**
			 * 设置子视图宽度模式为最大模式、
			 * 设置子视图宽度为sizeWidth
			 */
            int childWidthMode = MeasureSpec.AT_MOST;
            int childWidthSize = sizeWidth;
			/**
			 * 设置子视图盖度模式为最大模式
			 * 设置子视图为sizeHeight
			 */
            int childHeightMode = MeasureSpec.AT_MOST;
            int childHeightSize = sizeHeight;
			/**
			 *如果获取的子视图的参数与测量值相同则设置子视图的模式为精确模式
			 * 子视图的宽度大小设置为子视图的宽度减去内边距
			 */
            if(lp.width == LayoutParams.MATCH_PARENT) {
                childWidthMode = MeasureSpec.EXACTLY    ;
                childWidthSize -= lp.leftMargin + lp.rightMargin;
            } else if(lp.width >= 0) {
                childWidthMode = MeasureSpec.EXACTLY;
                childWidthSize = lp.width;
            }
			/**
			 * 如果获得的高度参数hetght大于0则设置子视图宽度模式为精确模式
			 * 将子视图的高度大小设置为获得的高度参数
			 * 否则见高度模式设置为未指定型
			 */
            if(lp.height >= 0) {
                childHeightMode = MeasureSpec.EXACTLY;
                childHeightSize = lp.height;
            } else if (modeHeight == MeasureSpec.UNSPECIFIED) {
                childHeightMode = MeasureSpec.UNSPECIFIED;
                childHeightSize = 0;
            }
			/**
			 * 根据计算得到的子视图高度，宽度和宽度模式和高度模式
			 * 调用函数makeMeasureSpec创建一个测量值
			 */
            child.measure(
                    MeasureSpec.makeMeasureSpec(childWidthSize, childWidthMode),
                    MeasureSpec.makeMeasureSpec(childHeightSize, childHeightMode)
            );
			/**
			 * 设置子视图的总宽度为展示图的宽度加上获取的lp中的左内边距和右內边距的和
			 */
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
			/**
			 *如果加上当前child则超过最大宽度，则开启新的一行
			 * 重新开始新行，开始记录下一行的宽度childidth
			 * 如果没有超过最大宽度则在行宽度上加上childwidth的值
			 * 并重写计算这一行的高度，为childHeight的最大高度
			 */
            if(lineWidth + childWidth > sizeWidth) {

                width = Math.max(width, lineWidth);
                lineWidth = childWidth;

                height += lineHeight;
                lineHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
            }
		/**
		 * 如果当前行为最后一行则重新计算行宽度
		 * 高度为当前高度加上行高度
		 */
            if(lastChild) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }

        }
		/**
		 * 设置宽度为在视图宽度的基础上加上视图的左外边距以及右外边距
		 */
        width += getPaddingLeft() + getPaddingRight();
		/**
		 *定义当前视图的大小
		 * 如果宽度模式为精确模式则将宽度设置为width否则设置为sizewidth
		 * 如果高度模式为精确模式则将宽度设置为height否则设置为sizeHeight
		 */
        setMeasuredDimension(
                (modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width,
                (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);
    }

    /**
     * {@inheritDoc}
     */
	@Override

	/**
	 * 重写onLayout函数，用于放置视图的位置，
	 */
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		/**
		 * 第一次测量之后，行管理器中就有了行的对象，之后每次测量都会去创建下一行，这样就会出现很多空行出来，所
		 * 以需要在测量之前将集合清空。
		 * 清空mLines，mLineHeights,mlineMargins中的数据
		 */
		mLines.clear();
        mLineHeights.clear();
        mLineMargins.clear();
		/**
		 * 声明视图的宽度和高度并设置weidth和height的值
		 */
		int width = getWidth();
		int height = getHeight();

		int linesSum = getPaddingTop();

		int lineWidth = 0;
		int lineHeight = 0;
		List<View> lineViews = new ArrayList<View>();

		float horizontalGravityFactor;
		switch ((mGravity & Gravity.HORIZONTAL_GRAVITY_MASK)) {
			case Gravity.LEFT:
			default:
                horizontalGravityFactor = 0;
				break;
			case Gravity.CENTER_HORIZONTAL:
				horizontalGravityFactor = .5f;
				break;
			case Gravity.RIGHT:
				horizontalGravityFactor = 1;
				break;
		}
		/**
		 * 遍历获取孩子
		 *如果孩子被设置为隐藏则继续
		 */

		for(int i = 0; i < getChildCount(); i++) {

			View child = getChildAt(i);

			if(child.getVisibility() == View.GONE) {
				continue;
			}
			/**
			 * 获取孩子的参数
			 * 孩子的宽度为孩子宽度测量值加上左内边距加上右外边距
			 * 孩子的高度为高度测量值的上边距加上下边距
			 */
			LayoutParams lp = (LayoutParams) child.getLayoutParams();

			int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
			int childHeight = child.getMeasuredHeight() + lp.bottomMargin + lp.topMargin;
			/**
			 * 如果宽度加上孩子宽度超过最大宽度则开始新行
			 * 视图的高度加上当前行高度 lineHeight\
			 *将lineHeigh和lineWidth初始化为0
			 */
			if(lineWidth + childWidth > width) {
				mLineHeights.add(lineHeight);
				mLines.add(lineViews);
				mLineMargins.add((int) ((width - lineWidth) * horizontalGravityFactor) + getPaddingLeft());

				linesSum += lineHeight;

				lineHeight = 0;
				lineWidth = 0;
				lineViews = new ArrayList<View>();
			}
			/**
			 * 如果没有超过最大宽度则继续添加孩子
			 * 更新行的高度
			 */
			lineWidth += childWidth;
			lineHeight = Math.max(lineHeight, childHeight);
			lineViews.add(child);
		}
		/**
		 * 添加新的一行
		 * 更新mLineHeights和mLines，mLineMargins
		 */
		mLineHeights.add(lineHeight);
		mLines.add(lineViews);
		mLineMargins.add((int) ((width - lineWidth) * horizontalGravityFactor) + getPaddingLeft());

		linesSum += lineHeight;

		int verticalGravityMargin = 0;
		/**
		 * 根据gravity的值确定水平方向的起始位置
		 */
		switch ((mGravity & Gravity.VERTICAL_GRAVITY_MASK)	) {
			case Gravity.TOP:
			default:
				break;
			case Gravity.CENTER_VERTICAL:
				verticalGravityMargin = (height - linesSum) / 2;
				break;
			case Gravity.BOTTOM:
				verticalGravityMargin = height - linesSum;
				break;
		}

		int numLines = mLines.size();

		int left;
		int top = getPaddingTop();
		/**
		 * 遍历每一行获取每一行的视图
		 */
		for(int i = 0; i < numLines; i++) {

			lineHeight = mLineHeights.get(i);
			lineViews = mLines.get(i);
			left = mLineMargins.get(i);

			int children = lineViews.size();
			/**
			 * 遍历每一个孩子
			 * 如果孩子被设置为隐藏则忽略继续
			 */
			for(int j = 0; j < children; j++) {

				View child = lineViews.get(j);

				if(child.getVisibility() == View.GONE) {
					continue;
				}
				/**
				 *孩子中的参数分装在lp中
				 */
				LayoutParams lp = (LayoutParams) child.getLayoutParams();

				// if height is match_parent we need to remeasure child to line height
				if(lp.height == LayoutParams.MATCH_PARENT) {
					int childWidthMode = MeasureSpec.AT_MOST;
					int childWidthSize = lineWidth;

					if(lp.width == LayoutParams.MATCH_PARENT) {
						childWidthMode = MeasureSpec.EXACTLY;
					} else if(lp.width >= 0) {
						childWidthMode = MeasureSpec.EXACTLY;
						childWidthSize = lp.width;
					}
					/**
					 * 测量孩子的传参数
					 * 参入参数为生成孩子的宽度和高度
					 */
					child.measure(
							MeasureSpec.makeMeasureSpec(childWidthSize, childWidthMode),
							MeasureSpec.makeMeasureSpec(lineHeight - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY)
					);
				}
				/**
				 * 声明变量childWidth和变量childHeight
				 * 并对他们进行初始化为测量值
				 */
				int childWidth = child.getMeasuredWidth();
				int childHeight = child.getMeasuredHeight();

				int gravityMargin = 0;
				/**
				 * 根据gravity的值确定水平方向的起始位置
				 */
				if(Gravity.isVertical(lp.gravity)) {
					switch (lp.gravity) {
						case Gravity.TOP:
						default:
							break;
						case Gravity.CENTER_VERTICAL:
						case Gravity.CENTER:
							gravityMargin = (lineHeight - childHeight - lp.topMargin - lp.bottomMargin) / 2 ;
							break;
						case Gravity.BOTTOM:
							gravityMargin = lineHeight - childHeight - lp.topMargin - lp.bottomMargin;
							break;
					}
				}
				/**
				 *安排孩子的布局，设置它在视图中的相对位置
				 */
				child.layout(left + lp.leftMargin,
						top + lp.topMargin + gravityMargin + verticalGravityMargin,
						left + childWidth + lp.leftMargin,
						top + childHeight + lp.topMargin + gravityMargin + verticalGravityMargin);
				/**
				 * 左边的宽度是孩子视图的宽度加上孩子视图的左右内边距
				 */
				left += childWidth + lp.leftMargin + lp.rightMargin;

			}

			top += lineHeight;
		}

	}

	@Override
	/**
	 * 重写LayoutParams函数 返回一个新建的LayoutParams(p)
	 */
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new LayoutParams(p);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(getContext(), attrs);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	/**
	 * 设置对象如何在X轴和Y轴上在自己的边界内定位其内容。
	 */
    public void setGravity(int gravity) {
		if(mGravity != gravity) {
			if((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
				/**
				 * 根据gravity的值将对象设置在容器的开始或者最左边
				 */
				gravity |= isIcs() ? Gravity.START : Gravity.LEFT;
			}

			if((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
				/**
				 * 将对象推送到容器的最上层
				 */
				gravity |= Gravity.TOP;
			}

			mGravity = gravity;
			requestLayout();
		}
	}

	/**
	 *获得自己的mGravity并返回
	 */
    public int getGravity() {
        return mGravity;
    }

    /**
     * @return <code>true</code> if device is running ICS or grater version of Android.
     */
    private static boolean isIcs() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

	/**
	 * 声明类LayoutParams
	 */
	public static class LayoutParams extends MarginLayoutParams {

		public int gravity = -1;

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);

			TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.FlowLayout_Layout);

            try {
                gravity = a.getInt(R.styleable.FlowLayout_Layout_android_layout_gravity, -1);
            } finally {
                a.recycle();
            }
		}

		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}

	}
}
