/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.cyberagent.android.gpuimage;

/**
 * A hardware-accelerated 9-hit box blur of an image
 * 模糊图像的一种硬件加速措施
 *
 * scaling: for the size of the applied blur, default of 1.0
 * 应用的模糊程度，默认为1
 */
public class GPUImageBoxBlurFilter extends GPUImageTwoPassTextureSamplingFilter {
    /*顶点着色器*/
    public static final String VERTEX_SHADER =
            "attribute vec4 position;\n" +
                    "attribute vec2 inputTextureCoordinate;\n" +
                    "\n" +
                    "uniform float texelWidthOffset; \n" +
                    "uniform float texelHeightOffset; \n" +
                    "\n" +
                    "varying vec2 centerTextureCoordinate;\n" +
                    "varying vec2 oneStepLeftTextureCoordinate;\n" +
                    "varying vec2 twoStepsLeftTextureCoordinate;\n" +
                    "varying vec2 oneStepRightTextureCoordinate;\n" +
                    "varying vec2 twoStepsRightTextureCoordinate;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "gl_Position = position;\n" +
                    "\n" +
                    "vec2 firstOffset = vec2(1.5 * texelWidthOffset, 1.5 * texelHeightOffset);\n" +
                    "vec2 secondOffset = vec2(3.5 * texelWidthOffset, 3.5 * texelHeightOffset);\n" +
                    "\n" +
                    "centerTextureCoordinate = inputTextureCoordinate;\n" +
                    "oneStepLeftTextureCoordinate = inputTextureCoordinate - firstOffset;\n" +
                    "twoStepsLeftTextureCoordinate = inputTextureCoordinate - secondOffset;\n" +
                    "oneStepRightTextureCoordinate = inputTextureCoordinate + firstOffset;\n" +
                    "twoStepsRightTextureCoordinate = inputTextureCoordinate + secondOffset;\n" +
                    "}\n";
    /*像素点着色器*/
    public static final String FRAGMENT_SHADER =
            "precision highp float;\n" +
                    "\n" +
                    "uniform sampler2D inputImageTexture;\n" +
                    "\n" +
                    "varying vec2 centerTextureCoordinate;\n" +
                    "varying vec2 oneStepLeftTextureCoordinate;\n" +
                    "varying vec2 twoStepsLeftTextureCoordinate;\n" +
                    "varying vec2 oneStepRightTextureCoordinate;\n" +
                    "varying vec2 twoStepsRightTextureCoordinate;\n" +
                    "\n" +
                    "void main()\n" +
                    "{\n" +
                    "lowp vec4 fragmentColor = texture2D(inputImageTexture, centerTextureCoordinate) * 0.2;\n" +
                    "fragmentColor += texture2D(inputImageTexture, oneStepLeftTextureCoordinate) * 0.2;\n" +
                    "fragmentColor += texture2D(inputImageTexture, oneStepRightTextureCoordinate) * 0.2;\n" +
                    "fragmentColor += texture2D(inputImageTexture, twoStepsLeftTextureCoordinate) * 0.2;\n" +
                    "fragmentColor += texture2D(inputImageTexture, twoStepsRightTextureCoordinate) * 0.2;\n" +
                    "\n" +
                    "gl_FragColor = fragmentColor;\n" +
                    "}\n";

    /*模糊程度*/
    private float blurSize = 1f;

    /**
     * Construct new BoxBlurFilter with default blur size of 1.0.
     * 默认构造器使用默认的模糊程度1.0初始化
     */
    public GPUImageBoxBlurFilter() {
        this(1f);
    }

    /**
     * 含参构造函数
     * @param blurSize 模糊程度
     */
    public GPUImageBoxBlurFilter(float blurSize) {
        super(VERTEX_SHADER, FRAGMENT_SHADER, VERTEX_SHADER, FRAGMENT_SHADER);
        this.blurSize = blurSize;
    }

    /**
     * A scaling for the size of the applied blur, default of 1.0
     * 设置应用的模糊程度
     *
     * @param blurSize 模糊程度
     */
    public void setBlurSize(float blurSize) {
        this.blurSize = blurSize;
        /*多线程运行*/
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                initTexelOffsets();
            }
        });
    }

    @Override
    public float getVerticalTexelOffsetRatio() {
        return blurSize;
    }

    @Override
    public float getHorizontalTexelOffsetRatio() {
        return blurSize;
    }
}
