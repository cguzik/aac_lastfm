package cg.lastfm.data

import com.google.gson.annotations.SerializedName

data class ImageURL(
        @SerializedName("#text")
        val url: String?,
        val size: Size?
) {

    enum class Size {
        @SerializedName("small")
        SMALL,
        @SerializedName("medium")
        MEDIUM,
        @SerializedName("large")
        LARGE,
        @SerializedName("extralarge")
        EXTRALARGE,
        @SerializedName("mega")
        MEGA
    }
}
