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

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Display;
import android.view.WindowManager;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * The main accessor for GPUImage functionality. This class helps to do common
 * tasks through a simple interface.
 * 这是GPUImage功能的主要处理函数，这个类通货一个简单的接口帮助完成常用任务。
 */
public class GPUImage {
    /**
     * 定义常量mContext，代表上下文
     * 定义常量mRenderer，代表渲染器
     * 定义常量mGlSurfaceView，其内嵌的surface专门负责OpenGL渲染
     * 定义常量mFilter，代表过滤器
     * 定义常量mCurrentBitmap，代表当前的二进制位图
     * 定义常量mScaleType，代表比例
     */
    private final Context mContext;
    private final GPUImageRenderer mRenderer;
    private GLSurfaceView mGlSurfaceView;
    private GPUImageFilter mFilter;
    private Bitmap mCurrentBitmap;
    private ScaleType mScaleType = ScaleType.CENTER_CROP;

    /**
     * Instantiates a new GPUImage object.
     * 构造函数，初始化一个新的GPUImage对象
     *
     * @param context the context 上下文参数
     */
    public GPUImage(final Context context) {
        /**
         * 如果手机不支持OpenGL ES 2.0，提示错误
         */
        if (!supportsOpenGLES2(context)) {
            throw new IllegalStateException("OpenGL ES 2.0 is not supported on this phone.");
        }

        mContext = context;
        mFilter = new GPUImageFilter();
        mRenderer = new GPUImageRenderer(mFilter);
    }

    /**
     * Checks if OpenGL ES 2.0 is supported on the current device.
     * 检查硬件是否支持OpenGL ES 2.0
     *
     * @param context the context 上下文
     * @return true, if successful 是否成功，如果成功则返回true
     */
    private boolean supportsOpenGLES2(final Context context) {
        final ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }

    /**
     * Sets the GLSurfaceView which will display the preview.
     * 设置展示用的GLSurfaceView
     *
     * @param view the GLSurfaceView
     */
    public void setGLSurfaceView(final GLSurfaceView view) {
        mGlSurfaceView = view;
        mGlSurfaceView.setEGLContextClientVersion(2);
        mGlSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGlSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mGlSurfaceView.setRenderer(mRenderer);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGlSurfaceView.requestRender();
    }

    /**
     * Request the preview to be rendered again.
     * 请求再次渲染
     */
    public void requestRender() {
        if (mGlSurfaceView != null) {
            mGlSurfaceView.requestRender();
        }
    }

    /**
     * Sets the up camera to be connected to GPUImage to get a filtered preview.
     * 设置相机与GPUImage相连，得到一个过滤获得演示结果
     * @param camera the camera 相机
     */
    public void setUpCamera(final Camera camera) {
        setUpCamera(camera, 0, false, false);
    }

