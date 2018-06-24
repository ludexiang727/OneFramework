package com.trip.taxi.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject
/**
 * Created by ludexiang on 2018/6/21.
 */
data class TaxiOrderDetail(
        @field:SerializedName("orderId")
        val orderId: String,
        @field:SerializedName("planStartPlace")
        val startPlaceName: String,
        @field:SerializedName("planEndPlace")
        val endPlaceName: String,
        @field:SerializedName("planStartLat")
        val startLat: Double,
        @field:SerializedName("planStartLng")
        val startLng: Double,
        @field:SerializedName("planEndLat")
        val endLat: Double,
        @field:SerializedName("planEndLng")
        val endLng: Double,
        @field:SerializedName("cityCode")
        val cityCode: String,
        @field:SerializedName("driverInfo")
        val driver: OrderDriver,
        @field:SerializedName("status")
        val orderStatus: Int,
        @field:SerializedName("carType")
        val carType: Int,
        @field:SerializedName("vendorId")
        val vendorId: Int,
        @field:SerializedName("payType")
        val payType: Int,
        @field:SerializedName("type")
        val type: Int
): BaseObject()

data class OrderDriver(
        @field:SerializedName("driverId")
        val driverId: String,
        @field:SerializedName("name")
        val driverName: String,
        @field:SerializedName("driverIcon")
        val driverIcon: String,
        @field:SerializedName("driverReceivedCount")
        val driverReceiveOrderCount: Long? = 0,
        @field:SerializedName("driverRate")
        val driverStar: Float? = null,
        @field:SerializedName("phoneNo")
        val driverTel: String,
        @field:SerializedName("driverCar")
        val driverCar: String ? = null,
        @field:SerializedName("driverCarColor")
        val driverCarColor: String ? = null,
        @field:SerializedName("driverCompany")
        val driverCompany: String? = null
)