package com.one.map.map.element;

import com.one.map.model.LatLng;
import java.util.List;

/**
 * Created by baidu on 17/1/4.
 */

public class Polyline {

    private IPolyline iPolyline;

    public Polyline(IPolyline iPolyline) {
        this.iPolyline = iPolyline;
    }

    /**
     * 设置折线颜色
     *
     * @param color 折线颜色
     */
    public void setFillColor(int color) {
        iPolyline.setColor(color);
    }

    public List<LatLng> getPoints() {
        return iPolyline.getPoints();
    }

    /**
     * 删除该覆盖物
     */
    public void remove() {
        iPolyline.remove();
    }
}
