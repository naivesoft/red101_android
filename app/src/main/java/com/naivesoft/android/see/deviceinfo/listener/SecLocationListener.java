/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.naivesoft.android.see.deviceinfo.listener;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class SecLocationListener implements LocationListener {

    /**
     * @see LocationListener#onLocationChanged(Location)
     */
    @Override
    public void onLocationChanged(Location location) {
        // currently not useful
    }

    /**
     * @see LocationListener#onStatusChanged(String, int, Bundle)
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // currently not useful
    }

    /**
     * @see LocationListener#onProviderEnabled(String)
     */
    @Override
    public void onProviderEnabled(String provider) {
        // currently not useful
    }

    /**
     * @see LocationListener#onProviderDisabled(String)
     */
    @Override
    public void onProviderDisabled(String provider) {
        // currently not useful
    }
}
