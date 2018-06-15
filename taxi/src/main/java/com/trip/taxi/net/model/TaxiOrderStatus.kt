package com.trip.taxi.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject
import com.trip.base.status.Status

/**
 * Created by ludexiang on 2018/6/15.
 */
data class TaxiOrderStatus(
        @field:SerializedName("orderId")
        val orderId: String,
        @field:SerializedName("status")
        val status: Int
) : BaseObject() {
}