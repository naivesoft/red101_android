/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.naivesoft.android.see.localstorage;

import com.naivesoft.android.see.localstorage.util.FileUtil;

public class SystemPropertyStorage {

    /**
     * 从System Property中获取数据
     *
     * @param propertyName
     * @return
     */
    public static String readDataFromSettings(String propertyName) {
        return System.getProperty(propertyName);
    }

    /**
     * 向System Property中写入数据
     *
     * @param propertyName
     * @param content
     */
    public static void writeDataToSettings(String propertyName, String content) {
        if (!FileUtil.isBlank(content)) {
            System.setProperty(propertyName, content);
        }
    }
}
