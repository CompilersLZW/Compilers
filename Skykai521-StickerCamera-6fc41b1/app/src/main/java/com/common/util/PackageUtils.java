package com.common.util;

import java.io.File;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import com.common.util.ShellUtils.CommandResult;

/**
 *申明一个PackageUtils类
 */
public class PackageUtils {

    public static final String TAG = "PackageUtils";

    private PackageUtils() {
        throw new AssertionError();
    }

    /**
     * App在安装时的默认值
     */
    public static final int APP_INSTALL_AUTO     = 0;
    public static final int APP_INSTALL_INTERNAL = 1;
    public static final int APP_INSTALL_EXTERNAL = 2;

    /**
     * 安装条件
     * <ul>
     * <li>如果获得root权限就看{@link #installSilent(Context, String)}</li>
     * <li>如果没有获得root权限就看{@link #installNormal(Context, String)}</li>
     * </ul>
     * 
     * @param context
     * @param filePath
     * @return
     */
    /**
     * 安装函数
     * 对应之前的roo权限
     * 返回函数installSilent(context,filePath)
     */
    public static final int install(Context context, String filePath) {
        if (PackageUtils.isSystemApplication(context) || ShellUtils.checkRootPermission()) {
            return installSilent(context, filePath);
        }
        return installNormal(context, filePath) ? INSTALL_SUCCEEDED : INSTALL_FAILED_INVALID_URI;
    }

