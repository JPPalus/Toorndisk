package olvb.reyalp.albums

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import olvb.reyalp.R
import olvb.reyalp.model.Album
import olvb.reyalp.tracks.TracksActivity
import olvb.reyalp.model.Artist
import kotlinx.android.synthetic.main.albums_fragment.*


class AlbumsFragment : Fragment(), AlbumsAdapter.OnInteractionListener {

    var artist: Artist? = null
    private lateinit var viewModel: AlbumsViewModel
    private lateinit var adapter: AlbumsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = AlbumsAdapter()
        adapter.listener = this

        // observe view model
        viewModel = ViewModelProviders.of(this).get(AlbumsViewModel::class.java)
        viewModel.albums.observe(this, Observer { albums ->
            onAlbumsChanged(albums)
        })
        viewModel.isLoading.observe(this, Observer { isLoading ->
            onIsLoadingChanged(isLoading)
        })
        viewModel.loadAlbums(artist)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.albums_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = adapter
        swipeRefresh.setOnRefreshListener {
            onSwipeRefresh()
        }
    }

    override fun onItemSelected(item: Album, position: Int) {
        // push new activity for tracks of selected album
        val intent = Intent(context, TracksActivity::class.java)
        intent.putExtra(TracksActivity.ALBUM_KEY, item)
        startActivity(intent)
    }

    private fun onAlbumsChanged(albums: List<Album>) {
        adapter.submitList(albums)
    }

    private fun onIsLoadingChanged(isLoading: Boolean) {
        if (!isLoading) {
            swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun onSwipeRefresh() {
        viewModel.loadAlbums(artist)
    }
}