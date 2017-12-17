package com.naivesoft.android.see.mapblock;

import android.text.TextUtils;

import com.naivesoft.android.see.model.UserPositionInfo;

/**
 * Created by admin on 2017/11/26.
 */

public class MapBlockMessageUtil {

    public static String getUserPositionData(String userId, double longitude, double latitude, String status) {
        return userId + "^" + longitude + "^" + latitude + "^" + status;
    }

    public static UserPositionInfo getUserPositionModel(String userPositionData) {
        if (TextUtils.isEmpty(userPositionData)) {
            return null;
        }
        String[] userInfoArray = userPositionData.split("\\^");
        if (userInfoArray == null || userInfoArray.length < 4) {
            return null;
        }
        return new UserPositionInfo(userInfoArray[0], userInfoArray[1], userInfoArray[2], userInfoArray[3]);

    }
}
