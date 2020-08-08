package olvb.reyalp.tracks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import olvb.reyalp.PlaybackService
import olvb.reyalp.R
import olvb.reyalp.model.Album
import olvb.reyalp.model.Artist
import olvb.reyalp.model.Playback
import olvb.reyalp.model.Track
import kotlinx.android.synthetic.main.tracks_fragment.*

class TracksFragment : Fragment(), TracksAdapter.OnInteractionListener {

    var artist: Artist? = null
    var album: Album? = null
    private lateinit var viewModel: TracksViewModel
    private lateinit var adapter: TracksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = TracksAdapter()
        adapter.listener = this

        // observe view model
        viewModel = ViewModelProviders.of(this).get(TracksViewModel::class.java)
        viewModel.tracks.observe(this, Observer { tracks ->
            onTracksChanged(tracks)
        })
        viewModel.isLoading.observe(this, Observer { isLoading ->
            onIsLoadingChanged(isLoading)
        })
        viewModel.loadTracks(artist, album)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tracks_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = adapter
        swipeRefresh.setOnRefreshListener {
            onSwipeRefresh()
        }
    }

    override fun onItemSelected(item: Track, position: Int) {
        // start service
        val intent = Intent(context, PlaybackService::class.java)
        context?.startService(intent)
        // load tracks and current track and start playback
        val tracks = viewModel.tracks.value ?: return
        Playback.load(tracks, position)
        Playback.play()
    }

    private fun onTracksChanged(tracks: List<Track>) {
        adapter.submitList(tracks)
    }

    private fun onIsLoadingChanged(isLoading: Boolean) {
        if (!isLoading) {
            swipeRefresh.isRefreshing = false
        }
    }

    private fun onSwipeRefresh() {
        viewModel.loadTracks(artist, album)
    }
}