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
        val driver: OrderDriver? = null,
        @field:SerializedName("taxiInfo")
        val taxiInfo: TaxiInfo? = null,
        @field:SerializedName("feeInfo")
        val feeInfo: FeeInfo,
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
        val driverStar: Float = 4f,
        @field:SerializedName("phoneNo")
        val driverTel: String,
        @field:SerializedName("driverCar")
        val driverCar: String ? = null,
        @field:SerializedName("driverCarColor")
        val driverCarColor: String ? = null,
        @field:SerializedName("company")
        val driverCompany: String? = null,
        @field:SerializedName("licenseNo")
        val driverCarNo: String? = null
)

data class FeeInfo(
        @field:SerializedName("actualTime")
        val actualTime: Long,
        @field:SerializedName("actualDistance")
        val actualDistance: Long,
        @field:SerializedName("totalMoney")
        val totalMoney: Int,// 分
        @field:SerializedName("actualPayMoney")
        val actualPayMoney: Int,
        @field:SerializedName("unPayMoney")
        val unPayMoney: Int,
        @field:SerializedName("discountMoney")
        val discountMoney: Int,// 券
        @field:SerializedName("refundMoney")
        val refundMoney: Int
)

data class TaxiInfo(
        @field:SerializedName("payForPickUp")
        val pay4PickUp : Int,
        @field:SerializedName("riderTags")
        val taxiMarks: List<String>,
        @field:SerializedName("dispatchFee")
        val taxiTip : Int,
        @field:SerializedName("feedback")
        val taxiFeedBack: Int,
        @field:SerializedName("driverComment")
        val taxiEvaluate: TaxiEvaluate? = null
)

data class TaxiEvaluate(
        @field:SerializedName("userId")
        val userId : String,
        @field:SerializedName("bizType")
        val bizType : Int,
        @field:SerializedName("driverId")
        val driverId : String,
        @field:SerializedName("orderId")
        val orderId: String,
        @field:SerializedName("content")
        val content: String? = "",
        @field:SerializedName("tags")
        val tags: String?,
        @field:SerializedName("star")
        val star : Int
)