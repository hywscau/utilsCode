package com.mdtech.jencenterjar;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.mdtech.jencenterjar.activity.ActBean;
import com.mdtech.jencenterjar.activity.TestActivity;

/**
 * created by HYW on 2020/3/7 0007
 * Describe:
 */
public class Admanager {


    public static boolean isDebug;

    private Admanager(){

    }

    private static volatile Admanager manager;

    public static final Admanager getInstance() {
        if (manager == null) {
            synchronized (Admanager.class) {
                if (manager == null) {
                    manager = new Admanager();
                }
            }
        }
        return manager;
    }

    private void getTest() {
        DataTestBean bean = new DataTestBean();
        bean.setAge(10);
        bean.setName("hyw");
        bean.setSchool("scau");
        Log.e("hyw","DataTestBean:"+bean.toString());
        ActBean actBean = new ActBean();
    }

    public void getDataBean() {
        DataBean bean = new DataBean();
        bean.setAge(10);
        bean.setName("hyw");
        bean.setSchool("scau");
        Log.e("hyw","DataBean:"+bean.toString());

        getTest();
    }

    public void openTestActivity(Activity activity) {
        activity.startActivity(new Intent(activity, TestActivity.class));
    }

}
