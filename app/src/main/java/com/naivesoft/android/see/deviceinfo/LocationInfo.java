/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.naivesoft.android.see.deviceinfo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Base64;
import android.util.Log;

import com.naivesoft.android.see.deviceinfo.constant.DeviceInfoConstant;
import com.naivesoft.android.see.deviceinfo.listener.SecLocationListener;
import com.naivesoft.android.see.deviceinfo.util.CommonUtils;

public class LocationInfo {

    /**
     * 经度
     */
    private String longitude;
    /**
     * 维度
     */
    private String latitude;
    /**
     * Wi-Fi的地址
     */
    private String bssid;
    /**
     * Wi-Fi的名称
     */
    private String ssid;
    /**
     * Wi-Fi是否已经连接上
     */
    private String isWifiActive;
    /**
     * Wi-Fi信号强度
     */
    private String wifiStrength;
    /**
     * 基站国家代码
     */
    private String mcc;
    /**
     * 基站网络代码
     */
    private String mnc;
    /**
     * Cell ID
     */
    private String cellId;
    /**
     * location area code
     */
    private String lac;

    private LocationInfo() {
    }

    /**
     * 获取LocationInfo实例
     *
     * @return single instance of LocationInfo
     */
    public static LocationInfo getLocationInfo(Context context) {
        LocationInfo locationInfo = new LocationInfo();
        setLocationInfos(context, locationInfo);
        setWifiInfos(context, locationInfo);
        return locationInfo;
    }

