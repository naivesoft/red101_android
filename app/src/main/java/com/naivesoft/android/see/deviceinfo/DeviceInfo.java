/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.naivesoft.android.see.deviceinfo;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.naivesoft.android.see.deviceinfo.util.CommonUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

public class DeviceInfo {

    private static DeviceInfo deviceInfo = new DeviceInfo();

    private DeviceInfo() {
    }

    /**
     * 获取DeviceInfo实例
     *
     * @return single instance of DeviceInfo
     */
    public static DeviceInfo getInstance() {
        return deviceInfo;
    }

    /**
     * 获取设备IMEI
     *
     * @param context
     * @return IMEI
     */
    public String getIMEI(Context context) {
        String imei = null;
        if (context != null) {
            try {
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) {
                    imei = tm.getDeviceId();
                }
            } catch (Exception e) {
            }
        }
        return imei;
    }

    /**
     * 获取设备IMSI
     *
     * @param context
     * @return IMSI
     */
    public String getIMSI(Context context) {
        String imsi = null;
        if (context != null) {
            try {
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null)
                    imsi = tm.getSubscriberId();
            } catch (Exception e) {
            }
        }
        return imsi;
    }

    /**
     * 获取设备传感器列表摘要
     *
     * @param context
     * @return Sensor Digest
     */
    public String getSensorDigest(Context context) {
        String sensorDigest = null;
        if (context != null) {
            try {
                SensorManager sensorManager = (SensorManager) context
                        .getSystemService(Context.SENSOR_SERVICE);
                if (sensorManager != null) {
                    List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
                    if (sensorList != null && sensorList.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (Sensor sensor : sensorList) {
                            sb.append(sensor.getName());
                            sb.append(sensor.getVersion());
                            sb.append(sensor.getVendor());
                        }
                        sensorDigest = CommonUtils.sha1ByString(sb.toString());
                    }
                }
            } catch (Exception e) {
            }
        }
        return sensorDigest;
    }

    /**
     * 获取设备分辨率
     *
     * @param context Context
     * @return 分辨率
     */
    public String getScreenResolution(Context context) {
        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            String dp = Integer.toString(dm.widthPixels) + "*" + Integer.toString(dm.heightPixels);
            return dp;
        } catch (Exception e) {
        }
        return null;

    }

    public String getScreenDpi(Context context) {
        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return "" + dm.densityDpi;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return width
     */
    public String getScreenWidth(Context context) {
        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return "" + dm.widthPixels;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return height
     */
    public String getScreenHeight(Context context) {
        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return "" + dm.heightPixels;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取MAC地址
     *
     * @param context
     * @return MAC Address
     */
    public String getMACAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getMacAddress();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取SIM卡序列号
     *
     * @return SIM Serial
     */
    public String getSIMSerial(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimSerialNumber();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取Android id
     *
     * @param context
     * @return Android id
     */
    public String getAndroidID(Context context) {
        try {
            return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * CPU的标识
     *
     * @return cpu id
     */
    public String getCPUSerial() {
        String cpuAddress = "0000000000000000";
        try {
            // 读取CPU信息
            Process process = Runtime.getRuntime().exec("cat /proc/cpuinfo | grep Serial");
            InputStreamReader reader = new InputStreamReader(process.getInputStream());
            LineNumberReader lineNumberReader = new LineNumberReader(reader);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++) {
                String str = lineNumberReader.readLine();
                if (str != null) {
                    // 查找到序列号所在行
                    if (str.indexOf("Serial") > -1) {
                        // 提取序列号
                        String strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        // 去空格
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    // 文件结尾
                    break;
                }
            }
        } catch (Exception exception) {
        }
        return cpuAddress;
    }

    /**
     * 获取CPU数量
     *
     * @return cpu count
     */
    public String getCpuCount() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                return Pattern.matches("cpu[0-9]+", pathname.getName());
            }
        }
        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return String.valueOf(files.length);
        } catch (Exception e) {
            //Default to return 1 core
            return "1";
        }
    }

    /**
     * 获取CPU频率
     *
     * @return cpu frequent
     */
    public String getCpuFrequent() {
        String cpufre1 = getCpuFreq1();
        if (!CommonUtils.isBlank(cpufre1)) {
            return cpufre1;
        }

        return getCpuFreq2();
    }

    /**
     * 获取/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq位置记录的CPU频率
     */
    private String getCpuFreq1() {
        String cpuInfoFile = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
        try {
            FileReader fileReader = new FileReader(cpuInfoFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
            String frequent = bufferedReader.readLine();
            bufferedReader.close();
            fileReader.close();
            if (!CommonUtils.isBlank(frequent)) {
                return frequent.trim();
            }
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * 获取/proc/cpuinfo位置记录的CPU频率
     */
    private String getCpuFreq2() {
        String cpuInfoFile = "/proc/cpuinfo";
        try {
            FileReader fileReader = new FileReader(cpuInfoFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
            String tmp = null;
            String freq = null;
            while ((tmp = bufferedReader.readLine()) != null) {
                if (!CommonUtils.isBlank(tmp)) {
                    String[] tmpar = tmp.split(":");
                    if (tmpar != null && tmpar.length > 1) {
                        if (tmpar[0].contains("BogoMIPS")) {
                            freq = tmpar[1].trim();
                            break;
                        }
                    }
                }
            }
            bufferedReader.close();
            fileReader.close();
            return freq;
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * 获取物理内存大小
     *
     * @return memory size
     */
    public String getMemorySize() {
        String memInfoFile = "/proc/meminfo";
        long initialMemory = 0;
        BufferedReader bufferedReader = null;
        try {
            FileReader fileReader = new FileReader(memInfoFile);
            bufferedReader = new BufferedReader(fileReader, 8192);
            String line = bufferedReader.readLine();
            if (line != null) {
                initialMemory = Integer.parseInt(line.split("\\s+")[1]);
            }
        } catch (IOException e) {
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
            }
        }
        return "" + initialMemory;
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public String getTotalInternalMemorySize() {
        long internalSize = 0L;
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long bSize = stat.getBlockSize();
            long bCount = stat.getBlockCount();
            internalSize = bCount * bSize;
        } catch (Exception e) {
        }
        return "" + internalSize;
    }

    /**
     * 获取SD卡容量
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public String getSDCardSize() {
        long sdCardInfo = 0L;
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File sdcardDir = Environment.getExternalStorageDirectory();
                StatFs sf = new StatFs(sdcardDir.getPath());
                long bSize = sf.getBlockSize();
                long bCount = sf.getBlockCount();
                sdCardInfo = bSize * bCount;
            }
        } catch (Exception e) {
        }
        return "" + sdCardInfo;
    }

    /**
     * 获取蓝牙地址
     *
     * @return
     */
    public String getBluMac() {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter != null) {
                if (!adapter.isEnabled()) {
                    return "";
                }
            }
            return adapter.getAddress();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取网络制式
     *
     * @param context
     * @return
     */
    public String getNetworkType(Context context) {
        try {
            TelephonyManager phoneMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (phoneMgr != null) {
                return String.valueOf(phoneMgr.getNetworkType());
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取基带编号
     *
     * @return
     */
    public String getBandVer() {
        String ver = null;
        try {
            Class<?> cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", String.class, String.class);
            Object result = m
                    .invoke(invoker, "gsm.version.baseband", "no message");
            ver = (String) result;
        } catch (Exception e) {
        }
        return ver;
    }

    /**
     * 获取已连接的Wifi的Mac地址
     *
     * @return
     */
    public String getWifiBssid(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wm.isWifiEnabled()) {
                WifiInfo wi = wm.getConnectionInfo();
                return wi.getBSSID();
            }
        } catch (Throwable e) {
        }
        return "";
    }
}
