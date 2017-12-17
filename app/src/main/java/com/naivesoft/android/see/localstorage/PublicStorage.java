/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.naivesoft.android.see.localstorage;

import com.naivesoft.android.see.localstorage.util.FileUtil;

import java.io.File;

public class PublicStorage {

    private final static String SDCARD_FORDER_PATH = ".SystemConfig";

    /**
     * 向公有区域存储数据，其中SD卡默认位置为.SystemConfig文件夹中
     *
     * @param publicName
     * @param content
     */
    public static void writeDataToPublicArea(String publicName, String content) {
        try {
            SystemPropertyStorage.writeDataToSettings(publicName, content);
        } catch (Throwable e) {
        }

        if (SDCardStorage.isSdCardAvailable()) {
            SDCardStorage.writeDataToSDCard(SDCARD_FORDER_PATH + File.separator + publicName,
                    content);
        }
    }

    /**
     * 读取公有区域中存储的数据，其中SD卡默认位置为.SystemConfig文件夹中
     *
     * @param publicName
     * @return
     */
    public static String readDataFromPublicArea(String publicName) {
        String content = "";
        try {
            content = SystemPropertyStorage.readDataFromSettings(publicName);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (FileUtil.isBlank(content)) {
            content = SDCardStorage.readDataFromSDCard(SDCARD_FORDER_PATH + File.separator
                    + publicName);
        }
        return content;
    }
}
