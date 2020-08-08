package olvb.reyalp.artists

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import olvb.reyalp.PlaybackFragment
import olvb.reyalp.R
import olvb.reyalp.albums.AlbumsFragment
import olvb.reyalp.tracks.TracksActivity
import olvb.reyalp.tracks.TracksFragment
import olvb.reyalp.model.Artist
import olvb.reyalp.model.Playback
import kotlinx.android.synthetic.main.artist_activity.*

class ArtistActivity : AppCompatActivity(), PlaybackFragment.OnBottomSheetBevahiorListener {

    private lateinit var artist: Artist
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    companion object {
        const val ARTIST_KEY = "artist"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // retrieve artist from intent
        artist = intent.getParcelableExtra(TracksActivity.ARTIST_KEY)

        setContentView(R.layout.artist_activity)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = artist.name

        // setup view pager handling tabs
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> TracksFragment()
                    1 -> AlbumsFragment()
                    else -> throw Exception("Invalid position")
                }
            }
            override fun getCount(): Int = 2
        }
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))

        // setup bottom sheet handling player fragment
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
            is TracksFragment -> fragment.artist = artist
            is AlbumsFragment -> fragment.artist = artist
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
