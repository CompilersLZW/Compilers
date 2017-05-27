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
 * 定义枚举类型Rotation，代表旋转的角度
 */
public enum Rotation {
    /**
     * 分别代表0,90,180和270度
     */
    NORMAL, ROTATION_90, ROTATION_180, ROTATION_270;

    /**
     * Retrieves the int representation of the Rotation.
     * 返回枚举类型代表的具体值
     *
     * @return 0, 90, 180 or 270
     */
    public int asInt() {
        switch (this) {
            case NORMAL: return 0;
            case ROTATION_90: return 90;
            case ROTATION_180: return 180;
            case ROTATION_270: return 270;
            default:
                /*如果找不到对应的枚举类型，则抛出错误信息*/
                throw new IllegalStateException("Unknown Rotation!");
        }
    }

    /**
     * Create a Rotation from an integer. Needs to be either 0, 90, 180 or 270.
     * 从整数值获得枚举类型
     *
     * @param rotation 0, 90, 180 or 270
     * @return Rotation object
     */
    public static Rotation fromInt(int rotation) {
        switch (rotation) {
            case 0: return NORMAL;
            case 90: return ROTATION_90;
            case 180: return ROTATION_180;
            case 270: return ROTATION_270;
            case 360: return NORMAL;
            default:
                /*如果找不到对应的枚举类型，则抛出错误信息*/
                throw new IllegalStateException(
                    rotation + " is an unknown rotation. Needs to be either 0, 90, 180 or 270!");
        }
    }
}
