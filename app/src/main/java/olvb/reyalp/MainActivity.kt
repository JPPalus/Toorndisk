package olvb.reyalp

import android.Manifest
import android.content.pm.PackageManager
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import olvb.reyalp.albums.AlbumsFragment
import olvb.reyalp.artists.ArtistsFragment
import olvb.reyalp.files.FilesFragment
import olvb.reyalp.model.Playback
import olvb.reyalp.tracks.TracksFragment
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), PermissionsFragment.OnPermissionsRequestListener, PlaybackFragment.OnBottomSheetBevahiorListener {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    companion object {
        const val PERMISSION_REQUEST_CODE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))

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

        requestExtStoragePermission()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true // TODO: do something
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

    // permissions fragment wants to request permissions again
    override fun onPermissionsRequest() {
        requestExtStoragePermission()
    }

    private fun onPlaybackIsReadyChanged(isReady: Boolean) {
        if (isReady) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            playbackFragment.view?.visibility = View.VISIBLE
        } else {
            playbackFragment.view?.visibility = View.GONE
        }
    }

    private fun requestExtStoragePermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            onExtStoragePermissionGranted()
        } else {
            requestPermissions(arrayOf(permission), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onExtStoragePermissionGranted()
                } else {
                    // ignore shouldShowRequestPermissionRationale because we need it anyway
                    onExtStoragePermissionDenied()
                }
            }
            else -> { }
        }
    }

    private fun onExtStoragePermissionGranted() {
        tabs.visibility = View.VISIBLE
        setupViewPagerAdapter()
    }

    private fun onExtStoragePermissionDenied() {
        tabs.visibility = View.GONE
        setupPermissionsViewPageAdapter()
    }

    private fun setupPermissionsViewPageAdapter() {
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return PermissionsFragment()
            }

            override fun getCount(): Int = 1
        }
    }

    private fun setupViewPagerAdapter() {
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> TracksFragment()
                    1 -> ArtistsFragment()
                    2 -> AlbumsFragment()
                    3 -> FilesFragment()
                    else -> throw Exception("Invalid position")
                }
            }

            override fun getCount(): Int = 4
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
