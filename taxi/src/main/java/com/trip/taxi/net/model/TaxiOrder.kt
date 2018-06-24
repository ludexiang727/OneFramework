package com.trip.taxi.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject
import com.trip.base.model.IOrder

/**
 * Created by ludexiang on 2018/6/13.
 */
data class TaxiOrder(
        @field:SerializedName("orderId")
        val orderId: String,
        @field:SerializedName("orderCreateTime")
        val orderCreateTime: Long,
        @field:SerializedName("currentTime")
        val currentServerTime: Long,
        @field:SerializedName("waitConfigTime")
        val waitConfigTime: Int = 0
) : BaseObject(), IOrder {
    var orderInfo: TaxiOrderDetail? = null

    fun saveOrderInfo(order: TaxiOrderDetail) {
        orderInfo = order
    }
}