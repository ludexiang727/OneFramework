package com.trip.base.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

/**
 * Created by ludexiang on 2018/6/21.
 */
data class BasePayList(
        @field:SerializedName("totalFee")
        val totalFee: Float,
        @field:SerializedName("feeDetailUrl")
        val feeDetail: String,
        @field:SerializedName("voucherUrl")
        val voucherUrl: String,
        @field:SerializedName("payList")
        val payList: List<PayTypeList>
) : BaseObject()

data class PayTypeList(
        @field:SerializedName("payItemIcon")
        val payItemIcon: String?,
        @field:SerializedName("payItemTitle")
        val payItemTitle: String,
        @field:SerializedName("payItemSelected")
        val payItemSelected: Boolean = false,
        @field:SerializedName("payItemIconId")
        val payItemIconRes: Int = 0,
        @field:SerializedName("payItemType")
        val payItemType : Int
)