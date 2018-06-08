package com.trip.taxi.net.model

import com.one.framework.net.base.BaseObject
import com.google.gson.annotations.SerializedName

/**
 * Created by ludexiang on 2018/6/8.
 */
data class TaxiEstimatePrice(
        @field:SerializedName("priceCopy")
        val estimatePrice: String,
        @field:SerializedName("couponCopy")
        val estimateCoupin: String? = null,
        @field:SerializedName("discountCopy")
        val estimateDiscount: String? = null,
        @field:SerializedName("couponFee")
        val estimateCouponFee: Int = 0,
        @field:SerializedName("discountRatio")
        val estimateDiscountRatio: Float = 0.0f,
        @field:SerializedName("narration")
        val estimateNarration: String? = null,
        @field:SerializedName("ruleCopy")
        val estimateRuleCopy: String? = null,
        @field:SerializedName("ruleUrl")
        val estimateRuleUrl: String? = null
) : BaseObject()