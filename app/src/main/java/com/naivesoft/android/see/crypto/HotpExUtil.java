/**
 * Alipay.com Inc.
 * Copyright (c) 2014 All Rights Reserved.
 */
package com.naivesoft.android.see.crypto;

public final class HotpExUtil {

    // SALT1 and SALT2 for HAC-SHA1 digesting, MUST BE THE EXACTLY SAME WITH THE CLIENT SIDE.
    public static final byte[] salt1 = Hex
            .decode("7B726A5DDD72CBF8D1700FB6EB278AFD7559C40A3761E5A71614D0AC9461ED8EE9F6AAEB443CD648");
    public static final byte[] salt2 = Hex
            .decode("C9582A82777392CAA65AD7F5228150E3F966C09D6A00288B5C6E0CFB441E111B713B4E0822A8C830");
    public static final int MAX_CODE_LEN = 8;
    public static final int HOTPEX_HASH_LEN = 20;

    /**
     * Forbidden constructor
     */
    private HotpExUtil() {
    }

    /**
     * Do hotpifying digesting on data.
     *
     * @param data data
     * @return hotpifying codes
     * @throws Exception thrown when HMAC-SHA1 is failed.
     */
    public static byte[] process(byte[] data) throws Exception {

        byte[] hash1 = new byte[HOTPEX_HASH_LEN];
        boolean init = ByteUtil.initWithByte(hash1, (byte) 0x00, 0, hash1.length);
        if (!init) {
            throw new IllegalStateException("failed to init hash1.");
        }

        byte[] hash2 = new byte[HOTPEX_HASH_LEN];
        init = ByteUtil.initWithByte(hash2, (byte) 0x00, 0, hash2.length);
        if (!init) {
            throw new IllegalStateException("failed to init hash2.");
        }

        byte[] temp_hash1 = CryptoUtil.digestWithHmacSha1(data, salt1);
        System.arraycopy(temp_hash1, 0, hash1, 0, temp_hash1.length);

        byte[] temp_hash2 = CryptoUtil.digestWithHmacSha1(data, salt2);
        System.arraycopy(temp_hash2, 0, hash2, 0, temp_hash2.length);

        byte[] code = new byte[MAX_CODE_LEN];

        // zip and fold
        int offset = hash1[HOTPEX_HASH_LEN - 1] & 0x0f;
        code[3] = (byte) (hash1[offset] & 0x7f);
        code[2] = (byte) (hash1[offset + 1] & 0xff);
        code[1] = (byte) (hash1[offset + 2] & 0xff);
        code[0] = (byte) (hash1[offset + 3] & 0xff);

        offset = hash2[HOTPEX_HASH_LEN - 1] & 0x0f;
        code[4] = (byte) (hash2[offset] & 0x7f);
        code[5] = (byte) (hash2[offset + 1] & 0xff);
        code[6] = (byte) (hash2[offset + 2] & 0xff);
        code[7] = (byte) (hash2[offset + 3] & 0xff);

        return code;
    }

    public static byte[] process(byte[] data, int length) throws Exception {

        byte[] hotpexCode = process(data);

        if (hotpexCode == null) {
            return null;
        }

        if (length <= 0) {
            return null;
        }

        if (length >= MAX_CODE_LEN) {
            return hotpexCode;
        }

        // rebuild return byte array
        byte[] reHotpexCode = new byte[length];

        for (int i = 0; i < length; i++) {
            reHotpexCode[i] = hotpexCode[i];
        }
        return reHotpexCode;
    }

}
