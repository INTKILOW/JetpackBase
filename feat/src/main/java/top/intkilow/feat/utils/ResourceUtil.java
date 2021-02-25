package top.intkilow.feat.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

/**
 * Copyright (C), 2015/6/12, 日照安泰科技发展有限公司
 * Author: flyzhang
 * Date: 2019/10/15 11:50
 * Description: 获取资源
 * <p>
 * </p>
 * History:
 * <author>      <time>      <version>      <desc>
 * 作者姓名       修改时间     版本号         描述
 */
public class ResourceUtil {

    private ResourceUtil() {

    }

    /**
     * 获取Drawable资源
     *
     * @param resId
     * @return
     */
    public static Drawable getDrawable(Context context, @DrawableRes int resId) {
        return ContextCompat.getDrawable(context, resId);
    }

    /**
     * 获取Color资源
     *
     * @param colorId
     * @return
     */
    public static int getColor(Context context, @ColorRes int colorId) {
        return ContextCompat.getColor(context, colorId);
    }

    /**
     * 获取字符串
     *
     * @param resId
     * @return
     */
    public static String getString(Context context, @StringRes int resId) {
        return context.getResources().getString(resId);
    }
}
