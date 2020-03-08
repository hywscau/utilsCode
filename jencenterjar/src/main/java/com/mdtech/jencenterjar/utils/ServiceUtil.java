package com.mdtech.jencenterjar.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Service应用相关类 
 */
public class ServiceUtil {
	/**
	 * 用来判断服务是否运行.
	 * 
	 * @param mContext
	 * @param className
	 *            判断的服务名字
	 * @param maxNumOfService
	 *            最多获取的service个数
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext, String className,
                                           int maxNumOfService) {
		boolean isRunning = false;
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> sl = am
				.getRunningServices(maxNumOfService);
		if (!(sl.size() > 0)) {
			return false;
		}
		for (int i = 0; i < sl.size(); i++) {
			if (sl.get(i).service.getClassName().equals(className)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
