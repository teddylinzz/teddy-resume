package com.teddy.teddyresume.ui.mainmodel

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Resume(
    val information: List<Information>
) {
    data class Information(
        val label: String,
        val content: String = "",
        val icon: String = "",
        val contacts: List<ContactInfo> = emptyList()
    )
}