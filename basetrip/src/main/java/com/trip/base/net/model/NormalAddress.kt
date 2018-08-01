package com.trip.base.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class NormalAddress(
        @field:SerializedName("total")
        val total: Int,
        @field:SerializedName("pageNo")
        val pageNo: Int,
        @field:SerializedName("pageSize")
        val pageSize: Int,
        @field:SerializedName("list")
        val address: List<NormalAdr>
): BaseObject()

data class NormalAdr(
        @field:SerializedName("userPoiHisId")
        val userPoiHisId: Int,
        @field:SerializedName("userId")
        val userId : String,
        @field:SerializedName("cityCode")
        val cityCode: String,
        @field:SerializedName("poiName")
        val poiName: String,
        @field:SerializedName("addressDetail")
        val addressDetail: String? = null,
        @field:SerializedName("longitude")
        val longitude: String,
        @field:SerializedName("latitude")
        val latitude: String,
        @field:SerializedName("tag")
        val tag: Int, //  1 home 2 company
        @field:SerializedName("counts")
        val counts: Int,
        @field:SerializedName("geoHash")
        val geoHash: String? = null,
        @field:SerializedName("createTime")
        val createTime: Long,
        @field:SerializedName("updateTime")
        val updateTime: Long
)