package com.mdtech.jencenterjar.utils;

import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by hrs on 2018/3/19.
 */

public class AppUtils {

    public static String getInstallTime(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            long firstInstallTime = packageInfo.firstInstallTime / 1000;//应用第一次安装的时间
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return firstInstallTime + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    /**
     * 获取安装列表app包名
     * @param context
     * @return
     */
    public final static synchronized ArrayList<String> getInstalledAppsPackage(Context context) {
        ArrayList<String> apps = new ArrayList<String>();
        if (context.getPackageManager() != null) {
            List<PackageInfo> packages = getInstalledPackages(context, 0);
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                //只有非系统应用才上报
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    String appName = packageInfo.packageName;
                    apps.add(appName);
                }
            }
            return apps;
        }
        return null;
    }

    public static synchronized List<String> getInstallAppList2(Context context) {
        List<PackageInfo> pinfo = getInstalledPackages(context, 0);
        StringBuilder builder = new StringBuilder();
        StringBuilder builderAppName = new StringBuilder();
        StringBuilder lastUpdateTime = new StringBuilder();
        PackageManager packageManager = context.getPackageManager();
        if (pinfo != null) {
            int size = pinfo.size();
            for (int i = 0; i < size; i++) {
                builder.append(pinfo.get(i).packageName);
                builderAppName.append(pinfo.get(i).applicationInfo.loadLabel(packageManager).toString());
                lastUpdateTime.append(pinfo.get(i).lastUpdateTime);
                if (i < size - 1) {
                    builder.append(",");
                    builderAppName.append(",");
                    lastUpdateTime.append(",");
                }
            }
        }
        List<String> name = new ArrayList<>();
        name.add(builder.toString());
        name.add(builderAppName.toString());
        name.add(lastUpdateTime.toString());
        return name;
    }

    public  static synchronized String getInstallAppList(Context context) {
//        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = getInstalledPackages(context, 0);
        StringBuilder builder = new StringBuilder();
        JSONObject object = new JSONObject();
        if (pinfo != null) {
            int size = pinfo.size();
            for (int i = 0; i < size; i++) {
                try {
                    object.put(pinfo.get(i).packageName, getInstallTime(context, pinfo.get(i).packageName));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                builder.append(pinfo.get(i).packageName);

                if (i < size - 1)
                    builder.append(",");
            }
        }
        return object.toString();
    }

    public static List<PackageInfo> getInstalledPackages(Context context, int flags) {
        final PackageManager pm = context.getPackageManager();
        try {
            return pm.getInstalledPackages(flags);
        } catch (Exception ignored) {
            //we don't care why it didn't succeed. We'll do it using an alternative way instead
        }
        //抛异常后,备选方案通过adb命令去获取已安装的应用列表(比较耗时,所以在异常捕获的前提下优先使用上面的系统api)
        Process process;
        List<PackageInfo> result = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            process = Runtime.getRuntime().exec("pm list packages");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final String packageName = line.substring(line.indexOf(':') + 1);
                final PackageInfo packageInfo = pm.getPackageInfo(packageName, flags);
                result.add(packageInfo);
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    public static void startApp(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.e("CpaWeb","startApp Exception:"+e.getMessage());
        }
    }

    /**
     * install app
     *
     * @param context
     * @param filePath
     * @return whether apk exist
     */

    public static boolean install(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        if (!filePath.endsWith(".apk"))
            filePath += ".apk";
        if (Build.VERSION.SDK_INT >= 24) {
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ToastUtils.show(context, "应用已下载成功，官方已验证，请放心使用");
            File file = new File(filePath);
            if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
                String packageName = context.getApplicationContext().getPackageName();
                Uri contentUri = FileProvider.getUriForFile(context, packageName + ".fileprovider", new File(filePath));
                i.setDataAndType(contentUri, "application/vnd.android.package-archive");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(i);
                return true;
            }
        } else {
            File file = new File(filePath);
            ToastUtils.show(context, "应用已下载成功，官方已验证，请放心使用");
            if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
                i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
                // 让安装界面置顶
                i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                return true;
            }
        }
        return false;
    }


    public static boolean isAppInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            if (info == null) {
                return false;
            } else {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 判断当前设备中有没有“有权查看使用权限的应用”这个选项
     *
     * @param context
     * @return
     */
    public static boolean hasOption(Context context) {
        PackageManager packageManager = context.getApplicationContext()
                .getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 判断调用该设备中“有权查看使用权限的应用”这个选项的APP有没有打开
     */
    public static boolean isSwitchOpen(Context context) {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager = (UsageStatsManager) context.getApplicationContext()
                    .getSystemService(Context.USAGE_STATS_SERVICE);
        }
        if (usageStatsManager == null)
            return false;
        List<UsageStats> queryUsageStats = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            queryUsageStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_BEST, 0, ts);
        }
        if (queryUsageStats != null && queryUsageStats.size() > 0)
            return true;
        else return false;
    }

    // 将Unix时间戳转变为日期，如调用TimeStampToDate("1252639886", "yyyy-MM-dd
    // HH:mm:ss")返回值：2009-11-09 11:31:26

    public static String TimeStamp2Date(Long timestamp, String formats) {
        String date = "";
        try {
            date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));

        } catch (NumberFormatException e) {

        }
        return date;
    }

    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    private static String sLastTopApp = "";
    private static String sLastTopAppActivity = "";
    public static String[] getTopAppInfo(Context context) {
        String[] info = new String[]{sLastTopApp,sLastTopAppActivity};
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                info[0] = appTasks.get(0).topActivity.getPackageName();
                info[1] =  appTasks.get(0).topActivity.getClassName();
                return info;
            }
        } else {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 1 * 60 * 1000;
            UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            String result = "";
            String resultActivity = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = manager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                    resultActivity = event.getClassName();
                }
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                sLastTopApp = result;
            }
            if (!android.text.TextUtils.isEmpty(resultActivity)) {
                sLastTopAppActivity = resultActivity;
            }
            info[0] = sLastTopApp;
            info[1] = sLastTopAppActivity;
            return info;
        }
        return info;
    }

    public static String getLauncherTopApp(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getPackageName();
            }
        } else {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 1 * 60 * 1000;
            UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = manager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                sLastTopApp = result;
                return result;
            } else {
                return sLastTopApp;
            }
        }
        return "";
    }


    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取图标 bitmap
     * @param context
     */
    public static Bitmap getAppIconBitmap(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext()
                    .getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        Drawable d = packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap bm = bd.getBitmap();
        return bm;
    }


    /**
     * 判断是否需要打开辅助功能权限并跳转设置页
     * @param context
     * @return
     */
    public static boolean isNeedOpenAccessotySetting(Context context) {
        if (Build.VERSION.SDK_INT >= 16 && !isAccessibilitySettingsOn(context)) {
            // 引导至辅助功能设置页面
            try {
                if (android.os.Build.BRAND.toUpperCase().equals("MEIZU")) {
                    ToastUtils.show(context, "请开启无障碍功能权限");
                } else {
                    ToastUtils.show(context, "请开启辅助功能权限");
                }
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是辅助功能权限是否打开
     * @param context
     * @return
     */
    public static boolean checkAccessotyIsOpen(Context context) {
        if (Build.VERSION.SDK_INT >= 16 && !isAccessibilitySettingsOn(context)) {
            return false;
        } else {
            return true;
        }
    }


    // 此方法用来判断当前应用的辅助功能服务是否开启
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            MyLog.i("mdsdk", e.getMessage());
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }
        return false;
    }

    public static void openAccessibilityAuthority(Context context) {
        if (Build.VERSION.SDK_INT >= 16 && !AppUtils.isAccessibilitySettingsOn(context)) {
            // 引导至辅助功能设置页面
            try {
                if (android.os.Build.BRAND.toUpperCase().equals("MEIZU")) {
                    ToastUtils.show(context, "请开启无障碍功能权限");
                } else {
                    ToastUtils.show(context, "请开启辅助功能权限");
                }
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
            }
            return;
        }
    }

    /**
     * 打开应用的指定页面
     * @param context
     * @param packageName
     * @param activityFullName
     * @return
     */
    public static boolean openAppTargetPage(Context context, String packageName, String activityFullName) {
        try {
            Intent intent=new Intent();
            intent.setClassName(packageName, activityFullName);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getSHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


}
