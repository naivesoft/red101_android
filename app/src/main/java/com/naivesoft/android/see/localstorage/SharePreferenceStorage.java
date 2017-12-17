/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.naivesoft.android.see.localstorage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SharePreferenceStorage {

    /**
     * 向SharePreference中存储内容
     *
     * @param context
     * @param title
     * @param args
     */
    public static void writeDataToSharePreference(Context context, String title,
                                                  Map<String, String> args) {
        if (args == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(title, 0);
        SharedPreferences.Editor editor = sp.edit();
        if (editor != null) {
            editor.clear();
            for (String key : args.keySet()) {
                editor.putString(key, args.get(key));
            }
            editor.commit();
        }
    }

    /**
     * 向SharePreference中存储内容
     *
     * @param context
     * @param title
     */
    public static void writeDataToSharePreference(Context context, String title, String key,
                                                  String value) {
        if (value == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(title, 0);
        SharedPreferences.Editor editor = sp.edit();
        if (editor != null) {
            editor.clear();
            editor.putString(key, value);
            editor.commit();
        }
    }

    /**
     * 从SharePreference中读取数据
     *
     * @param context
     * @param title
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getDataFromSharePreference(Context context, String title, String key,
                                                    String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(title, 0);
        return sp.getString(key, defaultValue);
    }
}
