package com.trip.taxi.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

/**
 * Created by ludexiang on 2018/6/20.
 */
data class TaxiOrderDriverLocation(
        @field:SerializedName("longitude")
        val longitude: Double,
        @field:SerializedName("latitude")
        val latitude: Double,
        @field:SerializedName("bizType")
        val bizType: Int,
        @field:SerializedName("bearing")
        val bearing: Float
) : BaseObject()