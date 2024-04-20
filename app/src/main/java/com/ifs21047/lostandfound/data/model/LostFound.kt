package com.ifs21047.lostfounds.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LostFound (
    val id: Int,
    val title: String,
    val description: String,
    val status: String,
    val iscompleted: Boolean,
    val cover: String?,
) : Parcelable
