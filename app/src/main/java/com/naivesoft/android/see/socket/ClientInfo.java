package com.naivesoft.android.see.socket;

import android.content.Context;
import android.text.TextUtils;

import com.naivesoft.android.see.deviceinfo.DeviceInfo;
import com.naivesoft.android.see.localstorage.SharePreferenceStorage;

import java.util.UUID;

/**
 * Created by admin on 2017/11/11.
 */

public class ClientInfo {
    private final static String SP_FILE_TITLE = "myconfig";
    private final static String SP_KEY_DEVICEID = "dd";
    private final static String SP_KEY_USERID = "uu";
    private static ClientInfo sInstance;
    private Context mContext;
    private String mDeviceIdCache;
    private String mUserIdCache;

    private ClientInfo(Context context) {
        mContext = context;
    }

    synchronized public static ClientInfo getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ClientInfo(context);
        }
        return sInstance;
    }

    public String getUserId() {
        if (!TextUtils.isEmpty(mUserIdCache)) {
            return mUserIdCache;
        }
        mUserIdCache = SharePreferenceStorage.getDataFromSharePreference(mContext, SP_FILE_TITLE, SP_KEY_USERID, "");
        return mUserIdCache;
    }

    public void setUserId(String userId) {
        mUserIdCache = userId;
        SharePreferenceStorage.writeDataToSharePreference(mContext, SP_FILE_TITLE, SP_KEY_USERID, userId);
    }

    public String getDeviceId() {
        if (!TextUtils.isEmpty(mDeviceIdCache)) {
            return mDeviceIdCache;
        }
        String deviceId = "";
        deviceId = getDeviceIdFromSP();
        if (!TextUtils.isEmpty(deviceId)) {
            mDeviceIdCache = deviceId;
            return deviceId;
        }

        // imei
        deviceId = DeviceInfo.getInstance().getIMEI(mContext);
        if (!TextUtils.isEmpty(deviceId)) {
            mDeviceIdCache = deviceId;
            saveToSP(deviceId);
            return deviceId;
        }

        // imsi
        deviceId = DeviceInfo.getInstance().getIMSI(mContext);
        if (!TextUtils.isEmpty(deviceId)) {
            mDeviceIdCache = deviceId;
            saveToSP(deviceId);
            return deviceId;
        }

        // ifaa deviceid

        // random
        deviceId = UUID.randomUUID().toString();
        mDeviceIdCache = deviceId;
        saveToSP(deviceId);
        return deviceId;
    }

    private String getDeviceIdFromSP() {
        return SharePreferenceStorage.getDataFromSharePreference(mContext, SP_FILE_TITLE, SP_KEY_DEVICEID, "");
    }

    private void saveToSP(String deviceId) {
        SharePreferenceStorage.writeDataToSharePreference(mContext, SP_FILE_TITLE, SP_KEY_DEVICEID, deviceId);
    }
}
