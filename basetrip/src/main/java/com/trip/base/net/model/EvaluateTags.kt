package com.trip.base.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class EvaluateTags(
        @field:SerializedName("tip")
        val tipInfo: String,
        @field:SerializedName("star1")
        val evaluateOne: List<String>,
        @field:SerializedName("star2")
        val evaluateTwo: List<String>,
        @field:SerializedName("star3")
        val evaluateThree: List<String>,
        @field:SerializedName("star4")
        val evaluateFour: List<String>,
        @field:SerializedName("star5")
        val evaluateFive: List<String>
) : BaseObject()