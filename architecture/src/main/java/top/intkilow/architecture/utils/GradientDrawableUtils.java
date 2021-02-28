package top.intkilow.architecture.utils;

import android.graphics.drawable.GradientDrawable;

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
