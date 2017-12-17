package com.naivesoft.android.see.model;

/**
 * Created by admin on 2017/11/26.
 */

public class UserPositionInfo {

    public String userId;
    public double longtitude;
    public double latitude;
    public String status;

    public UserPositionInfo(String userId, double longtitude, double latitude, String status) {
        this.userId = userId;
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.status = status;
    }

    public UserPositionInfo(String userId, String longtitude, String latitude, String status) {
        this.userId = userId;
        this.longtitude = Double.parseDouble(longtitude);
        this.latitude = Double.parseDouble(latitude);
        this.status = status;
    }
}
