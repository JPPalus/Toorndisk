package olvb.reyalp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Artist(
    val id: Long,
    val name: String
) : Parcelable