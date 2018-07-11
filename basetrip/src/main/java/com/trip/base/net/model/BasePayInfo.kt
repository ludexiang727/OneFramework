package com.trip.base.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class  BasePayInfo(
        @field:SerializedName("apply")
        val apply : Boolean,
        @field:SerializedName("sync")
        val sync: Boolean,
        @field:SerializedName("sign")
        val sign: String? = null,
        @field:SerializedName("tradeNo")
        val payId: String,
        @field:SerializedName("subTradeNo")
        val subTradeNo: String? = null
): BaseObject()