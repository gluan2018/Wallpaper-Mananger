package br.com.yuki.wallpaper.manager.model

import com.google.gson.annotations.SerializedName

data class Questions(
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String
)