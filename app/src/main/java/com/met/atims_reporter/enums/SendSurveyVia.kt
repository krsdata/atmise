package com.met.atims_reporter.enums

import com.google.gson.annotations.SerializedName

enum class SendSurveyVia {
    @SerializedName("mobile")
    MOBILE,

    @SerializedName("mobile")
    QR_CODE,

    @SerializedName("web")
    WEB
}