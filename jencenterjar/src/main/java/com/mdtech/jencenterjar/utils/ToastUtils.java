package com.mdtech.jencenterjar.utils;

import android.content.Context;

/**
 * Created by hrs12 on 2018/4/1.
 */

public class ToastUtils {
    private static MdToast mdToast;

    /**
     * 初始化pcGroupToast
     *
     * @param context
     */
    public static boolean initMdToast(final Context context) {
        if (null == context) return false;
        if (null == mdToast) {
            mdToast = new MdToast(context);
        }
        if (null != mdToast) {
            return true;
        }
        return false;
    }


    /**
     * 只显示一排文字，这个方法中所有内容都不能传递为null
     *
     * @param context
     * @param toastTextStr
     */
    public synchronized static void show(final Context context, final String toastTextStr) {
        if (context == null)
            return;
        try {
            if (initMdToast(context)) {
                mdToast.setHorizontalContent(toastTextStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
