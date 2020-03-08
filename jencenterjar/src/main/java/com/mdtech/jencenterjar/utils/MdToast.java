package com.mdtech.jencenterjar.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mdtech.jencenterjar.R;

/**
 * Created by hrs12 on 2018/4/1.
 */

public class MdToast extends Toast {
    private View view;              // 自定义的view
    private RelativeLayout firstRow;// 第一排的布局
    private ImageView toastImg;     // 图片
    private TextView toastText;     // 图片右侧的文字
    private TextView toastTextBImg; // 图片下方的文字

    public MdToast(Context context) {
        super(context);
        setGravity(Gravity.CENTER, 0, 0);
        setDuration(LENGTH_LONG);
        LayoutInflater inflater = LayoutInflater.from(context);
        if (null != inflater) {
            view = creatView(context);
            setView(view);
        }
    }

    private View creatView(Context context) {
        View view = View.inflate(context.getApplicationContext(), R.layout.metec_toast_ll,null);
        toastText = view.findViewById(R.id.tv_toast_text);
        toastImg = view.findViewById(R.id.tv_toast_tip);
        return view;
    }


    /**
     * 只显示一排文字
     *
     * @param toastTextStr
     */
    public void setHorizontalContent(final String toastTextStr) {
        setWidgetVisible(View.GONE, View.VISIBLE, View.GONE);
        setText(toastText, toastTextStr);
        show();
    }

    // 三个组件的可见性
    private void setWidgetVisible(final int toastImgVisible,
                                  final int toastTextVisible, final int toastBelowImgVisible) {
        setVisible(toastText, toastTextVisible);
        setVisible(toastImg, toastImgVisible);
        setVisible(toastTextBImg, toastBelowImgVisible);
    }

    private void setVisible(final View v, final int visible) {
        if (null != v) {
            v.setVisibility(visible);
        }
    }

    private void setText(final TextView t, final String s) {
        if (null != t && null != s) {
            t.setVisibility(View.VISIBLE);
            t.setText(s);
        }
    }

}
