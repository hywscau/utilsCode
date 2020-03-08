package com.mdtech.jencenterjar.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mdtech.jencenterjar.R;


/**
 * created by HYW on 2019/7/12 0012
 * Describe:
 */
public class ToastLongUtils {

    private Toast mToast;
    private Context mContext;
    private TextView textView;
    private TimeCount time;
    private String text;
    private boolean canceled = true;
    private Handler handle;
    private View view;
    private SpannableStringBuilder builderString;

    public ToastLongUtils(Context context) {
        this.mContext = context;
        mToast = new Toast(context);
        handle = new Handler();
        initView();
    }


    private void initView() {
        view = LayoutInflater.from(mContext).inflate(R.layout.mdtec_toast_long, null);
        textView = view.findViewById(R.id.tv_toast);
        mToast.setGravity(Gravity.CENTER, 0, 0);//setGravity用来设置Toast显示的位置，相当于xml中的android:gravity或android:layout_gravity
        mToast.setDuration(Toast.LENGTH_LONG);//setDuration方法：设置持续时间，以毫秒为单位。该方法是设置补间动画时间长度的主要方法
        mToast.setView(view); //添加视图文件
    }


    public void showWithAppName(int duration, String text) {
        this.text = text;
        builderString = new SpannableStringBuilder(text);
        //再构造一个改变字体颜色的Span
        ForegroundColorSpan span1 = new ForegroundColorSpan(Color.YELLOW);
        ForegroundColorSpan span2 = new ForegroundColorSpan(Color.YELLOW);
        int appNameStart = text.indexOf("[");
        int appNameEnd = text.indexOf("]");

        String timeString = "分钟";
        int timeStringStart = text.indexOf(timeString);
        //将这个Span应用于指定范围的字体
        if (appNameStart >= 0 && appNameEnd > appNameStart && appNameEnd < text.length()) {
            builderString.setSpan(span1, appNameStart, appNameEnd + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        if (timeStringStart - 1 >= 0 && timeStringStart < text.length() - timeString.length()) {
            builderString.setSpan(span2, timeStringStart - 1, timeStringStart + timeString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        textView.setText(builderString);
        time = new TimeCount(duration, 1000);
        if (canceled) {
            time.start();
            canceled = false;
            showUntilCancel();
        }
    }

    public void show(int duration, String text) {
        this.text = text;
        builderString = new SpannableStringBuilder(text);
        textView.setText(builderString);
        time = new TimeCount(duration, 1000);
        if (canceled) {
            time.start();
            canceled = false;
            showUntilCancel();
        }
    }

    public void showWithStrongText(int duration, String text, String strongText) {
        this.text = text;
        builderString = new SpannableStringBuilder(text);
        //再构造一个改变字体颜色的Span
        ForegroundColorSpan span = new ForegroundColorSpan(Color.YELLOW);
        int strongtextStart = text.indexOf(strongText);
        //将这个Span应用于指定范围的字体
        if (strongtextStart >= 0) {
            builderString.setSpan(span, strongtextStart, strongtextStart + strongText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        textView.setText(builderString);
        time = new TimeCount(duration, 1000);
        if (canceled) {
            time.start();
            canceled = false;
            showUntilCancel();
        }
    }

    /**
     * 隐藏Toast
     */
    public void hide() {
        if (mToast != null) {
            mToast.cancel();
        }
        canceled = true;
    }

    private void showUntilCancel() {
        if (canceled) {
            return;
        }
        mToast = new Toast(mContext);
        mToast.setDuration(Toast.LENGTH_LONG);//setDuration方法：设置持续时间，以毫秒为单位。该方法是设置补间动画时间长度的主要方法
        mToast.setView(view); //添加视图文件
        mToast.setGravity(Gravity.CENTER, 0, 0);

        //设置给EditText显示出来
        textView.setText(builderString);
        mToast.show();
        handle.postDelayed(new Runnable() {
            public void run() {
                showUntilCancel();
            }
        }, 3000);
    }

    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval); // 总时长,计时的时间间隔
        }

        @Override
        public void onFinish() { // 计时完毕时触发
            hide();
        }

        @Override
        public void onTick(long millisUntilFinished) { // 计时过程显示
        }

    }


}
