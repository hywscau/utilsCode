package com.mdtech.jencenterjar.utils;

import java.io.File;

/**
 * Created by hrs on 2018/3/16.
 */

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public FileUtils() {
    }


    public static boolean deleteFile(File file) {
        if (null != file && file.isFile() && file.exists()) {
            file.delete();
            return true;
        } else {
            MyLog.i(TAG, "the file is not exists: " + file.getAbsolutePath());
            return false;
        }
    }

}