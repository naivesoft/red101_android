/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.naivesoft.android.see.deviceinfo;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.naivesoft.android.see.deviceinfo.constant.DeviceInfoConstant;
import com.naivesoft.android.see.deviceinfo.util.CommonUtils;

import java.io.File;
import java.lang.reflect.Method;


public class EnvironmentInfo {

    private static EnvironmentInfo environmentInfo = new EnvironmentInfo();

    private EnvironmentInfo() {
    }

    /**
     * 获取EnvironmentInfo实例
     *
     * @return single instance of EnvironmentInfo
     */
    public static EnvironmentInfo getInstance() {
        return environmentInfo;
    }

    /**
     * 当前操作系统信息
     *
     * @return "Android"
     */
    public String getOSName() {
        return DeviceInfoConstant.OS_ANDROID;
    }

    /**
     * 设备是否被Root
     *
     * @return true if rooted
     */
    public boolean isRooted() {
        File file = null;
        String suPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/",
                "/vendor/bin/"};
        try {
            for (int i = 0; i < suPaths.length; i++) {
                file = new File(suPaths[i] + "su");
                if (file != null && file.exists()) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 是否在模拟器中运行
     *
     * @return true if in emulator
     */
    public boolean isEmulator(Context context) {
        try {
            // by environment
            if (Build.HARDWARE.contains("goldfish") || Build.PRODUCT.contains("sdk")
                    || Build.FINGERPRINT.contains("generic")) {
                return true;
            }

            // By IMEI
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null && CommonUtils.isZero(telephonyManager.getDeviceId())) {
                return true;
            }

            // By android ids
            String android_id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            return CommonUtils.isBlank(android_id);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Android设备主板名，ro.product.board
     *
     * @return ro.product.board
     */
    public String getProductBoard() {
        return Build.BOARD;
    }

    /**
     * Android设备品牌，ro.product.brand
     *
     * @return ro.product.brand
     */
    public String getProductBrand() {
        return Build.BRAND;
    }

    /**
     * Android设备名称，ro.product.device
     *
     * @return ro.product.device
     */
    public String getProductDevice() {
        return Build.DEVICE;
    }

    /**
     * Android设备版本号，ro.build.display.id
     *
     * @return ro.build.display.id
     */
    public String getBuildDisplayId() {
        return Build.DISPLAY;
    }

    /**
     * Android设备开发板型号，ro.build.version.incremental
     *
     * @return ro.build.version.incremental
     */
    public String getBuildVersionIncremental() {
        return Build.VERSION.INCREMENTAL;
    }

    /**
     * Android设备制造商，ro.product.manufacturer
     *
     * @return ro.product.manufacturer
     */
    public String getProductManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * Android设备可见型号，ro.product.model
     *
     * @return ro.product.model
     */
    public String getProductModel() {
        return Build.MODEL;
    }

    /**
     * Android设备正式名称，ro.product.name
     *
     * @return ro.product.name
     */
    public String getProductName() {
        return Build.PRODUCT;
    }

    /**
     * Android系统版本
     *
     * @return
     */
    public String getBuildVersionRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Android设备 SDK版本
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public String getBuildVersionSDK() {
        return Build.VERSION.SDK;
    }

    /**
     * Android设备ROM标识，ro.build.tags
     *
     * @return ro.build.tags
     */
    public String getBuildTags() {
        return Build.TAGS;
    }

    /**
     * Android设备QEMU虚拟化标识，ro.kernel.qemu
     *
     * @return ro.kernel.qemu
     */
    public String getKernelQemu() {
        return getSystemProperties("ro.kernel.qemu", "0");
    }

    /**
     * 在android.os.Build中没有提供的接口，通过反射调用android.os.SystemProperties查询
     *
     * @param propName     属性名称
     * @param defaultValue 默认值
     * @return
     */
    private String getSystemProperties(String propName, String defaultValue) {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method method = systemProperties.getMethod("get", String.class, String.class);
            return (String) method.invoke(null, propName, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
