/**
 * Alipay.com Inc.
 * Copyright (c) 2014 All Rights Reserved.
 */
package com.naivesoft.android.see.crypto;

import com.naivesoft.android.see.crypto.util.CommonUtils;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoUtil {

    public static final String HMAC_SHA1 = "HmacSHA1";
    public static final String HMAC_SHA_1 = "HMAC-SHA-1";
    public static final String RAW = "RAW";

    public static final byte[] DEFAULT_KEY = Hex
            .decode("7B726A5DDD72CBF8D1700FB6EB278AFD7559C40A3761E5A71614D0AC9461ED8EE9F6AAEB443CD648");

    /**
     * Forbidden constructor
     */
    private CryptoUtil() {
    }

    /**
     * Digestion with HmacSha1 method.
     *
     * @param data original data
     * @param key  key
     * @return digestion of the data
     * @throws Exception thrown when the process is failed.
     */
    public static byte[] digestWithHmacSha1(byte[] data, byte[] key) throws Exception {
        Mac hmacSha1;
        try {
            hmacSha1 = Mac.getInstance(HMAC_SHA1);
        } catch (NoSuchAlgorithmException omit) {
            hmacSha1 = Mac.getInstance(HMAC_SHA_1);
        }

        SecretKeySpec macKey = new SecretKeySpec(key, RAW);
        hmacSha1.init(macKey);
        return hmacSha1.doFinal(data);
    }

    public static String digestWithHmacSha1(String data) {

        if (CommonUtils.isBlank(data)) {
            return null;
        }

        Mac hmacSha1;
        try {
            hmacSha1 = Mac.getInstance(HMAC_SHA1);
        } catch (NoSuchAlgorithmException omit) {
            try {
                hmacSha1 = Mac.getInstance(HMAC_SHA_1);
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        }

        try {
            SecretKeySpec macKey = new SecretKeySpec(DEFAULT_KEY, RAW);
            hmacSha1.init(macKey);
            byte tmp[] = hmacSha1.doFinal(data.getBytes("UTF-8"));
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
