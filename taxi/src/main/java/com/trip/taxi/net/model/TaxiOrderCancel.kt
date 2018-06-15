package com.trip.taxi.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

/**
 * Created by ludexiang on 2018/6/14.
 */
data class TaxiOrderCancel(
        @field:SerializedName("orderId")
        val orderId: String,
        @field:SerializedName("cancelFee")
        val cancelFee: Int = 0
): BaseObject()