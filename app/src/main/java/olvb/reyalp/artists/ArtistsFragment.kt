package olvb.reyalp.artists

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import olvb.reyalp.R
import olvb.reyalp.model.Artist
import kotlinx.android.synthetic.main.artists_fragment.*


class ArtistsFragment : Fragment(), ArtistsAdapter.OnInteractionListener {

    private lateinit var viewModel: ArtistsViewModel
    private lateinit var adapter: ArtistsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = ArtistsAdapter()
        adapter.listener = this

        // observe view model
        viewModel = ViewModelProviders.of(this).get(ArtistsViewModel::class.java)
        viewModel.artists.observe(this, Observer { artists ->
            onArtistsChanged(artists)
        })
        viewModel.isLoading.observe(this, Observer { isLoading ->
            onIsLoadingChanged(isLoading)
        })
        viewModel.loadArtists()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.artists_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = adapter
        swipeRefresh.setOnRefreshListener {
            onSwipeRefresh()
        }
    }

    override fun onItemSelected(item: Artist, position: Int) {
        // push new activity for selected artist
        val intent = Intent(context, ArtistActivity::class.java)
        intent.putExtra(ArtistActivity.ARTIST_KEY, item)
        startActivity(intent)
    }

    private fun onArtistsChanged(artists: List<Artist>) {
        adapter.submitList(artists)
    }

    private fun onIsLoadingChanged(isLoading: Boolean) {
        if (!isLoading) {
            swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun onSwipeRefresh() {
        viewModel.loadArtists()
    }
}