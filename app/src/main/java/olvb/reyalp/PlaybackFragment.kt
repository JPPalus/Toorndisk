package olvb.reyalp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import olvb.reyalp.model.Playback
import olvb.reyalp.model.Track
import kotlinx.android.synthetic.main.playback_collapsed_layout.*
import kotlinx.android.synthetic.main.playback_fragment.*

class PlaybackFragment: Fragment() {

    private var onBottomSheetBevahiorListener: OnBottomSheetBevahiorListener? = null
    private var playeadSeekBarIsBeingTouched = false

    interface OnBottomSheetBevahiorListener {
        fun onExpand()
        fun onCollapse()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onBottomSheetBevahiorListener = context as? OnBottomSheetBevahiorListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Playback.currentTrack.observe(this, Observer { track ->
            onCurrentTrackChanged(track)
        })

        Playback.isPlaying.observe(this, Observer { isPlaying ->
            onIsPlayingChanged(isPlaying)
        })

        Playback.duration.observe(this, Observer { duration ->
            onDurationChanged(duration)
        })

        Playback.playheadPosition.observe(this, Observer { playhead ->
            onPlayheadChanged(playhead)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.playback_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.visibility = View.GONE
        collapsedToolbar.visibility = View.VISIBLE

        toolbar.setNavigationOnClickListener {
            onToolbarNavigationClicked()
        }

        toolbar.setOnClickListener {
            onToolbarClicked()
        }

        collapsedToolbar.setOnClickListener {
            onCollapsedToolbarClicked()
        }

        playPauseButton.setOnClickListener {
            onPlayPauseClicked()
        }

        collapsedPlayPauseButton.setOnClickListener {
            onPlayPauseClicked()
        }

        previousButton.setOnClickListener {
            onPreviousClicked()
        }

        nextButton.setOnClickListener {
            onNextClicked()
        }

        playheadSeekBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                onPlayheadSeekBarTouchStart()
            }
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                onPlayheadSeekBarTouchEnd()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
        }

        return super.onOptionsItemSelected(item)
    }


    fun onExpanded() {
        toolbar.visibility = View.VISIBLE
        collapsedToolbar.visibility = View.GONE
    }

    fun onCollapsed() {
        toolbar.visibility = View.GONE
        collapsedToolbar.visibility = View.VISIBLE
    }

    private fun onToolbarClicked() {
        onBottomSheetBevahiorListener?.onCollapse()
    }

    private fun onCollapsedToolbarClicked() {
        onBottomSheetBevahiorListener?.onExpand()
    }

    private fun onToolbarNavigationClicked() {
        onBottomSheetBevahiorListener?.onCollapse()
    }

    private fun onCurrentTrackChanged(track: Track?) {
        val title = track?.title ?: "No track"
        titleTextView.text = title
        collapsedTitleTextView.text = title

        detailsTextView.text = track?.artist ?: track?.album ?: "Unknown"
        collapsedDetailsTextView.text = track?.artist ?: track?.album ?: "Unknown"

        val artPath = track?.artPath
        if (artPath !== null) {
            artwortkImageView.setImageURI(Uri.parse(artPath))
        } else {
            artwortkImageView.setImageURI(null)
        }
    }

    private fun onIsPlayingChanged(isPlaying: Boolean) {
        val imageResource = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        playPauseButton.setImageResource(imageResource)
        collapsedPlayPauseButton.setImageResource(imageResource)
    }

    private fun onDurationChanged(duration: Int) {
        playheadSeekBar.max = duration
        collapsedPlayheadProgressBar.max = duration
    }

    private fun onPlayheadChanged(position: Int) {
        if (!playeadSeekBarIsBeingTouched) {
            playheadSeekBar.progress = position
        }
        collapsedPlayheadProgressBar.progress = position
    }

    private fun onPlayPauseClicked() {
        if (Playback.isPlaying.value == true) {
            Playback.pause()
        } else {
            Playback.play()
        }
    }

    private fun onPreviousClicked() {
        Playback.skipToPrevious()
    }

    private fun onNextClicked() {
        Playback.skipToNext()
    }

    private fun onPlayheadSeekBarTouchStart() {
        playeadSeekBarIsBeingTouched = true
    }

    private fun onPlayheadSeekBarTouchEnd() {
        Playback.seekTo(playheadSeekBar.progress)
        playeadSeekBarIsBeingTouched = false
    }
}