/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.naivesoft.android.see.crypto;

import com.naivesoft.android.see.crypto.util.CommonUtils;

import java.security.MessageDigest;

public class DigestUtil {
    /**
     * SHA1 digest
     *
     * @param str
     * @return
     */
    public static byte[] sha1ByByte(String str) {
        try {
            if (CommonUtils.isBlank(str)) {
                return null;
            }
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes("UTF-8"));
            return md.digest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * SHA1 digest
     *
     * @param str
     * @return
     */
    public static String sha1ByString(String str) {
        try {
            if (CommonUtils.isBlank(str)) {
                return null;
            }
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes("UTF-8"));
            byte tmp[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tmp.length; i++) {
                sb.append(String.format("%02x", tmp[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * MD5摘要
     *
     * @param str
     * @return
     */
    public static String MD5(String str) {
        try {
            if (CommonUtils.isBlank(str)) {
                return null;
            }
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes("UTF-8"));
            byte tmp[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(String.format("%02x", tmp[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
