/**
 * Alipay.com Inc.
 * Copyright (c) 2014 All Rights Reserved.
 */
package com.naivesoft.android.see.crypto;

public class ByteUtil {

    /**
     * Forbidden constructor
     */
    private ByteUtil() {
    }

    /**
     * Initialize a byte buffer with a byte value.
     *
     * @param buf   byte buffer
     * @param aByte byte value
     * @param from  start position
     * @param len   length of buffer to be initialized
     * @return true if succeed, false if failed.
     */
    public static boolean initWithByte(byte[] buf, byte aByte, int from, int len) {
        if (null == buf) {
            return false;
        }

        if (buf.length < from + len) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            buf[from + i] = aByte;
        }

        return true;
    }

    /**
     * append source byte array to  dest byte array at index
     *
     * @param dest
     * @param source
     * @param index
     * @return
     */
    public static boolean appendByteArray(byte[] dest, byte[] source, int index) {
        if (dest == null || source == null) {
            return false;
        }

        if (index >= dest.length) {
            return true;
        }

        for (int i = 0; i < source.length; i++) {
            if (i + index + 1 > dest.length) {
                break;
            }
            dest[i + index] = source[i];
        }
        return true;
    }

    /**
     * Compare two byte buffers to see if they are the same byte by byte.
     *
     * @param src      first byte buffer
     * @param srcFrom  start position of first byte buffer
     * @param dest     second byte buffer
     * @param destFrom start position of second byte buffer
     * @param len      length to be compared
     * @return true if they are same byte by byte, else false.
     */
    public static boolean isTheSame(byte[] src, int srcFrom, byte[] dest, int destFrom, int len) {
        if (null == src || null == dest || len <= 0) {
            return false;
        }

        if (src.length < srcFrom + len) {
            return false;
        }

        if (dest.length < destFrom + len) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (src[srcFrom + i] != dest[destFrom + i]) {
                return false;
            }
        }

        return true;
    }
}