    /**
     * Sets the up camera to be connected to GPUImage to get a filtered preview.
     * 设置相机与GPUImage相连，得到一个过滤获得演示结果
     *
     * @param camera the camera 相机
     * @param degrees by how many degrees the image should be rotated
     *                相片被旋转的程度
     * @param flipHorizontal if the image should be flipped horizontally
     *                       是否水平翻转
     * @param flipVertical if the image should be flipped vertically
     *                     是否垂直翻转
     */
    public void setUpCamera(final Camera camera, final int degrees, final boolean flipHorizontal,
            final boolean flipVertical) {
        /*设置渲染模式*/
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            setUpCameraGingerbread(camera);
        } else {
            camera.setPreviewCallback(mRenderer);
            camera.startPreview();
        }
        /*选择旋转角度*/
        Rotation rotation = Rotation.NORMAL;
        switch (degrees) {
            case 90:
                rotation = Rotation.ROTATION_90;
                break;
            case 180:
                rotation = Rotation.ROTATION_180;
                break;
            case 270:
                rotation = Rotation.ROTATION_270;
                break;
        }
        mRenderer.setRotationCamera(rotation, flipHorizontal, flipVertical);
    }


    /**
     * @TargetApi(11) 表示在此方法中使用Android API 11
     * 为相机设置渲染
     *
     * @param camera
     */
    @TargetApi(11)
    private void setUpCameraGingerbread(final Camera camera) {
        mRenderer.setUpSurfaceTexture(camera);
    }

    /**
     * Sets the filter which should be applied to the image which was (or will
     * be) set by setImage(...).
     * 设置应该被应用到相片上的过滤器，该方法在setImage()方法中被调用
     *
     * @param filter the new filter 新的过滤器
     */
    public void setFilter(final GPUImageFilter filter) {
        mFilter = filter;
        mRenderer.setFilter(mFilter);
        requestRender();
    }

    /**
     * Sets the image on which the filter should be applied.
     * 设置应该应用滤镜的图片
     *
     * @param bitmap the new image 新的图片的位图图像
     */
    public void setImage(final Bitmap bitmap) {
        mCurrentBitmap = bitmap;
        mRenderer.setImageBitmap(bitmap, false);
        requestRender();
    }

    /**
     * This sets the scale type of GPUImage. This has to be run before setting the image.
     * If image is set and scale type changed, image needs to be reset.
     * 设置GPUImage的比例类型，此方法必须在设置图像前被调用
     * 如果图像的已经被设置或者比例改变，则图像应该被重新设置
     *
     * @param scaleType The new ScaleType 新的比例
     */
    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
        mRenderer.setScaleType(scaleType);
        //删除渲染的图像
        mRenderer.deleteImage();
        mCurrentBitmap = null;
        requestRender();
    }

    /**
     * Sets the rotation of the displayed image.
     * 设置图像显示的翻转角度
     * @param rotation new rotation 旋转角度，枚举类
     */
    public void setRotation(Rotation rotation) {
        mRenderer.setRotation(rotation);
    }

    /**
     * Deletes the current image.
     * 删除正在渲染的图像
     */
    public void deleteImage() {
        mRenderer.deleteImage();
        mCurrentBitmap = null;
        requestRender();
    }

    /**
     * Sets the image on which the filter should be applied from a Uri.
     * 通过统一资源标识符设置需要过滤效果的图像
     *
     * @param uri the uri of the new image 新图像的URI地址
     */
    public void setImage(final Uri uri) {
        new LoadImageUriTask(this, uri).execute();
    }

    /**
     * Sets the image on which the filter should be applied from a File.
     * 从文件中获得应该设置滤镜效果的图像
     *
     * @param file the file of the new image
     *             图像所在的文件
     */
    public void setImage(final File file) {
        new LoadImageFileTask(this, file).execute();
    }

    /**
     * 通过URI获得路径
     * @param uri URI路径
     * @return 返回路径
     */
    private String getPath(final Uri uri) {
        String[] projection = {
                MediaStore.Images.Media.DATA,
        };
        Cursor cursor = mContext.getContentResolver()
                .query(uri, projection, null, null, null);
        int pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        String path = null;
        if (cursor.moveToFirst()) {
            path = cursor.getString(pathIndex);
        }
        cursor.close();
        return path;
    }

    /**
     * Gets the current displayed image with applied filter as a Bitmap.
     * 得到正在应用过滤效果的位图图象
     * @return the current image with filter applied
     *         正在应用过滤效果的位图图象
     */
    public Bitmap getBitmapWithFilterApplied() {
        return getBitmapWithFilterApplied(mCurrentBitmap);
    }

    /**
     * Gets the given bitmap with current filter applied as a Bitmap.
     *
     * @param bitmap the bitmap on which the current filter should be applied
     * @return the bitmap with filter applied
     */
    public Bitmap getBitmapWithFilterApplied(final Bitmap bitmap) {
        if (mGlSurfaceView != null) {
            mRenderer.deleteImage();
            mRenderer.runOnDraw(new Runnable() {

                @Override
                public void run() {
                    synchronized(mFilter) {
                        mFilter.destroy();
                        mFilter.notify();
                    }
                }
            });
            synchronized(mFilter) {
                requestRender();
                try {
                    mFilter.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        GPUImageRenderer renderer = new GPUImageRenderer(mFilter);
        renderer.setRotation(Rotation.NORMAL,
                mRenderer.isFlippedHorizontally(), mRenderer.isFlippedVertically());
        renderer.setScaleType(mScaleType);
        PixelBuffer buffer = new PixelBuffer(bitmap.getWidth(), bitmap.getHeight());
        buffer.setRenderer(renderer);
        renderer.setImageBitmap(bitmap, false);
        Bitmap result = buffer.getBitmap();
        mFilter.destroy();
        renderer.deleteImage();
        buffer.destroy();

        mRenderer.setFilter(mFilter);
        if (mCurrentBitmap != null) {
            mRenderer.setImageBitmap(mCurrentBitmap, false);
        }
        requestRender();

        return result;
    }

    /**
     * Gets the images for multiple filters on a image. This can be used to
     * quickly get thumbnail images for filters. <br>
     * Whenever a new Bitmap is ready, the listener will be called with the
     * bitmap. The order of the calls to the listener will be the same as the
     * filter order.
     *
     * @param bitmap the bitmap on which the filters will be applied
     * @param filters the filters which will be applied on the bitmap
     * @param listener the listener on which the results will be notified
     */
    public static void getBitmapForMultipleFilters(final Bitmap bitmap,
            final List<GPUImageFilter> filters, final ResponseListener<Bitmap> listener) {
        if (filters.isEmpty()) {
            return;
        }
        GPUImageRenderer renderer = new GPUImageRenderer(filters.get(0));
        renderer.setImageBitmap(bitmap, false);
        PixelBuffer buffer = new PixelBuffer(bitmap.getWidth(), bitmap.getHeight());
        buffer.setRenderer(renderer);

        for (GPUImageFilter filter : filters) {
            renderer.setFilter(filter);
            listener.response(buffer.getBitmap());
            filter.destroy();
        }
        renderer.deleteImage();
        buffer.destroy();
    }

    /**
     * Deprecated: Please use
     * {@link GPUImageView#saveToPictures(String, String, jp.co.cyberagent.android.gpuimage.GPUImageView.OnPictureSavedListener)}
     *
     * Save current image with applied filter to Pictures. It will be stored on
     * the default Picture folder on the phone below the given folderName and
     * fileName. <br>
     * This method is async and will notify when the image was saved through the
     * listener.
     *
     * @param folderName the folder name
     * @param fileName the file name
     * @param listener the listener
     */
    @Deprecated
    public void saveToPictures(final String folderName, final String fileName,
            final OnPictureSavedListener listener) {
        saveToPictures(mCurrentBitmap, folderName, fileName, listener);
    }

    /**
     * Deprecated: Please use
     * {@link GPUImageView#saveToPictures(String, String, jp.co.cyberagent.android.gpuimage.GPUImageView.OnPictureSavedListener)}
     *
     * Apply and save the given bitmap with applied filter to Pictures. It will
     * be stored on the default Picture folder on the phone below the given
     * folerName and fileName. <br>
     * This method is async and will notify when the image was saved through the
     * listener.
     *
     * @param bitmap the bitmap
     * @param folderName the folder name
     * @param fileName the file name
     * @param listener the listener
     */
    @Deprecated
    public void saveToPictures(final Bitmap bitmap, final String folderName, final String fileName,
            final OnPictureSavedListener listener) {
        new SaveTask(bitmap, folderName, fileName, listener).execute();
    }

    /**
     * Runs the given Runnable on the OpenGL thread.
     *
     * @param runnable The runnable to be run on the OpenGL thread.
     */
    void runOnGLThread(Runnable runnable) {
        mRenderer.runOnDrawEnd(runnable);
    }

    /**
     * 获得输出的宽度
     * @return 返回输出宽度
     */
    private int getOutputWidth() {
        if (mRenderer != null && mRenderer.getFrameWidth() != 0) {
            return mRenderer.getFrameWidth();
        } else if (mCurrentBitmap != null) {
            return mCurrentBitmap.getWidth();
        } else {
            WindowManager windowManager =
                    (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            return display.getWidth();
        }
    }

    /**
     * 获得输出的高度
     * @return 返回输出的高度
     */
    private int getOutputHeight() {
        if (mRenderer != null && mRenderer.getFrameHeight() != 0) {
            return mRenderer.getFrameHeight();
        } else if (mCurrentBitmap != null) {
            return mCurrentBitmap.getHeight();
        } else {
            WindowManager windowManager =
                    (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            return display.getHeight();
        }
    }

    /**
     * 定义一个内部类SaveTask，继承AsyncTask类，用于保存图片。
     *     AsyncTask类介绍：
     *       Android UI是线程不安全的，如果想要在子线程里进行UI操作，
     *     就需要借助Android的异步消息处理机制。不过为了更加方便我们在子线程中更新UI元素，
     *     Android从1.5版本就引入了一个AsyncTask类，使用它就可以非常灵活方便地从子线程切
     *     换到UI线程。
     *       AsyncTask 本质是用handler更新界面；在3.0版本以后，它在AsyncTask中是以常量的形
     *     式被使用的，因此在整个应用程序中的所有AsyncTask实例都会共用同一个SerialExecutor。
     */
    @Deprecated
    private class SaveTask extends AsyncTask<Void, Void, Void> {

        private final Bitmap mBitmap;
        private final String mFolderName;
        private final String mFileName;
        private final OnPictureSavedListener mListener;
        /*Handler的对象用于更新界面*/
        private final Handler mHandler;

        /**
         * 类的构造函数
         *
         * @param bitmap 位图常量
         * @param folderName 文件夹名
         * @param fileName 文件名
         * @param listener
         */
        public SaveTask(final Bitmap bitmap, final String folderName, final String fileName,
                final OnPictureSavedListener listener) {
            mBitmap = bitmap;
            mFolderName = folderName;
            mFileName = fileName;
            mListener = listener;
            /*定义mHandler对象，是Handler类的实例，用于更新对象*/
            mHandler = new Handler();
        }

        /**
         * 重写继承类中的方法：doInBackground(后台调用)
         * @param params 常量参数
         *               Void...等价于 Void[] params，支持多个参数
         * @return 返回值为空
         */
        @Override
        protected Void doInBackground(final Void... params) {
            /*利用GPUImage中的方法getBitmapWithFilterApplied
                对位图常量mBitmap进行处理，得到处理后的位图结果*/
            Bitmap result = getBitmapWithFilterApplied(mBitmap);
            /*按照对象的文件夹名，文件名和得到的位图保存图片*/
            saveImage(mFolderName, mFileName, result);
            return null;
        }

        /**
         * 保存处理后图片
         * @param folderName 文件夹名
         * @param fileName 文件名
         * @param image 处理后的位图图像
         */
        private void saveImage(final String folderName, final String fileName, final Bitmap image) {
            /*获得android图片的保存路径*/
            File path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            /*利用上述获得的路径以及文件夹名和文件名，
                在内存中创建文件*/
            File file = new File(path, folderName + "/" + fileName);
            try {
                /*先调用getParentFile()获得父目录，
                    用.mkdirs()生成父目录文件夹，最后把文件生成到这个文件夹下面*/
                file.getParentFile().mkdirs();
                /*调用compress方法压缩位图文件，
                    其中80代表压缩后的程度，表示压缩率为20%。*/
                image.compress(CompressFormat.JPEG, 80, new FileOutputStream(file));
                /*调用多媒体扫描服务的静态方法scanFile，扫描文件，
                    实现主动通知android图片库进行更新的功能*/
                MediaScannerConnection.scanFile(mContext,
                        new String[] {
                            file.toString()
                        }, null,
                        /**
                         *
                         */
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(final String path, final Uri uri) {
                                if (mListener != null) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mListener.onPictureSaved(uri);
                                        }
                                    });
                                }
                            }
                        });
            } catch (FileNotFoundException e) {
                /*抛出IO异常*/
                e.printStackTrace();
            }
        }
    }

    /**
     * 定义接口OnPictureSavedListener，
     *     对图片保存进行监听
     */
    public interface OnPictureSavedListener {
        void onPictureSaved(Uri uri);
    }

    /**
     * 创建内部类LoadImageUriTask，继承LoadImageTask
     */
    private class LoadImageUriTask extends LoadImageTask {

        private final Uri mUri;

        public LoadImageUriTask(GPUImage gpuImage, Uri uri) {
            super(gpuImage);
            mUri = uri;
        }

        /**
         * 重写继承类中的decode方法，按需进行解码
         * @param options 选项
         * @return 返回位图
         */
        @Override
        protected Bitmap decode(BitmapFactory.Options options) {
            try {
                InputStream inputStream;
                if (mUri.getScheme().startsWith("http") || mUri.getScheme().startsWith("https")) {
                    inputStream = new URL(mUri.toString()).openStream();
                } else {
                    inputStream = mContext.getContentResolver().openInputStream(mUri);
                }
                return BitmapFactory.decodeStream(inputStream, null, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 重写继承类中的getImageOrientation方法，获得图像的方向
         * @return 图像的方向值
         * @throws IOException
         */
        @Override
        protected int getImageOrientation() throws IOException {
            Cursor cursor = mContext.getContentResolver().query(mUri,
                    new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

            if (cursor == null || cursor.getCount() != 1) {
                return 0;
            }

            cursor.moveToFirst();
            int orientation = cursor.getInt(0);
            cursor.close();
            return orientation;
        }
    }

    /**
     * 内部类LoadImageFileTask，继承自LoadImageTask，完成载入图像的任务
     */
    private class LoadImageFileTask extends LoadImageTask {

        private final File mImageFile;

        public LoadImageFileTask(GPUImage gpuImage, File file) {
            super(gpuImage);
            mImageFile = file;
        }

        /**
         * 重写继承类中的decode方法，按需进行解码
         * @param options 选项
         * @return 返回位图
         */
        @Override
        protected Bitmap decode(BitmapFactory.Options options) {
            return BitmapFactory.decodeFile(mImageFile.getAbsolutePath(), options);
        }

        /**
         * 重写继承类中的getImageOrientation方法，获得图像的方向
         * @return 图像的方向值
         * @throws IOException
         */
        @Override
        protected int getImageOrientation() throws IOException {
            ExifInterface exif = new ExifInterface(mImageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    return 0;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        }
    }

    /**
     * 定义抽象类LoadImageTask，继承自AsyncTask，用于载入图像
     */
    private abstract class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {

        private final GPUImage mGPUImage;
        private int mOutputWidth;
        private int mOutputHeight;

        /**
         * 构造函数，用于初始化GPUImage的对象
         * @param gpuImage GPUImage对象
         */
        @SuppressWarnings("deprecation")
        public LoadImageTask(final GPUImage gpuImage) {
            mGPUImage = gpuImage;
        }

        /**
         * 在后台完成工作
         * @param params 参数
         * @return 位图
         */
        @Override
        protected Bitmap doInBackground(Void... params) {
            if (mRenderer != null && mRenderer.getFrameWidth() == 0) {
                try {
                    synchronized (mRenderer.mSurfaceChangedWaiter) {
                        mRenderer.mSurfaceChangedWaiter.wait(3000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mOutputWidth = getOutputWidth();
            mOutputHeight = getOutputHeight();
            return loadResizedImage();
        }

        /**
         * 执行POST命令
         * @param bitmap 位图
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mGPUImage.deleteImage();
            mGPUImage.setImage(bitmap);
        }

        /**
         * 抽象方法decode，不含该方法的实现
         * @param options 操作
         * @return 位图
         */
        protected abstract Bitmap decode(BitmapFactory.Options options);

        /**
         * 重新获得图像的size
         * @return 计算后的位图
         */
        private Bitmap loadResizedImage() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            decode(options);
            int scale = 1;
            while (checkSize(options.outWidth / scale > mOutputWidth, options.outHeight / scale > mOutputHeight)) {
                scale++;
            }

            scale--;
            if (scale < 1) {
                scale = 1;
            }
            options = new BitmapFactory.Options();
            options.inSampleSize = scale;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inTempStorage = new byte[32 * 1024];
            Bitmap bitmap = decode(options);
            if (bitmap == null) {
                return null;
            }
            bitmap = rotateImage(bitmap);
            bitmap = scaleBitmap(bitmap);
            return bitmap;
        }

        /**
         * scaleBitmap方法用于调整位图的规模
         * @param bitmap 原始位图
         * @return 新的位图
         */
        private Bitmap scaleBitmap(Bitmap bitmap) {
            // resize to desired dimensions 调整到所需尺寸
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] newSize = getScaleSize(width, height);
            Bitmap workBitmap = Bitmap.createScaledBitmap(bitmap, newSize[0], newSize[1], true);
            if (workBitmap != bitmap) {
                bitmap.recycle();
                bitmap = workBitmap;
                System.gc();
            }

            if (mScaleType == ScaleType.CENTER_CROP) {
                // Crop it
                int diffWidth = newSize[0] - mOutputWidth;
                int diffHeight = newSize[1] - mOutputHeight;
                workBitmap = Bitmap.createBitmap(bitmap, diffWidth / 2, diffHeight / 2,
                        newSize[0] - diffWidth, newSize[1] - diffHeight);
                if (workBitmap != bitmap) {
                    bitmap.recycle();
                    bitmap = workBitmap;
                }
            }

            return bitmap;
        }

        /**
         * Retrieve the scaling size for the image dependent on the ScaleType.<br>
         *     获得图像的大小
         * <br>
         * If CROP: sides are same size or bigger than output's sides<br>
         * Else   : sides are same size or smaller than output's sides
         */
        private int[] getScaleSize(int width, int height) {
            float newWidth;
            float newHeight;

            float withRatio = (float) width / mOutputWidth;
            float heightRatio = (float) height / mOutputHeight;

            boolean adjustWidth = mScaleType == ScaleType.CENTER_CROP
                    ? withRatio > heightRatio : withRatio < heightRatio;

            if (adjustWidth) {
                newHeight = mOutputHeight;
                newWidth = (newHeight / height) * width;
            } else {
                newWidth = mOutputWidth;
                newHeight = (newWidth / width) * height;
            }
            return new int[]{Math.round(newWidth), Math.round(newHeight)};
        }

        private boolean checkSize(boolean widthBigger, boolean heightBigger) {
            if (mScaleType == ScaleType.CENTER_CROP) {
                return widthBigger && heightBigger;
            } else {
                return widthBigger || heightBigger;
            }
        }

        private Bitmap rotateImage(final Bitmap bitmap) {
            if (bitmap == null) {
                return null;
            }
            Bitmap rotatedBitmap = bitmap;
            try {
                int orientation = getImageOrientation();
                if (orientation != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(orientation);
                    rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rotatedBitmap;
        }

        protected abstract int getImageOrientation() throws IOException;
    }

    public interface ResponseListener<T> {
        void response(T item);
    }

    public enum ScaleType { CENTER_INSIDE, CENTER_CROP }
}
