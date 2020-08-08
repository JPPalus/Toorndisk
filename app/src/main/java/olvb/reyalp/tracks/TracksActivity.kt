package olvb.reyalp.tracks

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import olvb.reyalp.PlaybackFragment
import olvb.reyalp.R
import olvb.reyalp.model.Album
import olvb.reyalp.model.Artist
import olvb.reyalp.model.Playback
import kotlinx.android.synthetic.main.tracks_activity.*


class TracksActivity : AppCompatActivity(), PlaybackFragment.OnBottomSheetBevahiorListener {

    private var artist: Artist? = null
    private var album: Album? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    companion object {
        const val ARTIST_KEY = "artist"
        const val ALBUM_KEY = "album"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // retrieve artist and album from intent
        if (intent.hasExtra(ARTIST_KEY)) {
            artist = intent.getParcelableExtra(ARTIST_KEY)
        }
        if (intent.hasExtra(ALBUM_KEY)) {
            album = intent.getParcelableExtra(ALBUM_KEY)
        }

        setContentView(R.layout.tracks_activity)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = album?.title ?: artist?.name

        // setup bottom sheet handling playback fragment
        bottomSheetBehavior = BottomSheetBehavior.from(playbackFragment.view)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onBottomSheetStateChanged(newState)
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        playbackFragment.view?.visibility = if (Playback.isReady.value == true) View.VISIBLE else View.GONE

        // observe playback
        Playback.isReady.observe(this, Observer { isReady ->
            onPlaybackIsReadyChanged(isReady)
        })

    }

    override fun onAttachFragment(fragment: Fragment?) {
        when (fragment) {
            is TracksFragment -> {
                fragment.artist = artist
                fragment.album = album
            }
        }
        super.onAttachFragment(fragment)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_settings -> return true
        }

        return super.onOptionsItemSelected(item)
    }

    // playback fragment is requesting to be expanded (click on toolbar)
    override fun onExpand() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    // playback fragment is requesting to be collapsed (click on toolbar)
    override fun onCollapse() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun onPlaybackIsReadyChanged(isReady: Boolean) {
        if (isReady) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            playbackFragment.view?.visibility = View.VISIBLE


//            val constraintSet = ConstraintSet()
//            constraintSet.clone(constraintLayout)
//            constraintSet.connect(R.id.trackListFragment, ConstraintSet.BOTTOM, R.id.constraintLayout, ConstraintSet.BOTTOM, 150)
//            constraintSet.applyTo(constraintLayout)
        } else {
            playbackFragment.view?.visibility = View.GONE
        }
    }

    private fun onBottomSheetStateChanged(newState: Int) {
        val playbackFragment = (playbackFragment as? PlaybackFragment) ?: return
        when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> playbackFragment.onExpanded()
            BottomSheetBehavior.STATE_COLLAPSED -> playbackFragment.onCollapsed()
        }
    }
}