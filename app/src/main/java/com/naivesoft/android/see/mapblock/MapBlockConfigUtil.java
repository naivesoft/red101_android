package com.naivesoft.android.see.mapblock;

import java.util.ArrayList;

/**
 * Created by admin on 2017/11/11.
 */

public class MapBlockConfigUtil {

    public static Long[] getGroupFactorList() {
        return GROUP_FACTOR_LIST;
    }
    public static Long getGroupFactorByScaleLevel(long scaleLevel) {
        for (MapBlockConfig item : MapBlockConfig.values()) {
            if (item.isMatchScale(scaleLevel)) {
                return item.getGroupFactor();
            }
        }
        return MapBlockConfig.DEFAULT.getGroupFactor();
    }

    private static Long[] GROUP_FACTOR_LIST = new Long[MapBlockConfig.values().length - 1];
    static {
        ArrayList<Long> result = new ArrayList<>();
        for (MapBlockConfig item : MapBlockConfig.values()) {
            if (item != MapBlockConfig.DEFAULT) {
                result.add(item.getGroupFactor());
            }
        }
        result.toArray(GROUP_FACTOR_LIST);
    }

    public enum MapBlockConfig {
        B100(0.01D * Math.pow(10, 10), Long.MIN_VALUE, 100L),
        B1K(1D * Math.pow(10, 10), 100L, 1000L),
        B5K(25D * Math.pow(10, 10), 1000L, 5000L),
        B10K(100D * Math.pow(10, 10), 5000L, 10000L),
        B20K(400D * Math.pow(10, 10), 10 * 1000L, 20 * 1000L),
        B50K(2500D * Math.pow(10, 10), 20 * 1000L, 50 * 1000L),
        B100K(10000D * Math.pow(10, 10), 50 * 1000L, 100 * 1000L),
        B200K(40000D * Math.pow(10, 10), 100 * 1000L, 200 * 1000L),
        B500K(250000D * Math.pow(10, 10), 200 * 1000L, 500 * 1000L),
        B1000K(1000000D * Math.pow(10, 10), 500 * 1000L, Long.MAX_VALUE),
        DEFAULT(1 * Math.pow(10, 10), -1, -1);

        private double mGroupFactor;
        private long mMinScaleLevelExclude;
        private long mMaxScaleLevelInclude;

        private MapBlockConfig(double groupFactor, long minScaleLevelExclude, long maxScaleLevelInclude) {
            mGroupFactor = groupFactor;
            mMinScaleLevelExclude = minScaleLevelExclude;
            mMaxScaleLevelInclude = maxScaleLevelInclude;
        }
        public long getGroupFactor() {
            return Math.round(mGroupFactor);
        }
        public boolean isMatchScale(long currentScale) {
            return currentScale > mMinScaleLevelExclude && currentScale <= mMaxScaleLevelInclude;
        }
    }
}
