package olvb.reyalp.files

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
import olvb.reyalp.model.Playback
import olvb.reyalp.model.Track
import kotlinx.android.synthetic.main.files_fragment.*
import java.io.File


class FilesFragment : Fragment(), FilesAdapter.OnInteractionListener {

    var directory: File? = null
    private lateinit var viewModel: FilesViewModel
    private lateinit var adapter: FilesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = FilesAdapter()
        adapter.listener = this

        // observe view model
        viewModel = ViewModelProviders.of(this).get(FilesViewModel::class.java)
        viewModel.files.observe(this, Observer { files ->
            onFilesChanged(files)
        })
        viewModel.isLoading.observe(this, Observer { isLoading ->
            onIsLoadingChanged(isLoading)
        })
        viewModel.loadFiles(directory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.files_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = adapter
        swipeRefresh.setOnRefreshListener {
            onSwipeRefresh()
        }
    }

    override fun onItemSelected(item: File, position: Int) {
        if (item.isDirectory) {
            // push new activity for files of selected directory
            val intent = Intent(context, FilesActivity::class.java)
            intent.putExtra(FilesActivity.DIRECTORY_KEY, item)
            startActivity(intent)
        } else {
            // start service
            val intent = Intent(context, PlaybackService::class.java)
            context?.startService(intent)
            // load files as tracks and current track and start playback
            val files = viewModel.files.value ?: return
            val tracks = files.map { file -> Track(file.path, file.name) }
            Playback.load(tracks, position)
            Playback.play()
        }
    }

    private fun onFilesChanged(files: List<File>) {
        adapter.submitList(files)
    }

    private fun onIsLoadingChanged(isLoading: Boolean) {
        if (!isLoading) {
            swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun onSwipeRefresh() {
        viewModel.loadFiles(directory)
    }
}