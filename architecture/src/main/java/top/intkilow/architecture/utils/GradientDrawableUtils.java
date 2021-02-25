package top.intkilow.architecture.utils;

import android.graphics.drawable.GradientDrawable;

/**
 * Copyright (C), 2015/6/12, 日照安泰科技发展有限公司
 * Author: flyzhang
 * Date: 2020/2/25 14:49
 * Description:
 * <p>
 * </p>
 * History:
 * <author>      <time>      <version>      <desc>
 * 作者姓名       修改时间     版本号         描述
 */
public class GradientDrawableUtils {
    public static GradientDrawable getDrawable(int backgroundColor,int strokeColor,int strokeWidth,int shape,float[] f) {

        //创建Drawable对象
        GradientDrawable drawable = new GradientDrawable();
        //设置背景色
        drawable.setColor(backgroundColor);
        //设置边框的宽度以及边框的颜色
        drawable.setStroke(strokeWidth, strokeColor);
        //设置圆角的半径  
        drawable.setCornerRadii(f);
        //设置shape形状
        drawable.setShape(shape);

        return drawable;

    }

    public static float[] getRadii(int radii){

        return new float[]{radii,radii,radii,radii,radii,radii,radii,radii};
    }

    public static GradientDrawable getDrawable(int backgroundColor,int shape,float[] f) {

        //创建Drawable对象
        GradientDrawable drawable = new GradientDrawable();
        //设置背景色
        drawable.setColor(backgroundColor);
        //设置圆角的半径  
        drawable.setCornerRadii(f);
        //设置shape形状
        drawable.setShape(shape);

        return drawable;

    }

    public static GradientDrawable getDrawable(int backgroundColor,int shape,float f) {

        //创建Drawable对象
        GradientDrawable drawable = new GradientDrawable();
        //设置背景色
        drawable.setColor(backgroundColor);
        //设置圆角的半径  
        drawable.setCornerRadius(f);
        //设置shape形状
        drawable.setShape(shape);

        return drawable;

    }

}
