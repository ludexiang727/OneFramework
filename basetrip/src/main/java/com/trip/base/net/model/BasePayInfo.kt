package com.trip.base.net.model

import com.google.gson.annotations.SerializedName
import com.one.framework.net.base.BaseObject

data class  BasePayInfo(
        @field:SerializedName("payId")
        val payId: String
): BaseObject()