    /**
     * 对应上文的没有获得root权限的情况
     * @param context
     * @param filePath file path of package 文件路径
     * @return whether apk exist 安装apk是否存在
     */
    public static boolean installNormal(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file == null || !file.exists() || !file.isFile() || file.length() <= 0) {
            return false;
        }

        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }

    /**
     * 在获得root权限之后直接安装
     * <ul>
     * <strong>Attentions:</strong>
     * <li>Don't call this on the ui thread, it may costs some times.</li>
     * 在安装期间不要打电话它会耗费更多的时间
     * <li>You should add <strong>android.permission.INSTALL_PACKAGES</strong> in manifest, so no need to request root
     * 你应该在manifest.xml中添加权限，所以不需要root权限
     * permission, if you are system app.</li>
     * 如果你是系统应用
     * <li>Default pm install params is "-r".</li>
     * 设置默认参数pm为-r
     * </ul>
     * @param context
     * @param filePath file path of package 安装路径
     * @return {@link PackageUtils#INSTALL_SUCCEEDED} 如果安装成功就返回这个包，否则就返回其他的
     *         {@link PackageUtils}.INSTALL_FAILED_*. same to {@link PackageManager}.INSTALL_* 如果安装失败就返回这个
     * @see #installSilent(Context, String, String)  这个函数为沉默安装函数
     */
    public static int installSilent(Context context, String filePath) {
        return installSilent(context, filePath, " -r " + getInstallLocationParams());
    }

    /**
     * 在获得root权限之后直接安装
     * <ul>
     * <strong>Attentions:</strong>
     * <li>Don't call this on the ui thread, it may costs some times.</li>
     * 在安装期间不要打电话它会耗费更多的时间
     * <li>You should add <strong>android.permission.INSTALL_PACKAGES</strong> in manifest, so no need to request root
     * 你应该在manifest.xml中添加权限，所以不需要root权限
     * permission, if you are system app.</li>
     * 如果你是系统应用
     * <li>Default pm install params is "-r".</li>
     * 设置默认参数pm为-r
     * </ul>
     *
     * @param context
     * @param filePath file path of package 安装路径
     * @return {@link PackageUtils#INSTALL_SUCCEEDED} 如果安装成功就返回这个包，否则就返回其他的
     *         {@link PackageUtils}.INSTALL_FAILED_*. same to {@link PackageManager}.INSTALL_* 如果安装失败就返回这个
     * @see #installSilent(Context, String, String)  这个函数为沉默安装函数
     */
    public static int installSilent(Context context, String filePath, String pmParams) {
        if (filePath == null || filePath.length() == 0) {
            return INSTALL_FAILED_INVALID_URI;
        }

        File file = new File(filePath);
        if (file == null || file.length() <= 0 || !file.exists() || !file.isFile()) {
            return INSTALL_FAILED_INVALID_URI;
        }

        /**
         * 如果是系统应用则不需要获得root权限 <uses-permission
         * android:name="android.permission.INSTALL_PACKAGES" /> in mainfest
         **/

        StringBuilder command = new StringBuilder().append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install ")
                .append(pmParams == null ? "" : pmParams).append(" ").append(filePath.replace(" ", "\\ "));
        /**
         * 设置library路径
         */

        /**
        * 查看安装是否成功
         * 如果安装结果的返回信息不为空而且包含success
         * 则返回信息INSTALL_SUCCEEDED
        */
        CommandResult commandResult = ShellUtils.execCommand(command.toString(), !isSystemApplication(context), true);
        if (commandResult.successMsg != null
                && (commandResult.successMsg.contains("Success") || commandResult.successMsg.contains("success"))) {
            return INSTALL_SUCCEEDED;
        }
        /**
         * 向后台打印信息
         * 信息为installSilent successMsg:和, ErrorMsg:
         */
        Log.e(TAG,
                new StringBuilder().append("installSilent successMsg:").append(commandResult.successMsg)
                        .append(", ErrorMsg:").append(commandResult.errorMsg).toString());
        /**
         * 如果安装信息的errorMsg为null则返回INSTALL_FAILED_OTHER的值
         */
        if (commandResult.errorMsg == null) {
            return INSTALL_FAILED_OTHER;
        }
        /**
         * 如果返回的错误信息包含INSTALL_FAILED_ALREADY_EXISTS
         * 就返回INSTALL_FAILED_ALREADY_EXISTS
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_ALREADY_EXISTS")) {
            return INSTALL_FAILED_ALREADY_EXISTS;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_INVALID_APK
         * 就返回INSTALL_FAILED_INVALID_APK
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INVALID_APK")) {
            return INSTALL_FAILED_INVALID_APK;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_INVALID_URI
         * 就返回INSTALL_FAILED_INVALID_URI
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INVALID_URI")) {
            return INSTALL_FAILED_INVALID_URI;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_INSUFFICIENT_STORAGE
         * 就返回INSTALL_FAILED_INSUFFICIENT_STORAGE
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INSUFFICIENT_STORAGE")) {
            return INSTALL_FAILED_INSUFFICIENT_STORAGE;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_DUPLICATE_PACKAGE
         * 就返回INSTALL_FAILED_DUPLICATE_PACKAGE
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_DUPLICATE_PACKAGE")) {
            return INSTALL_FAILED_DUPLICATE_PACKAGE;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_NO_SHARED_USER
         * 就返回INSTALL_FAILED_NO_SHARED_USER
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_NO_SHARED_USER")) {
            return INSTALL_FAILED_NO_SHARED_USER;
        }
        /**
         * 如果错误的信息包括INSTALL_FAILED_UPDATE_INCOMPATIBLE
         * 就返回INSTALL_FAILED_UPDATE_INCOMPATIBLE
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_UPDATE_INCOMPATIBLE")) {
            return INSTALL_FAILED_UPDATE_INCOMPATIBLE;
        }
        /**
         * 如果错误信息包含INSTALL_FAILED_SHARED_USER_INCOMPATIBLE
         * 就返回INSTALL_FAILED_SHARED_USER_INCOMPATIBLE
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_SHARED_USER_INCOMPATIBLE")) {
            return INSTALL_FAILED_SHARED_USER_INCOMPATIBLE;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_MISSING_SHARED_LIBRARY
         * 就返回INSTALL_FAILED_MISSING_SHARED_LIBRARY
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_MISSING_SHARED_LIBRARY")) {
            return INSTALL_FAILED_MISSING_SHARED_LIBRARY;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_REPLACE_COULDNT_DELETE
         * 就返回INSTALL_FAILED_REPLACE_COULDNT_DELETE
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_REPLACE_COULDNT_DELETE")) {
            return INSTALL_FAILED_REPLACE_COULDNT_DELETE;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_DEXOPT
         * 就返回INSTALL_FAILED_DEXOPT
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_DEXOPT")) {
            return INSTALL_FAILED_DEXOPT;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_OLDER_SDK
         * 就返回INSTALL_FAILED_OLDER_SDK
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_OLDER_SDK")) {
            return INSTALL_FAILED_OLDER_SDK;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_CONFLICTING_PROVIDER
         * 就返回INSTALL_FAILED_CONFLICTING_PROVIDER
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_CONFLICTING_PROVIDER")) {
            return INSTALL_FAILED_CONFLICTING_PROVIDER;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_NEWER_SDK
         * 就返回INSTALL_FAILED_NEWER_SDK
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_NEWER_SDK")) {
            return INSTALL_FAILED_NEWER_SDK;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_TEST_ONLY
         * 就返回INSTALL_FAILED_TEST_ONLY
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_TEST_ONLY")) {
            return INSTALL_FAILED_TEST_ONLY;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_CPU_ABI_INCOMPATIBLE
         * 就返回INSTALL_FAILED_CPU_ABI_INCOMPATIBLE
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_CPU_ABI_INCOMPATIBLE")) {
            return INSTALL_FAILED_CPU_ABI_INCOMPATIBLE;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_MISSING_FEATURE
         * 就返回INSTALL_FAILED_MISSING_FEATURE
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_MISSING_FEATURE")) {
            return INSTALL_FAILED_MISSING_FEATURE;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_MISSING_FEATURE
         * 就返回INSTALL_FAILED_MISSING_FEATURE
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_CONTAINER_ERROR")) {
            return INSTALL_FAILED_CONTAINER_ERROR;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_INVALID_INSTALL_LOCATION
         * 就返回INSTALL_FAILED_INVALID_INSTALL_LOCATION
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INVALID_INSTALL_LOCATION")) {
            return INSTALL_FAILED_INVALID_INSTALL_LOCATION;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_MEDIA_UNAVAILABLE
         * 就返回INSTALL_FAILED_MEDIA_UNAVAILABLE
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_MEDIA_UNAVAILABLE")) {
            return INSTALL_FAILED_MEDIA_UNAVAILABLE;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_VERIFICATION_TIMEOUT
         * 就返回INSTALL_FAILED_VERIFICATION_TIMEOUT
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_VERIFICATION_TIMEOUT")) {
            return INSTALL_FAILED_VERIFICATION_TIMEOUT;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_VERIFICATION_FAILURE
         * 就返回INSTALL_FAILED_VERIFICATION_FAILURE
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_VERIFICATION_FAILURE")) {
            return INSTALL_FAILED_VERIFICATION_FAILURE;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_PACKAGE_CHANGED
         * 就返回INSTALL_FAILED_PACKAGE_CHANGED
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_PACKAGE_CHANGED")) {
            return INSTALL_FAILED_PACKAGE_CHANGED;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_PACKAGE_CHANGED
         * 就返回INSTALL_FAILED_PACKAGE_CHANGED
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_UID_CHANGED")) {
            return INSTALL_FAILED_UID_CHANGED;
        }
        /**
         * 如果错误的信息包含INSTALL_PARSE_FAILED_NOT_APK
         * 就返回INSTALL_PARSE_FAILED_NOT_APK
         */
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_NOT_APK")) {
            return INSTALL_PARSE_FAILED_NOT_APK;
        }
        /**
         * 如果错误的信息包含INSTALL_PARSE_FAILED_BAD_MANIFEST
         * 就返回INSTALL_PARSE_FAILED_BAD_MANIFEST
         */
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_BAD_MANIFEST")) {
            return INSTALL_PARSE_FAILED_BAD_MANIFEST;
        }
        /**
         * 如果错误的信息包含INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION
         * 就返回INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION
         */
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION")) {
            return INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION;
        }
        /**
         * 如果错误的信息包含INSTALL_PARSE_FAILED_NO_CERTIFICATES
         * 就返回INSTALL_PARSE_FAILED_NO_CERTIFICATES
         */
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_NO_CERTIFICATES")) {
            return INSTALL_PARSE_FAILED_NO_CERTIFICATES;
        }
        /**
         * 如果错误的信息包含INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES
         * 就返回INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES
         */
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES")) {
            return INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES;
        }
        /**
         * 如果错误的信息包含INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING
         * 就返回INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING
         */
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING")) {
            return INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING;
        }
        /**
         * 如果错误的信息包含INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME
         * 就返回INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME
         */
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME")) {
            return INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME;
        }
        /**
         * 如果错误的信息包含INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID
         * 就返回INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID
         */
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID")) {
            return INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID;
        }
        /**
         * 如果错误的信息包含INSTALL_PARSE_FAILED_MANIFEST_MALFORMED
         * 就返回INSTALL_PARSE_FAILED_MANIFEST_MALFORMED
         */
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_MANIFEST_MALFORMED")) {
            return INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
        }
        /**
         * 如果错误的信息包含INSTALL_PARSE_FAILED_MANIFEST_EMPTY
         * 就返回INSTALL_PARSE_FAILED_MANIFEST_EMPTY
         */
        if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_MANIFEST_EMPTY")) {
            return INSTALL_PARSE_FAILED_MANIFEST_EMPTY;
        }
        /**
         * 如果错误的信息包含INSTALL_FAILED_INTERNAL_ERROR
         * 就返回INSTALL_FAILED_INTERNAL_ERROR
         */
        if (commandResult.errorMsg.contains("INSTALL_FAILED_INTERNAL_ERROR")) {
            return INSTALL_FAILED_INTERNAL_ERROR;
        }
        return INSTALL_FAILED_OTHER;
    }

    /**
     * uninstall according conditions未安装条件
     * <ul>
     * <li>if system application or rooted, see {@link #uninstallSilent(Context, String)}</li>
     * 如果系统应用没有root，查看链接#uninstallSilent(Context, String)
     * <li>else see {@link #uninstallNormal(Context, String)}</li>
     * 否则查看链接#uninstallNormal(Context, String)
     * </ul>
     * 
     * @param context
     * @param packageName package name of app
     *                   包名
     * @return whether package name is empty
     * 是否包名为空
     * @return
     */

    public static final int uninstall(Context context, String packageName) {
        if (PackageUtils.isSystemApplication(context) || ShellUtils.checkRootPermission()) {
            return uninstallSilent(context, packageName);
        }
        return uninstallNormal(context, packageName) ? DELETE_SUCCEEDED : DELETE_FAILED_INVALID_PACKAGE;
    }

    /**
     * uninstall package normal by system intent
     * 
     * @param context
     * @param packageName package name of app
     * @return whether package name is empty
     */
    public static boolean uninstallNormal(Context context, String packageName) {
        if (packageName == null || packageName.length() == 0) {
            return false;
        }

        Intent i = new Intent(Intent.ACTION_DELETE, Uri.parse(new StringBuilder(32).append("package:")
                .append(packageName).toString()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }

    /**
     * uninstall package and clear data of app silent by root
     * 
     * @param context
     * @param packageName package name of app
     * @return
     * @see #uninstallSilent(Context, String, boolean)
     */
    public static int uninstallSilent(Context context, String packageName) {
        return uninstallSilent(context, packageName, true);
    }

    /**
     * uninstall package silent by root
     * <ul>
     * <strong>Attentions:</strong>
     * <li>Don't call this on the ui thread, it may costs some times.</li>
     * 不要打电话在ui抛出异常时，它会耗费更多的时间。
     * <li>You should add <strong>android.permission.DELETE_PACKAGES</strong> in manifest, so no need to request root
     * permission, if you are system app.</li>
     * 如果你是系统用户，你应该在mainifest.xml里面添加权限要求
     * </ul>
     * 
     * @param context file path of package
     * @param packageName package name of app
     * @param isKeepData whether keep the data and cache directories around after package removal
     * @return <ul>
     *         <li>{@link #DELETE_SUCCEEDED} means uninstall success</li>
     *         <li>{@link #DELETE_FAILED_INTERNAL_ERROR} means internal error</li>
     *         <li>{@link #DELETE_FAILED_INVALID_PACKAGE} means package name error</li>
     *         <li>{@link #DELETE_FAILED_PERMISSION_DENIED} means permission denied</li>
     */
    public static int uninstallSilent(Context context, String packageName, boolean isKeepData) {
        if (packageName == null || packageName.length() == 0) {
            return DELETE_FAILED_INVALID_PACKAGE;
        }

        /**
         * if context is system app, don't need root permission, but should add <uses-permission
         * android:name="android.permission.DELETE_PACKAGES" /> in mainfest
         * 如果你没有获得root权限，请在mainfest添加<uses-permission android:name="android.permission.DELETE_PACKAGES" />
         **/
        StringBuilder command = new StringBuilder().append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall")
                .append(isKeepData ? " -k " : " ").append(packageName.replace(" ", "\\ "));
        /**
         * 设置lib路径
         */
        CommandResult commandResult = ShellUtils.execCommand(command.toString(), !isSystemApplication(context), true);
        if (commandResult.successMsg != null
                && (commandResult.successMsg.contains("Success") || commandResult.successMsg.contains("success"))) {
            return DELETE_SUCCEEDED;
        }
        /**
         * 向后台打印输出未安装信息
         */
        Log.e(TAG,
                new StringBuilder().append("uninstallSilent successMsg:").append(commandResult.successMsg)
                        .append(", ErrorMsg:").append(commandResult.errorMsg).toString());
        /**
         * 如果错误信息等于null，返回DELETE_FAILED_INTERNAL_ERROR
         */
        if (commandResult.errorMsg == null) {
            return DELETE_FAILED_INTERNAL_ERROR;
        }
        /**
         * 如果错误信息包含Permission denied
         * 返回DELETE_FAILED_PERMISSION_DENIED
         */

        if (commandResult.errorMsg.contains("Permission denied")) {
            return DELETE_FAILED_PERMISSION_DENIED;
        }
        return DELETE_FAILED_INTERNAL_ERROR;
    }

    /**
     * whether context is system application
     * 判断是否是系统应用
     * 如果conntext不为空就会返回false
     * 否则返回isSystemApplication(context, context.getPackageName())
     */
    public static boolean isSystemApplication(Context context) {
        if (context == null) {
            return false;
        }

        return isSystemApplication(context, context.getPackageName());
    }

    /**
     * whether packageName is system application
     *
     * @param context
     * @param packageName
     * @return
     */
    /**
     *判断包是否为系统文件
     * 如果context不为空就返回false
     * 否则返回这个函数
     */
    public static boolean isSystemApplication(Context context, String packageName) {
        if (context == null) {
            return false;
        }

        return isSystemApplication(context.getPackageManager(), packageName);
    }

    /**
     *判断包是否为系统文件
     * 如果packageManager == null 或者 packageName == null 或者 packageName.length() == 0
     * 就返回false
     * 否则返回true
     */
    public static boolean isSystemApplication(PackageManager packageManager, String packageName) {
        if (packageManager == null || packageName == null || packageName.length() == 0) {
            return false;
        }

        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断包是否在栈顶
     * 你应该在manifest中添加android.permission.GET_TASKS
     * 如果params错误或者栈为空就返回空值
     * 否则返回栈顶的数据
     */
    public static Boolean isTopActivity(Context context, String packageName) {
        if (context == null || StringUtils.isEmpty(packageName)) {
            return null;
        }

        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (ListUtils.isEmpty(tasksInfo)) {
            return null;
        }
        try {
            return packageName.equals(tasksInfo.get(0).topActivity.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获得app 的codecode
     * 如果context为空则获得获得context中的信息存入pm
     * 如果pm不为空就新建信息包pi
     * 将pm中的信息保存在pi中返回
     * 否则抛出异常
     */
    public static int getAppVersionCode(Context context) {
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionCode;
                    }
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    /**
     * 获得安装地址
     * 可以通过设置获得安装路径
     * System Menu Setting->Storage->Prefered install location
     * 如果成功获得就返回这个地址
     * 否则抛出异常
     * 可以查看链接{@link IPackageManager#getInstallLocation()}
     */
    public static int getInstallLocation() {
        CommandResult commandResult = ShellUtils.execCommand(
                "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm get-install-location", false, true);
        if (commandResult.result == 0 && commandResult.successMsg != null && commandResult.successMsg.length() > 0) {
            try {
                int location = Integer.parseInt(commandResult.successMsg.substring(0, 1));
                switch (location) {
                    case APP_INSTALL_INTERNAL:
                        return APP_INSTALL_INTERNAL;
                    case APP_INSTALL_EXTERNAL:
                        return APP_INSTALL_EXTERNAL;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Log.e(TAG, "pm get-install-location error");
            }
        }
        return APP_INSTALL_AUTO;
    }

    /**
     * 获取安装位置参数
     * 如果location 为APP_INSTALL_INTERNAL 返回-f
     * 如果location为APP_INSTALL_EXTERNAL返回-s
     * f否则返回字符串""
     */
    private static String getInstallLocationParams() {
        int location = getInstallLocation();
        switch (location) {
            case APP_INSTALL_INTERNAL:
                return "-f";
            case APP_INSTALL_EXTERNAL:
                return "-s";
        }
        return "";
    }

    /**
     * 开始安装
     * 获取sdk版本
     * 根据不同的sdk版本设置安装参数intent
     */
    public static void startInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        int sdkVersion = Build.VERSION.SDK_INT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", packageName, null));
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra((sdkVersion == Build.VERSION_CODES.FROYO ? "pkg"
                    : "com.android.settings.ApplicationPkgName"), packageName);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 定义静态变量INSTALL_SUCCEEDED  为1
     */
    public static final int INSTALL_SUCCEEDED                              = 1;
    /**
     *定义静态变量INSTALL_FAILED_ALREADY_EXISTS 为-1
     * 表示这个包已经成功建立
     */
    public static final int INSTALL_FAILED_ALREADY_EXISTS                  = -1;

    /**
     * 设置静态变量INSTALL_FAILED_INVALID_APK为-2
     * 返回报的安装信息
     * 表示这个包的 文件夹无法获得.
     */
    public static final int INSTALL_FAILED_INVALID_APK                     = -2;

    /**
     * 设置静态变量INSTALL_FAILED_INVALID_URI = -3
     * 表示 URI passed 无法获得.
     */
    public static final int INSTALL_FAILED_INVALID_URI                     = -3;

    /**
     *返回值INSTALL_FAILED_INSUFFICIENT_STORAGE = -1
     *表示系统文件夹空间不足
     */
    public static final int INSTALL_FAILED_INSUFFICIENT_STORAGE            = -4;

    /**
     * 设置返回值INSTALL_FAILED_DUPLICATE_PACKAGE=-5
     * 表示存在一个同名的包
     */
    public static final int INSTALL_FAILED_DUPLICATE_PACKAGE               = -5;

    /**
     * 设置返回值INSTALL_FAILED_NO_SHARED_USER =-6
     * 表示分享用户不存在
     */
    public static final int INSTALL_FAILED_NO_SHARED_USER                  = -6;

    /**
     *设置返回值INSTALL_FAILED_UPDATE_INCOMPATIBLE  = -7
     * 表示存在一个具有相同文件的包
     */
    public static final int INSTALL_FAILED_UPDATE_INCOMPATIBLE             = -7;

    /**
     * 设置返回值INSTALL_FAILED_SHARED_USER_INCOMPATIBLE   = -8;
     * 表示安装的包存在一个不存在的文件的请求
     */
    public static final int INSTALL_FAILED_SHARED_USER_INCOMPATIBLE        = -8;

    /**
     * 设置返回值INSTALL_FAILED_MISSING_SHARED_LIBRARY   = -9
     * 表示该包使用的一个库文件不存在
     */
    public static final int INSTALL_FAILED_MISSING_SHARED_LIBRARY          = -9;

    /**
     * 设置返回值INSTALL_FAILED_REPLACE_COULDNT_DELETE   = -10
     * 表示该包要求使用的库文件不存在
     */
    public static final int INSTALL_FAILED_REPLACE_COULDNT_DELETE          = -10;

    /**
     * 设置返回值INSTALL_FAILED_DEXOPT  =-11
   *表示该包的优化出现错误，原因可能是储存空间不够或者是没有获得权限
     */
    public static final int INSTALL_FAILED_DEXOPT                          = -11;

    /**
     * 设置返回值INSTALL_FAILED_OLDER_SDK  = 12
     * 表示当前SDK版本太低
     */
    public static final int INSTALL_FAILED_OLDER_SDK                       = -12;

    /**
     * 设置返回值INSTALL_FAILED_CONFLICTING_PROVIDER=-13
     * 表示该包安装失败因为存在一个相同供应者的包
     */
    public static final int INSTALL_FAILED_CONFLICTING_PROVIDER            = -13;

    /**
     *设置返回值INSTALL_FAILED_NEWER_SDK  = -14
     * 这个包安装失败因为当前sdk的版本太新
     */
    public static final int INSTALL_FAILED_NEWER_SDK                       = -14;

    /**
     * 设置返回值INSTALL_FAILED_TEST_ONLY =-15
     * 表示安装新的包出现错误，因为这是一个测试用的包文件不可以被调用
     * the
     * flag.
     */
    public static final int INSTALL_FAILED_TEST_ONLY                       = -15;

    /**
     * 设置返回值INSTALL_FAILED_CPU_ABI_INCOMPATIBLE = -16
     * 表示该包需要本地文件，但无法获得CPU_ABI
     */
    public static
    final int INSTALL_FAILED_CPU_ABI_INCOMPATIBLE            = -16;
    /**
     * 设置返回值INSTALL_FAILED_MISSING_FEATURE                = -17
     * 这个新的包无法得到一个新的特征
     */
    public static final int INSTALL_FAILED_MISSING_FEATURE                 = -17;

    /**
     * 设置返回值INSTALL_FAILED_CONTAINER_ERROR                 = -18
     * 这个新的包无法得到一个新的特征
     */
    public static final int INSTALL_FAILED_CONTAINER_ERROR                 = -18;
    /**
     * 设置返回值INSTALL_FAILED_INVALID_INSTALL_LOCATION                = -19
     * 这个新的包不能被安装在这个特定的文件路径下
     */
    public static final int INSTALL_FAILED_INVALID_INSTALL_LOCATION        = -19;
    /** 设置返回值INSTALL_FAILED_MEDIA_UNAVAILABLE                 = -20
     * 这个新的包不能被安装在这个特定的文件路径下，因为它的媒介是不能得到的
     */
    public static final int INSTALL_FAILED_MEDIA_UNAVAILABLE               = -20;
    /** 设置返回值INSTALL_FAILED_VERIFICATION_TIMEOUT                 = -21
     * 这个新的包不能被安装，因为核实时间不恰当
     */
    public static final int INSTALL_FAILED_VERIFICATION_TIMEOUT            = -21;

    /** 设置返回值 INSTALL_FAILED_VERIFICATION_FAILURE                   = -22
     * 这个新的包不能被安装，因为核实不成功
     */
    public static final int INSTALL_FAILED_VERIFICATION_FAILURE            = -22;
    /** 设置返回值 INSTALL_FAILED_PACKAGE_CHANGED                    = -23
     * 这个新的包期待被召回
     */

    public static final int INSTALL_FAILED_PACKAGE_CHANGED                 = -23;

    /** 设置返回值 INSTALL_FAILED_PACKAGE_CHANGED                    = -24
     * 这个新的包被设置了一个之前保持的值
     */
    public static final int INSTALL_FAILED_UID_CHANGED                     = -24;

    /** 设置返回值 INSTALL_PARSE_FAILED_NOT_APK                   = -100
     * 这个新的包的安装路径不正确或者不包含apk后缀名
     */
    public static final int INSTALL_PARSE_FAILED_NOT_APK                   = -100;

    /** 设置返回值 INSTALL_PARSE_FAILED_BAD_MANIFEST              = -101
     * 这个新的包不能被manifest更改
     */
    public static final int INSTALL_PARSE_FAILED_BAD_MANIFEST              = -101;

    /** 设置返回值 INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION      = -102
     * 抛出一个期待之外的异常
     */
    public static final int INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION      = -102;

    /** 设置返回值 INSTALL_PARSE_FAILED_NO_CERTIFICATES           = -103
     * 不包含apk认证的证书
     */
    public static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES           = -103;

    /** 设置返回值INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;
     * 本地文件夹发现了apk认证的证书
     */
    public static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;

    /** 设置返回值 INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING      = -105
     * 本地文件夹发现了apk认证的证书
     */
    public static final int INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING      = -105;

    /** 设置返回值 INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME          = -106
     * 检测出manifest不包含的package
     */
    public static final int INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME          = -106;

    /** 设置返回值 INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID        = -107
     * 检测出manifest坏的分享
     */
    public static final int  INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID        = -107;

    /** 设置返回值 INSTALL_PARSE_FAILED_MANIFEST_MALFORMED        = -108
     * * 检测出manifest的结构问题
     */
    public static final int INSTALL_PARSE_FAILED_MANIFEST_MALFORMED        = -108;

    /** 设置返回值 INSTALL_PARSE_FAILED_MANIFEST_EMPTY            = -109
     * 检测出manifest不包含的可相应的tag
     */
    public static final int INSTALL_PARSE_FAILED_MANIFEST_EMPTY            = -109;

    /** 设置返回值 INSTALL_FAILED_INTERNAL_ERROR                  = -110
     * 检测出安装APK失败
     */
    public static final int INSTALL_FAILED_INTERNAL_ERROR                  = -110;
    /** 设置返回值 INSTALL_FAILED_OTHER                           = -1000000
     * 检测出其他原因
     */
    public static final int INSTALL_FAILED_OTHER                           = -1000000;

    /**
     * 未安装成功
     */
    public static final int DELETE_SUCCEEDED                               = 1;

    /**
     * 设置未安装参数DELETE_FAILED_INTERNAL_ERROR                   = -1
     * 未成功安装就删除文件
     */
    public static final int DELETE_FAILED_INTERNAL_ERROR                   = -1;

    /**
     * 设置未安装参数 DELETE_FAILED_INVALID_PACKAGE                  = -3;
     * 未成功安装，文件名无效
     */

    public static final int DELETE_FAILED_INVALID_PACKAGE                  = -3;

    /**
     * 设置未安装参数 DELETE_FAILED_PERMISSION_DENIED                = -4
     * 未成功安装，许可被禁止
     */
    public static final int DELETE_FAILED_PERMISSION_DENIED                = -4;
}
