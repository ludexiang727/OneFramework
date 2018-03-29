package com.one.map.util;

import com.one.map.model.Address;
import com.one.map.model.LatLng;
import com.one.map.model.MapStatusOperation;

public class MapUtils {

    /**
     * 计算地图缩放中心
     *
     * @param mapHeight
     * @param mapWidth
     * @param topCoverHeight
     * @param bottomCoverHeight
     * @return index0:scaleCenterX, index1:scaleCenterY
     */
    public static int[] calculateMapScaleCenter(int mapHeight, int mapWidth, int topCoverHeight, int bottomCoverHeight) {
        int scaleCenterX = mapWidth / 2;
        int scaleCenterY = (topCoverHeight + mapHeight - bottomCoverHeight) / 2;
        int[] center = new int[2];
        center[0] = scaleCenterX;
        center[1] = scaleCenterY;
        return center;
    }

    /**
     * 格式化时间 00:00:00
     *
     * @param second 秒
     * @return 格式化后的时间
     */
    public static String formatTime(long second) {
        StringBuilder stringBuilder = new StringBuilder();
        if (second >= 3600) {
            long hour = second / 3600;
            if (hour <= 9) {
                stringBuilder.append("0");
            }
            stringBuilder.append(second / 3600);
            stringBuilder.append(":");
            second = second % 3600;
        }
        long min = second / 60;
        if (min <= 9) {
            stringBuilder.append("0");
        }
        stringBuilder.append(min);
        stringBuilder.append(":");
        long secondTime = second % 60;
        if (secondTime <= 9) {
            stringBuilder.append("0");
        }
        stringBuilder.append(secondTime);
        return stringBuilder.toString();
    }


    /**
     * 判断地址是否有效
     */
    public static boolean validAddress(Address address) {
        return address != null && address.mAdrLatLng.latitude > 0
            && address.mAdrLatLng.longitude > 0;
    }

    /**
     * 判断Padding是否有效
     *
     * @param padding
     * @return
     */
    public static boolean checkPaddingValid(MapStatusOperation.Padding padding) {
        if (padding == null)
            return false;
        //过滤0,0,0,0防止抖动（之后是否真有0,0,0,0是正常的情况?）
        return !(padding.top == 0 && padding.bottom == 0 && padding.left == 0
            && padding.right == 0);
    }

    /**
     * @param params
     * @return 最大值
     */
    public static double max(double... params) {
        double result = params[0];
        for (double p : params) {
            if (p > result) {
                result = p;
            }
        }
        return result;
    }

    /**
     * @param params
     * @return 最小值
     */
    public static double min(double... params) {
        double result = params[0];
        for (double p : params) {
            if (p < result) {
                result = p;
            }
        }
        return result;
    }

    /**
     * @param latLng
     * @param center
     * @return 镜像LatLng
     */
    public static final LatLng getSymmetry(LatLng latLng, LatLng center) {
        return new LatLng(2.0D * center.latitude - latLng.latitude, 2.0D * center.longitude - latLng.longitude);
    }
    
    
}
