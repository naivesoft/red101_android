/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.naivesoft.android.see.localstorage;

import android.os.Environment;

import com.naivesoft.android.see.localstorage.util.FileUtil;

import java.io.File;

public class SDCardStorage {

    /**
     * 向SD卡中文件写入内容
     *
     * @param fileName 文件名
     * @param content  内容
     */
    public static void writeDataToSDCard(String fileName, String content) {
        try {
            if (isSdCardAvailable()) {
                File file = new File(Environment.getExternalStorageDirectory(), fileName);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                }
                FileUtil.writeFile(file.getAbsolutePath(), content);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 读取SD卡中文件内容
     *
     * @param fileName
     * @return
     */
    public static String readDataFromSDCard(String fileName) {
        try {
            if (isSdCardAvailable()) {
                File file = new File(Environment.getExternalStorageDirectory(), fileName);
                if (file.exists()) {
                    return FileUtil.readFile(file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * SD卡是否可写
     *
     * @return
     */
    public static boolean isSdCardAvailable() {
        String mountedProperty = Environment.getExternalStorageState();
        if (mountedProperty != null && mountedProperty.length() > 0
                && ((mountedProperty.equals("mounted")) || (mountedProperty.equals("mounted_ro")))) {
            File sdCardFile = Environment.getExternalStorageDirectory();
            if (sdCardFile != null) {
                return true;
            }
        }
        return false;
    }
}
