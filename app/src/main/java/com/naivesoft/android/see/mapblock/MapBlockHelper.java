package com.naivesoft.android.see.mapblock;

import com.amap.api.maps.model.LatLng;

import java.util.HashSet;
import java.util.Set;

public class MapBlockHelper {

    public static Set<String> getLookBlockList(double scale, LatLng latlng) {
        return getLookBlockList(scale, Math.round(latlng.longitude * Math.pow(10, 6)), Math.round(latlng.latitude * Math.pow(10, 6)));
    }

    public static Set<String> getLookBlockList(double scale, long lon, long lat) {
        Long groupFactor = MapBlockConfigUtil.getGroupFactorByScaleLevel(scale);
        long encodePosition = encode(lon, lat);
        Set<String> resultList = new HashSet<>();
        resultList.add(Long.toHexString(encodePosition / groupFactor));
        return resultList;
    }

    public static Set<String> getInBlockList(long lon, long lat) {
        long encodePosition = encode(lon, lat);
        Long[] groupFactorList = MapBlockConfigUtil.getGroupFactorList();
        Set<String> resultList = new HashSet<>();
        for (Long groupFactor : groupFactorList) {
            resultList.add(Long.toHexString(encodePosition / groupFactor));
        }
        return resultList;
    }

    private static long encode(long lon, long lat) {
        long area = 0;
        //先设置经度,避免负数,直接加180
        long newLon = lon + 180000000;
        for (int i = 0; i < 32; i += 1) {
            if (theBitTrue(newLon, i)) {
                area = setTheBitTrue(area, 2 * i);
            } else {
                area = setTheBitFalse(area, 2 * i);
            }
        }
        //再设置维度
        long newLat = lat + 90000000;
        for (int i = 0; i < 32; i += 1) {
            if (theBitTrue(newLat, i)) {
                area = setTheBitTrue(area, 2 * i + 1);
            } else {
                area = setTheBitFalse(area, 2 * i + 1);
            }
        }
        return area;
    }

    /**
     * 判断num上  第point位上是否为1
     * point 从0开始.
     */
    private static boolean theBitTrue(long num, int point) {
        long bits = 1l << point;
        return (bits & num) == bits;
    }

    /**
     * 将num的第point位设置成1,然后返回
     */
    private static long setTheBitTrue(long num, int point) {
        return num |= (1l << point);
    }

    /**
     * 将num的第point位设置成0,然后返回
     */
    private static long setTheBitFalse(long num, int point) {
        return num &= ~(1l << point);
    }

}
