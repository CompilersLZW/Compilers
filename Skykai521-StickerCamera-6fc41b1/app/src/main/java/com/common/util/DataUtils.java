package com.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.stickercamera.App;
import java.util.HashSet;
import java.util.Set;

public final class DataUtils {
    //设置常数，储存应用配置信息
    private final static String SHARED_PREFERENCE_NAME = "SC_SHARED_PREFERENCE";

    /**
     * 使用泛型对JSON包进行传值。
     * @param c
     * @param name
     * @param <T>
     * @return
     */
    public static <T extends Object> T getObject(Class<T> c, String name) {
        T t = null;
        /**
         * 首先对json包进行解析，解析包内的数据
         */
        try {
            String str = DataUtils.getStringPreferences(App.getApp(), name);
            if (StringUtils.isNotBlank(str)) {
                t = (T) JSON.parseObject(str, c);
            }
            /**
             * 如果解析失败，返回信息解析信息失败
             */
        } catch (Exception e) {
            Log.e("DataUtils", "解析信息失败");
            DataUtils.setStringPreferences(App.getApp(), name, "");
        }
        return t;
    }

    /**
     * 得到名为‘name’的偏好文件。同时你可以更改和返回他的值。
     * 任何调用者在调用同样名字的偏好文件时只有一个实例返回，这就意味着这些调用者都可以看到其他调用者做出的更改。
     * @param context
     * @param name
     * @return
     */
    public static String getStringPreferences(Context context, String name) {
        try {
            return context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                    .getString(name, "");
        } catch (Exception e) {
            Log.e("datautils", e.getMessage() + "");
            remove(context, name);
        }
        return "";
    }
    /**
     * Sets the name of the SharedPreferences file that preferences managed by this
     * will use.
     * @param sharedPreferencesName The name of the SharedPreferences file.
     * @see Context#getSharedPreferences(String, int)
     */
    public static void setStringPreferences(Context context, String name, String value) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
                    Context.MODE_PRIVATE).edit();
            editor.putString(name, value);
            editor.commit();
        } catch (Exception e) {
            Log.e("datautils", e.getMessage() + "");
            remove(context, name);
        }
    }

    /**
     * 和上文一致，只不过采用的是BooleanPreferences
     * @param context
     * @param name
     * @return
     */
    public static boolean getBooleanPreferences(Context context, String name) {
        try {
            return context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                    .getBoolean(name, false);
        } catch (Exception e) {
            Log.e("datautils", e.getMessage());
            remove(context, name);
        }
        return false;
    }

    public static void setBooleanPreferences(Context context, String name, boolean value) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
                    Context.MODE_PRIVATE).edit();
            editor.putBoolean(name, value);
            editor.commit();
        } catch (Exception e) {
            Log.e("datautils", e.getMessage());
            remove(context, name);
        }
    }

    public static void remove(Context context, String name) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
                    Context.MODE_PRIVATE).edit();
            editor.remove(name);
            editor.commit();
        } catch (Exception e) {
            Log.e("datautils", e.getMessage() + "");
        }
    }

    public static long getLongPreferences(Context context, String name) {
        try {
            return context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                    .getLong(name, 0);
        } catch (Exception e) {
            Log.e("datautils", e.getMessage());
            remove(context, name);
        }
        return 0;
    }
    /**
     * 长整型的Preferences
     * @param context
     * @param name
     * @return
     */
    public static void setLongPreferences(Context context, String name, long value) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
                    Context.MODE_PRIVATE).edit();
            editor.putLong(name, value);
            editor.commit();
        } catch (Exception e) {
            Log.e("datautils", e.getMessage());
            remove(context, name);
        }
    }
}
