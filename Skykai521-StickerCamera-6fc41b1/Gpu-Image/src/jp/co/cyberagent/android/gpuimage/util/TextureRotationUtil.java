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

package jp.co.cyberagent.android.gpuimage.util;

import jp.co.cyberagent.android.gpuimage.Rotation;

public class TextureRotationUtil {

    /*不翻转时对应的值*/
    public static final float TEXTURE_NO_ROTATION[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };
    /*翻转90度时对应的值*/
    public static final float TEXTURE_ROTATED_90[] = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };
    /*翻转180度时对应的值*/
    public static final float TEXTURE_ROTATED_180[] = {
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
    };
    /*翻转270度时对应的值*/
    public static final float TEXTURE_ROTATED_270[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    /*默认构造函数为空*/
    private TextureRotationUtil() {
    }

    /**
     * 通过枚举类型Rotation，获得旋转角度
     * @param rotation 旋转角度
     * @param flipHorizontal 是否水平翻转
     * @param flipVertical 是否垂直翻转
     * @return 返回翻转角度
     */
    public static float[] getRotation(final Rotation rotation, final boolean flipHorizontal,
                                                         final boolean flipVertical) {
        float[] rotatedTex;
        switch (rotation) {
            /*翻转90度*/
            case ROTATION_90:
                rotatedTex = TEXTURE_ROTATED_90;
                break;
            /*翻转180度*/
            case ROTATION_180:
                rotatedTex = TEXTURE_ROTATED_180;
                break;
            /*翻转270度*/
            case ROTATION_270:
                rotatedTex = TEXTURE_ROTATED_270;
                break;
            /*不翻转*/
            case NORMAL:
            default:
                /*默认不翻转*/
                rotatedTex = TEXTURE_NO_ROTATION;
                break;
        }
        /*水平翻转时对应值*/
        if (flipHorizontal) {
            rotatedTex = new float[]{
                    flip(rotatedTex[0]), rotatedTex[1],
                    flip(rotatedTex[2]), rotatedTex[3],
                    flip(rotatedTex[4]), rotatedTex[5],
                    flip(rotatedTex[6]), rotatedTex[7],
            };
        }
        /*垂直翻转时对应值*/
        if (flipVertical) {
            rotatedTex = new float[]{
                    rotatedTex[0], flip(rotatedTex[1]),
                    rotatedTex[2], flip(rotatedTex[3]),
                    rotatedTex[4], flip(rotatedTex[5]),
                    rotatedTex[6], flip(rotatedTex[7]),
            };
        }
        return rotatedTex;
    }

    /**
     * 进行翻转操作（改变角度的值）
     * @param i 翻转角度
     * @return 返回翻转后的值
     */
    private static float flip(final float i) {
        if (i == 0.0f) {
            return 1.0f;
        }
        return 0.0f;
    }
}
