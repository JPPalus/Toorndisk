package olvb.reyalp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

object Playback {
    val isReady = MutableLiveData<Boolean>()
    val isPlaying = MutableLiveData<Boolean>()
    val tracks = MutableLiveData<List<Track>>()
    val trackPosition = MutableLiveData<Int?>()
    val currentTrack: LiveData<Track?> = Transformations.map(trackPosition) { position ->
        if (position == null) null else tracks.value?.get(position)
    }
    val playheadPosition = MutableLiveData<Int>()
    var duration = MutableLiveData<Int>()
    val requestedPlayheadPosition = MutableLiveData<Int>()

    fun play() {
        isPlaying.value = true
    }

    fun pause() {
        isPlaying.value = false
    }

    fun skipToPrevious() {
        val position = this.trackPosition.value ?: return
        if (position == 0) {
            return
        }
        this.trackPosition.value = position - 1
    }

    fun skipToNext() {
        val position = this.trackPosition.value ?: return
        val tracks = this.tracks.value ?: return
        if (position == tracks.size - 1 ) {
            return
        }
        this.trackPosition.value = position + 1
    }

    fun seekTo(position: Int) {
        this.requestedPlayheadPosition.value = position
    }

    fun load(tracks: List<Track>, position: Int) {
        this.tracks.value = tracks
        this.trackPosition.value = position
    }
}