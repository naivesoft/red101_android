package com.naivesoft.android.see.mapblock;

import java.util.ArrayList;

/**
 * Created by admin on 2017/11/11.
 */

public class MapBlockHelper {

    private MapBlockHelper() {
    }

    private static MapBlockHelper sInstance = new MapBlockHelper();

    public final static MapBlockHelper getInstance() {
        return sInstance;
    }

    public Long[] getLookBlockList(long scale, long lon, long lat) {
        long groupFactor = MapBlockConfigUtil.getGroupFactorByScaleLevel(scale);
        long encodePosition = encode(lon, lat);
        Long[] result = new Long[1];
        ArrayList<Long> resultList = new ArrayList<>();
        resultList.add(encodePosition / Math.round(groupFactor));
        resultList.toArray(result);
        return result;
    }

    public Long[] getInBlockList(long lon, long lat) {
        long encodePosition = encode(lon, lat);
        Long[] groupFactorList = MapBlockConfigUtil.getGroupFactorList();
        Long[] result = new Long[groupFactorList.length];
        ArrayList<Long> resultList = new ArrayList<>();
        for (Long groupFactor : groupFactorList) {
            resultList.add(encodePosition / Math.round(groupFactor));
        }
        resultList.toArray(result);
        return result;
    }

    private long encode(long lon, long lat) {
        long area = 0;
        //先设置经度,避免负数,直接加180
        long newLon = lon + 180000000;
        for (int i = 0; i < 64; i += 2) {
            if (theBitTrue(newLon, i)) {
                area = setTheBitTrue(area, i);
            } else {
                area = setTheBitFalse(area, i);
            }
        }
        //再设置维度
        long newLat = lat + 90000000;
        for (int i = 0; i < 64; i += 2) {
            if (theBitTrue(newLat, i)) {
                area = setTheBitTrue(area, i + 1);
            } else {
                area = setTheBitFalse(area, i + 1);
            }
        }
        return area;
    }

    /**
     * 判断num上  第point位上是否为1
     * point 从0开始.
     */
    private boolean theBitTrue(long num, int point) {
        long bits = 1l << point;
        return (bits & num) == bits;
    }

    /**
     * 将num的第point位设置成1,然后返回
     */
    private long setTheBitTrue(long num, int point) {
        return num |= (1l << point);
    }

    /**
     * 将num的第point位设置成0,然后返回
     */
    private long setTheBitFalse(long num, int point) {
        return num &= ~(1l << point);
    }

}
