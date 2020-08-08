package olvb.reyalp

import android.content.Intent
import android.media.*
import android.os.Handler
import android.os.PowerManager
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import olvb.reyalp.model.Playback


class PlaybackService : LifecycleService(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnTimedMetaDataAvailableListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener {
    // MediaPlayer.OnInfoListener MediaPlayer.OnMediaTimeDiscontinuityListener
    private var mediaPlayer: MediaPlayer? = null
    private val playheadHandler = Handler()
    private var mediaPlayerIsPrepared = false

    companion object {
        const val PLAYHEAD_UPDATE_INTERVAL = 1000L
    }

    override fun onCreate() {
        super.onCreate()

        Playback.isPlaying.observe(this, Observer {
            onIsPlayingChanged(it)
        })
        Playback.trackPosition.observe(this, Observer {
            onTrackPositionChanged(it)
        })
        Playback.requestedPlayheadPosition.observe(this, Observer {
            onRequestedPlayheadPositionChanged(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Playback.isReady.value = true
//        setupNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Playback.isReady.value = false
        removePlayer()
        super.onDestroy()
    }

    override fun onPrepared(mp: MediaPlayer) {
        mediaPlayerIsPrepared = true
        Playback.duration.value = mp.duration

        if (Playback.isPlaying.value == true) {
            play()
        }
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        // TODO: actually handle error
        removePlayer()
        return false
    }

    override fun onTimedMetaDataAvailable(mp: MediaPlayer?, data: TimedMetaData?) {
        // TODO: do something
        return
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        // TODO: do something
    }

    override fun onCompletion(mp: MediaPlayer) {
        val position = Playback.trackPosition.value
        val tracks = Playback.tracks.value
        if (position == null || tracks == null) {
            Playback.isPlaying.value = false
            return
        }

        if (position >= tracks.size) {
            Playback.isPlaying.value = false
            Playback.trackPosition.value = null
            return
        }

        Playback.trackPosition.value = position + 1
    }

    private fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            play()
        } else {
            pause()
        }
    }

    private fun onTrackPositionChanged(position: Int?) {
        removePlayer()
        Playback.playheadPosition.value = 0

        if (position == null) {
            return
        }

        val tracks = Playback.tracks.value ?: return
        val path = tracks[position].path
        setupPlayer(path)
    }

    private fun onRequestedPlayheadPositionChanged(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    private fun setupPlayer(path: String) {
        mediaPlayerIsPrepared = false
        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener(this@PlaybackService)
            setOnErrorListener(this@PlaybackService)
            setOnSeekCompleteListener(this@PlaybackService)
            setOnCompletionListener(this@PlaybackService)

            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_MEDIA)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            })
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)

            setDataSource(path)
            prepareAsync()
        }
    }

    private fun removePlayer() {
        stopPlayheaderHandler()
        mediaPlayerIsPrepared = false
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun play() {
        if (!mediaPlayerIsPrepared) {
            return
        }
        mediaPlayer?.start()
        startPlayheadHandler()
    }

    private fun pause() {
        mediaPlayer?.pause()
        stopPlayheaderHandler()
    }

    private fun startPlayheadHandler() {
        playheadHandler.post(object : Runnable {
            override fun run() {
                Playback.playheadPosition.postValue(mediaPlayer?.currentPosition)
                playheadHandler.postDelayed(this, PLAYHEAD_UPDATE_INTERVAL)
            }
        })
    }

    private fun stopPlayheaderHandler() {
        playheadHandler.removeCallbacksAndMessages(null)

    }
}