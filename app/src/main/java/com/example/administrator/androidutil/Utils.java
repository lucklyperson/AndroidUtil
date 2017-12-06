package com.example.administrator.androidutil;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by $wu on 2017-12-06 下午 12:00.
 * 常用工具集合
 */

public class Utils {


    /**
     * 时间戳转化为标准时间
     *
     * @param m    时间戳(毫秒)
     * @param type 要转为的标准时间类型(如 yyyy-MM-dd HH:mm:ss)
     * @return 标准时间
     */
    public static String getStandTime(long m, String type) {
        SimpleDateFormat format = new SimpleDateFormat(type);
        return format.format(new Date(m));
    }

    /**
     * 将标准时间转化为时间戳
     *
     * @param str  “2015-10-01 10:20”
     * @param type “yyyy-MM-dd HH:mm”
     * @return 格林威治时间戳
     */
    public static long getLongTime(String str, String type) {
        String stdTime = str;
        try {
            Date date = new SimpleDateFormat(type).parse(stdTime);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;

    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 获取屏幕的宽
     *
     * @param context context
     * @return int
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高
     *
     * @param context context
     * @return int
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }


    /**
     * 计算图片的缩放比
     *
     * @param options   options
     * @param reqWidth  RequestWidth
     * @param reqHeight RequestHeight
     * @return inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            //计算出实际宽高和目标宽高的比率
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) height / (float) reqHeight);
            //选择宽和高中最小的比率作为inSamplize的值，这样可以保证最终图片的宽和高
            inSampleSize = widthRatio > heightRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    /**
     * 图片的压缩（尺寸压缩和质量压缩）
     *
     * @param srcPath   源文件的路径
     * @param desPath   压缩后的保存路径
     * @param maxWidth  最大的高度
     * @param maxHeight 最大的宽度
     * @param quality   不压缩百分比（quality表示不压缩的量 100表示不压缩）
     * @throws IOException
     */
    public static void compressPicture(String srcPath, String desPath, int maxWidth, int maxHeight, int quality) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            //开始读入图片，此时把options.inJustDecodeBounds 设回true了
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(srcPath, opts);
            int w = opts.outWidth;
            int h = opts.outHeight;
            int inSampleSize = 1;
            if (w > h && w > maxWidth) {
                inSampleSize = w / maxWidth;
            } else if (w < h && h > maxHeight) {
                inSampleSize = h / maxHeight;
            }
            opts.inSampleSize = inSampleSize;
            opts.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeFile(srcPath, opts);
            OutputStream out = new FileOutputStream(desPath);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.close();
            bm.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断App是否在后台运行
     *
     * @param context context
     * @return boolean
     */
    public static boolean isBackgroundRun(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List tasks = activityManager.getRunningTasks(1);
        //获取当前应用栈中栈顶应用的包名并进行比较
        if (tasks != null && tasks.size() >= 1) {
            if (context.getPackageName().equalsIgnoreCase(((ActivityManager.RunningTaskInfo) tasks.get(0)).baseActivity.getPackageName())) {
                return false;
            }
        }
        return true;
    }


    /**
     * 获取缓存的目录
     *
     * @param context 上下文对象
     * @return cache目录的路径
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //SD卡存在，或者sd卡不可移动
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }


    /**
     * 删除目录下的文件
     *
     * @param dir 文件目录
     */
    public static void deleteOnlyDir(File dir) {
        if (dir != null && dir.isDirectory()) {

            String[] children = dir.list();

            for (int i = 0; i < children.length; i++) {

                File file = new File(dir, children[i]);
                if (file.exists()) {
                    file.delete();
                }


            }

        }

    }

}
