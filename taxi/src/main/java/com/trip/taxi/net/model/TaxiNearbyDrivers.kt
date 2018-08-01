package com.trip.taxi.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class TaxiNearbyDrivers(
        @field:SerializedName("driverList")
        var nearbyDrivers: List<NearbyDriver>
) : BaseObject()

data class NearbyDriver(
        @field:SerializedName("longitude")
        val longitude: Double,
        @field:SerializedName("latitude")
        val latitude: Double,
        @field:SerializedName("bearing")
        val bearing: Float,
        @field:SerializedName("distance")
        val distance: Long
)