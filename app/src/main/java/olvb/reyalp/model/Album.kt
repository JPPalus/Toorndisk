package olvb.reyalp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Album(
    val id: Long,
    val title: String,
    val artist: String
) : Parcelable