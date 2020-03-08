package com.mdtech.jencenterjar;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.mdtech.jencenterjar.activity.TestActivity;

/**
 * created by HYW on 2020/3/7 0007
 * Describe:
 */
public class TesytClass {

    private TesytClass(){

    }

    private static volatile TesytClass manager;

    public static final TesytClass getInstance() {
        if (manager == null) {
            synchronized (TesytClass.class) {
                if (manager == null) {
                    manager = new TesytClass();
                }
            }
        }
        return manager;
    }

    private void getAdmanager() {
//        Admanager bean = Admanager.getInstance();
//        bean.getDataBean();
    }

}
