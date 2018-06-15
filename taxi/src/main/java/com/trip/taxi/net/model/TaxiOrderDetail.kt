package com.trip.taxi.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

/**
 * Created by ludexiang on 2018/6/15.
 */
data class TaxiOrderDetail(
        @field:SerializedName("orderId")
        val orderId: String,
        @field:SerializedName("start")
        val start : OrderAddress,
        @field:SerializedName("end")
        val end: OrderAddress,
        @field:SerializedName("driverInfo")
        val driver: OrderDriver,
        @field:SerializedName("status")
        val orderStatus: Int
) : BaseObject()

data class OrderAddress(
        @field:SerializedName("displayName")
        val displayName: String,
        @field:SerializedName("latitude")
        val latitude: Double,
        @field:SerializedName("longitude")
        val longitude: Double
)

data class OrderDriver(
        @field:SerializedName("driverId")
        val driverId: String,
        @field:SerializedName("driverName")
        val driverName: String,
        @field:SerializedName("driverIconUrl")
        val driverIcon: String,
        @field:SerializedName("driverReceivedCount")
        val driverReceiveOrderCount: Long,
        @field:SerializedName("driverStar")
        val driverStar : Float,
        @field:SerializedName("driverTel")
        val driverTel: String,
        @field:SerializedName("driverCar")
        val driverCar: String,
        @field:SerializedName("driverCarColor")
        val driverCarColor: String,
        @field:SerializedName("driverCompany")
        val driverCompany: String
)