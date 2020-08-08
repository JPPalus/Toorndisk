package olvb.reyalp.model

data class Track(
    val path: String,
    val title: String,
    val artist: String? = null,
    val album: String? = null,
    val artPath: String? = null
)