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

import android.opengl.GLES20;

/**
 * brightness value ranges from -1.0 to 1.0, with 0.0 as the normal level
 * 亮度值从-1.0到1.0,值为0.0时表示正常水平
 */
public class GPUImageBrightnessFilter extends GPUImageFilter {
    public static final String BRIGHTNESS_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float brightness;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4((textureColor.rgb + vec3(brightness)), textureColor.w);\n" +
            " }";

    private int mBrightnessLocation;
    private float mBrightness;

    /**
     * 默认构造函数，默认值为0.0
     */
    public GPUImageBrightnessFilter() {
        this(0.0f);
    }

    /**
     * 含参构造函数，可设置亮度值
     * @param brightness 亮度值常量
     */
    public GPUImageBrightnessFilter(final float brightness) {
        super(NO_FILTER_VERTEX_SHADER, BRIGHTNESS_FRAGMENT_SHADER);
        mBrightness = brightness;
    }

    /**
     * 初始化
     */
    @Override
    public void onInit() {
        super.onInit();
        mBrightnessLocation = GLES20.glGetUniformLocation(getProgram(), "brightness");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setBrightness(mBrightness);
    }

    /**
     * 设置亮度值
     * @param brightness 亮度值常量
     */
    public void setBrightness(final float brightness) {
        mBrightness = brightness;
        setFloat(mBrightnessLocation, mBrightness);
    }
}
