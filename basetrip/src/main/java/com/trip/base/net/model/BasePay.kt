package com.trip.base.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class BasePay(
        @field:SerializedName("apply")
        val apply: Boolean,
        @field:SerializedName("tradeNo")
        val payId: String,
        @field:SerializedName("subTradeNo")
        val subTradeNo: String,
        @field:SerializedName("sync")
        val sync: Boolean,
        @field:SerializedName("sign")
        val sign: Sign
) : BaseObject()

data class Sign(
        @field:SerializedName("tradetype")
        val type: String,
        @field:SerializedName("mchid")
        val mchid: String,
        @field:SerializedName("package")
        val packageName: String,
        @field:SerializedName("appid")
        val appId: String,
        @field:SerializedName("sign")
        val sign: String,
        @field:SerializedName("partnerId")
        val partnerId: String,
        @field:SerializedName("prepayid")
        val prePayId: String,
        @field:SerializedName("deviceinfo")
        val deviceInfo: String,
        @field:SerializedName("mwebUrl")
        val payUrl: String? = null,
        @field:SerializedName("noncestr")
        val noncestr: String,
        @field:SerializedName("timestamp")
        val timeStamp: Long
)