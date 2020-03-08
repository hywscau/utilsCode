package com.mdtech.jencenterjar.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebSettings;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by hrs on 2018/4/17.
 * 获取设备相关信息
 */

public class DeviceUtil {
    private static final String marshmallowMacAddress = "02:00:00:00:00:00";
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";
    /**
     * Unknown network class
     */
    public static final String NETWORK_CLASS_UNKNOWN = "UNKNOWN";

    /**
     * wifi net work
     */
    public static final String NETWORK_WIFI = "WIFI";

    /**
     * "2G" networks
     */
    public static final String NETWORK_CLASS_2_G = "2G";

    /**
     * "3G" networks
     */
    public static final String NETWORK_CLASS_3_G = "3G";

    /**
     * "4G" networks
     */
    public static final String NETWORK_CLASS_4_G = "4G";

    public static String getDeviceType() {
        String devType = "";
        devType = Build.MODEL + ",SDK: " + Build.VERSION.SDK + ",VERSION: " + Build.VERSION.RELEASE;
        return devType;
    }

    // 返回网络类型，返回整数，未知（0），2G（2），3G（3），4G（4），WiFi（1）
    public static int getNetWorkTypeInteger(Context context) {
        int netWorkType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                netWorkType = 1;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                String netWorkTypeStr = getNetWorkClass(context);
                switch (netWorkTypeStr) {
                    case NETWORK_CLASS_2_G:
                        netWorkType = 2;
                        break;
                    case NETWORK_CLASS_3_G:
                        netWorkType = 3;
                        break;
                    case NETWORK_CLASS_4_G:
                        netWorkType = 4;
                    default:
                        break;
                }
            }
        }
        return netWorkType;
    }

    public static int getProviderNameInt(Context myActivity) {
        String provider = getProvidersName(myActivity);
        int operatorType = 0;
        if (("unknow").equals(provider)) {
            operatorType = 0;
        } else if (("中国移动").equals(provider)) {
            operatorType = 1;
        } else if (("中国联通").equals(provider)) {
            operatorType = 2;
        } else if (("中国电信").equals(provider)) {
            operatorType = 3;
        }
        return operatorType;
    }

    public static String getBrand() {
        return Build.BRAND;
    }

    public static int getSystemBrightness(Context context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    public static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
                if (TextUtils.isEmpty(userAgent))
                    userAgent = "Mozilla/5.0 (Linux; U; Android 1.1; en-gb; dream) ";
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        //中文过滤
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }

    /**
     * 获取屏幕的宽度
     */
    public static String getScreenHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels + "";
    }

    /**
     * 获取屏幕的宽度
     */
    public static String getDensity(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density + "";
    }

    /**
     * 获取屏幕的宽度
     */
    public static String getScreenWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels + "";
    }

    /**
     * 获取运营商
     *
     * @return
     */
    private static String getProvidersName(Context myActivity) {
        String ProvidersName = "";
        try {
            if (ContextCompat.checkSelfPermission(myActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "unknow";
            }
            TelephonyManager telephonyManager = (TelephonyManager) myActivity.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null)
                return "中国移动";
            String IMSI = telephonyManager.getSubscriberId();
            if (IMSI == null) {
                return "unknow";
            }

            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProvidersName;
    }

    public static String getNetWorkClass(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            switch (telephonyManager.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return NETWORK_CLASS_2_G;

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return NETWORK_CLASS_3_G;

                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NETWORK_CLASS_4_G;

                default:
                    return NETWORK_CLASS_UNKNOWN;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static int hasSimCard(Context context) {
        int sim = 0;
        try {
            sim = 0;
            if (context == null)
                return sim;
            TelephonyManager manager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager == null)
                return sim;
            int simState = manager.getSimState();
            switch (simState) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    sim = 0; // 没有SIM卡
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    sim = 0;
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    sim = 1;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sim;
    }

    //ip地址
    public static String getIp(final Context context) {
        String ip = null;
        ConnectivityManager conMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        // mobile 3G Data Network
        try {
            android.net.NetworkInfo.State mobile = conMan.getNetworkInfo(
                    ConnectivityManager.TYPE_MOBILE).getState();
            // wifi
            android.net.NetworkInfo.State wifi = conMan.getNetworkInfo(
                    ConnectivityManager.TYPE_WIFI).getState();

            // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则为空
            if (mobile == android.net.NetworkInfo.State.CONNECTED
                    || mobile == android.net.NetworkInfo.State.CONNECTING) {
                ip = "";
            }
            if (wifi == android.net.NetworkInfo.State.CONNECTED
                    || wifi == android.net.NetworkInfo.State.CONNECTING) {
                //获取wifi服务
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                //判断wifi是否开启
//                if (!wifiManager.isWifiEnabled()) {
//                    wifiManager.setWifiEnabled(true);
//                }
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                ip = (ipAddress & 0xFF) + "." +
                        ((ipAddress >> 8) & 0xFF) + "." +
                        ((ipAddress >> 16) & 0xFF) + "." +
                        (ipAddress >> 24 & 0xFF);
            }
        } catch (NullPointerException e) {
        }
        return ip;
    }


    //Anroid6.0以下获取mac方法
    public static String getMacBeforeAndroidSmallow(Context context) {
        String mac = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        mac = wifiInfo.getMacAddress();
        return mac;
    }

    //Anroid6.0及以上获取mac方法
    public static String getMacAfterAndroidSmallow(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        if (wifiInf != null && marshmallowMacAddress.equals(wifiInf.getMacAddress())) {
            String result = null;
            try {
                result = getAdressMacByInterface();
                if (result != null) {
                    return result;
                } else {
                    result = getAddressMacByFile(wifiMan);
                    return result;
                }
            } catch (IOException e) {
                MyLog.w("MobileAccess", "Erreur lecture propriete Adresse MAC");
            } catch (Exception e) {
                MyLog.w("MobileAcces", "Erreur lecture propriete Adresse MAC ");
            }
        } else {
            if (wifiInf != null && wifiInf.getMacAddress() != null) {
                return wifiInf.getMacAddress();
            } else {
                return "";
            }
        }
        return marshmallowMacAddress;
    }

    private static String getAdressMacByInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            MyLog.w("MobileAcces", "Erreur lecture propriete Adresse MAC ");
        }
        return null;
    }


    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String ret;
        int wifiState = wifiMan.getWifiState();

//        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = new FileInputStream(fl);
        ret = crunchifyGetStringFromStream(fin);
        fin.close();
        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
//        wifiMan.setWifiEnabled(enabled);
        return ret;
    }

    private static String crunchifyGetStringFromStream(InputStream crunchifyStream) throws IOException {
        if (crunchifyStream != null) {
            Writer crunchifyWriter = new StringWriter();

            char[] crunchifyBuffer = new char[2048];
            try {
                Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));
                int counter;
                while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {
                    crunchifyWriter.write(crunchifyBuffer, 0, counter);
                }
            } finally {
                crunchifyStream.close();
            }
            return crunchifyWriter.toString();
        } else {
            return "No Contents";
        }
    }

    //wifi
    public static String getSsid(Context context) {
        String ssid = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        ssid = wifiInfo.getSSID();
        return ssid;
    }

    //mac地址
    public static String getBssid(Context context) {
        String bssid = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        bssid = wifiInfo.getBSSID();
        return bssid;
    }

    public static String getDeviceId(Activity context) {
        String id = "";
        //android.telephony.TelephonyManager
        try {
            TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                return "";
            }
            if (mTelephony != null && mTelephony.getDeviceId() != null) {
                id = mTelephony.getDeviceId();
            } else {
                //android.provider.Settings;
                id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("hyw","Exception:"+e.getMessage());
        }
        return id;
    }

    public static String getDeviceId(Activity context, int index) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                return tm.getDeviceId(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isRoot() {

        boolean root = false;
        try {
            if ((!new File("/system/bin/su").exists())
                    && (!new File("/system/xbin/su").exists())) {
                root = false;
            } else {
                root = true;
            }

        } catch (Exception e) {
        }
        return root;
    }

    /**
     * 利用反射获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 复制内容到剪贴板
     *
     * @param label
     * @param text
     */
    public static void copyText(Context context, String label, String text) {
        //提示
        Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText(label, text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }


    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                Log.d("result", line);
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * 获得设备硬件标识
     *
     * @param context 上下文
     * @return 设备硬件标识
     */
    public static String getDeviceIdQ(Context context) {
        StringBuilder sbDeviceId = new StringBuilder();
        //获得AndroidId（无需权限）
        String androidid = getAndroidId(context);
        //获得设备序列号（无需权限）
        String serial = getSERIAL();
        //获得硬件uuid（根据硬件相关属性，生成uuid）（无需权限）
        String uuid = getDeviceUUID().replace("-", "");
        //追加androidid
        if (!TextUtils.isEmpty(androidid)) {
            sbDeviceId.append(androidid);
            sbDeviceId.append("|");
        }
        //追加serial
        if (!TextUtils.isEmpty(serial)) {
            sbDeviceId.append(serial);
            sbDeviceId.append("|");
        }
        //追加硬件uuid
        if (!TextUtils.isEmpty(uuid)) {
            sbDeviceId.append(uuid);
        }

        //生成SHA1，统一DeviceId长度
        if (sbDeviceId.length() > 0) {
            try {
                byte[] hash = getHashByString(sbDeviceId.toString());
                String sha1 = bytesToHex(hash);
                if (sha1 != null && sha1.length() > 0) {
                    //返回最终的DeviceId
                    return sha1;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        //如果以上硬件标识数据均无法获得，
        //则DeviceId默认使用系统随机数，这样保证DeviceId不为空
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 获得设备的AndroidId
     *
     * @param context 上下文
     * @return 设备的AndroidId
     */
    private static String getAndroidId(Context context) {
        String androidId = "";
        try {
            if (isRoot()) {
                androidId = execRootCmd("settings get secure android_id");
                if (TextUtils.isEmpty(androidId)) {
                    androidId = Settings.Secure.getString(context.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
            } else {
                androidId = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return androidId;
    }

    /**
     * 获得设备序列号（如：WTK7N16923005607）, 个别设备无法获取
     *
     * @return 设备序列号
     */
    private static String getSERIAL() {
        String serial = "";
        try {
            if (isRoot()) {
                serial = execRootCmd("getprop ro.serialno");
                if (TextUtils.isEmpty(serial)) {
                    serial = Build.SERIAL;
                }
            } else {
                serial = Build.SERIAL;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return serial;
    }


    /**
     * 获得设备硬件uuid
     * 使用硬件信息，计算出一个随机数
     *
     * @return 设备硬件uuid
     */
    private static String getDeviceUUID() {
        String dev = "";
        try {
            if (isRoot()) {
                dev = "md666" + execRootCmd("getprop ro.product.board").length() % 10 +
                        execRootCmd("getprop ro.product.brand").length() % 10 +
                        execRootCmd("getprop ro.product.device").length() % 10 +
                        execRootCmd("getprop ro.hardware").length() % 10 +
                        execRootCmd("getprop ro.build.id").length() % 10 +
                        execRootCmd("getprop ro.product.model").length() % 10 +
                        execRootCmd("getprop ro.product.name").length() % 10 +
                        execRootCmd("getprop ro.serialno").length() % 10;
                if (TextUtils.isEmpty(dev)) {
                    dev = "md666" + Build.BOARD.length() % 10 +
                            Build.BRAND.length() % 10 +
                            Build.DEVICE.length() % 10 +
                            Build.HARDWARE.length() % 10 +
                            Build.ID.length() % 10 +
                            Build.MODEL.length() % 10 +
                            Build.PRODUCT.length() % 10 +
                            Build.SERIAL.length() % 10;
                }
            } else {
                dev = "3883756" + Build.BOARD.length() % 10 +
                        Build.BRAND.length() % 10 +
                        Build.DEVICE.length() % 10 +
                        Build.HARDWARE.length() % 10 +
                        Build.ID.length() % 10 +
                        Build.MODEL.length() % 10 +
                        Build.PRODUCT.length() % 10 +
                        Build.SERIAL.length() % 10;
            }
            return new UUID(dev.hashCode(),
                    getSERIAL().hashCode()).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * 取SHA1
     *
     * @param data 数据
     * @return 对应的hash值
     */
    private static byte[] getHashByString(String data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.reset();
            messageDigest.update(data.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (Exception e) {
            return "".getBytes();
        }
    }

    /**
     * 转16进制字符串
     *
     * @param data 数据
     * @return 16进制字符串
     */
    private static String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        String stmp;
        for (int n = 0; n < data.length; n++) {
            stmp = (Integer.toHexString(data[n] & 0xFF));
            if (stmp.length() == 1)
                sb.append("0");
            sb.append(stmp);
        }
        return sb.toString().toUpperCase(Locale.CHINA);
    }

}
