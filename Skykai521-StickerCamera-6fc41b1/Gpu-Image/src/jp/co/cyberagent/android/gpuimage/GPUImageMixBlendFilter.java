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
 * 混合滤镜
 */
public class GPUImageMixBlendFilter extends GPUImageTwoInputFilter {

    /*混合位置*/
    private int mMixLocation;
    /*混合度*/
    private float mMix;

    public GPUImageMixBlendFilter(String fragmentShader) {
        this(fragmentShader, 0.5f);
    }

    /**
     * 含参构造函数
     * @param fragmentShader 像素点着色
     * @param mix 混合度
     */
    public GPUImageMixBlendFilter(String fragmentShader, float mix) {
        super(fragmentShader);
        mMix = mix;
    }

    @Override
    public void onInit() {
        super.onInit();
        mMixLocation = GLES20.glGetUniformLocation(getProgram(), "mixturePercent");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setMix(mMix);
    }

    /**
     * @param mix ranges from 0.0 (only image 1) to 1.0 (only image 2),
     *            with 0.5 (half of either) as the normal level
     *
     * 混合程度从0.0（只含有第一张图片）到1.0（只含有第二张图片），0.5表示两部分中每部分的一半
     */
    public void setMix(final float mix) {
        mMix = mix;
        setFloat(mMixLocation, mMix);
    }
}