    /**
     * 位置信息采集
     *
     * @param context
     * @return
     */
    private static void setLocationInfos(Context context, LocationInfo locationInfo) {

        try {
            // 标记network位置信息是否采集到
            boolean isNetworkLocated = false;

            // 获取LocationManager服务
            LocationManager locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

            // 网络定位是否可用
            if (locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                LocationListener ls = new SecLocationListener();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        DeviceInfoConstant.REQUEST_LOCATE_INTERVAL, 0, ls, Looper.getMainLooper());
                locationManager.removeUpdates(ls);

                // 获取网络定位信息
                Location locationdata = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (locationdata != null) {
                    // 通过网络采集到位置
                    isNetworkLocated = true;
                    locationInfo.setLatitude("" + locationdata.getLatitude());
                    locationInfo.setLongitude("" + locationdata.getLongitude());
                }
            }

            // 获取Cell ID 和 lac
            TelephonyManager mTelephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            // 获取手机类型
            if (mTelephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                // 如果是CDMA制式手机
                if (!isNetworkLocated) {
                    setPhoneType(mTelephonyManager, locationInfo, TelephonyManager.PHONE_TYPE_CDMA);
                }
            } else {
                setPhoneType(mTelephonyManager, locationInfo, TelephonyManager.PHONE_TYPE_GSM);
                // GMS和其他制式手机
            }
        } catch (Exception e) {
            Log.e("e", "e", e.fillInStackTrace());
        }
    }

    private static void setPhoneType(TelephonyManager mTelephonyManager, LocationInfo locationInfo,
                                     int type) {
        String mcc = "";
        String mnc = "";
        String cellId = "";
        String lac = "";
        if (type == TelephonyManager.PHONE_TYPE_CDMA) {
            try {
                CdmaCellLocation cdma = (CdmaCellLocation) mTelephonyManager.getCellLocation();
                // 如果网络已经采集到位置，则cdma模式不再进行采集
                if (cdma != null && CommonUtils.isBlank(locationInfo.getLatitude())
                        && CommonUtils.isBlank(locationInfo.getLongitude())) {
                    lac = String.valueOf(cdma.getNetworkId());
                    String networkOperator = mTelephonyManager.getNetworkOperator();
                    if (networkOperator != null && !networkOperator.equals("")) {
                        mcc = networkOperator.substring(0, 3);
                    }
                    mnc = String.valueOf(cdma.getSystemId()); // 替代mnc，实际值不为mnc
                    cellId = String.valueOf(cdma.getBaseStationId());
                    locationInfo.setLatitude("" + cdma.getBaseStationLatitude());
                    locationInfo.setLongitude("" + cdma.getBaseStationLongitude());
                }
            } catch (Exception e) {
                Log.e("e", "e", e.fillInStackTrace());
            }
        } else {
            try {
                GsmCellLocation mGsmCellLocation = (GsmCellLocation) mTelephonyManager
                        .getCellLocation();
                if (mGsmCellLocation != null) {
                    String networkOperator = mTelephonyManager.getNetworkOperator();
                    if (networkOperator != null && !networkOperator.equals("")) {
                        mcc = mTelephonyManager.getNetworkOperator().substring(0, 3);
                        mnc = mTelephonyManager.getNetworkOperator().substring(3, 5);
                    }
                    cellId = String.valueOf(mGsmCellLocation.getCid());
                    lac = String.valueOf(mGsmCellLocation.getLac());
                }
            } catch (Exception e) {
                Log.e("e", "e", e.fillInStackTrace());
            }
        }
        locationInfo.setMcc(mcc);
        locationInfo.setMnc(mnc);
        locationInfo.setCellId(cellId);
        locationInfo.setLac(lac);
    }

    private static void setWifiInfos(Context context, LocationInfo locationInfo) {

        try {
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            locationInfo.setIsWifiActive("" + mWifi.isConnected());

            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wm.isWifiEnabled()) {
                WifiInfo wi = wm.getConnectionInfo();
                locationInfo.setBssid(wi.getBSSID());
                locationInfo.setSsid(Base64
                        .encodeToString(wi.getSSID().getBytes(), Base64.URL_SAFE));
                locationInfo.setWifiStrength("" + wi.getRssi());
            }
        } catch (Exception e) {
            Log.e("e", "e", e.fillInStackTrace());
        }
    }

    /**
     * Getter method for property <tt>longitude</tt>.
     *
     * @return property value of longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * Setter method for property <tt>longitude</tt>.
     *
     * @param longitude value to be assigned to property longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * Getter method for property <tt>latitude</tt>.
     *
     * @return property value of latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * Setter method for property <tt>latitude</tt>.
     *
     * @param latitude value to be assigned to property latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * Getter method for property <tt>bssid</tt>.
     *
     * @return property value of bssid
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * Setter method for property <tt>bssid</tt>.
     *
     * @param bssid value to be assigned to property bssid
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Getter method for property <tt>ssid</tt>.
     *
     * @return property value of ssid
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Setter method for property <tt>ssid</tt>.
     *
     * @param ssid value to be assigned to property ssid
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Getter method for property <tt>isWifiActive</tt>.
     *
     * @return property value of isWifiActive
     */
    public String getIsWifiActive() {
        return isWifiActive;
    }

    /**
     * Setter method for property <tt>isWifiActive</tt>.
     *
     * @param isWifiActive value to be assigned to property isWifiActive
     */
    public void setIsWifiActive(String isWifiActive) {
        this.isWifiActive = isWifiActive;
    }

    /**
     * Getter method for property <tt>wifiStrength</tt>.
     *
     * @return property value of wifiStrength
     */
    public String getWifiStrength() {
        return wifiStrength;
    }

    /**
     * Setter method for property <tt>wifiStrength</tt>.
     *
     * @param wifiStrength value to be assigned to property wifiStrength
     */
    public void setWifiStrength(String wifiStrength) {
        this.wifiStrength = wifiStrength;
    }

    /**
     * Getter method for property <tt>mcc</tt>.
     *
     * @return property value of mcc
     */
    public String getMcc() {
        return mcc;
    }

    /**
     * Setter method for property <tt>mcc</tt>.
     *
     * @param mcc value to be assigned to property mcc
     */
    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    /**
     * Getter method for property <tt>mnc</tt>.
     *
     * @return property value of mnc
     */
    public String getMnc() {
        return mnc;
    }

    /**
     * Setter method for property <tt>mnc</tt>.
     *
     * @param mnc value to be assigned to property mnc
     */
    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    /**
     * Getter method for property <tt>cellId</tt>.
     *
     * @return property value of cellId
     */
    public String getCellId() {
        return cellId;
    }

    /**
     * Setter method for property <tt>cellId</tt>.
     *
     * @param cellId value to be assigned to property cellId
     */
    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    /**
     * Getter method for property <tt>lac</tt>.
     *
     * @return property value of lac
     */
    public String getLac() {
        return lac;
    }

    /**
     * Setter method for property <tt>lac</tt>.
     *
     * @param lac value to be assigned to property lac
     */
    public void setLac(String lac) {
        this.lac = lac;
    }

}
