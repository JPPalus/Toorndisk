package olvb.reyalp.albums

import android.os.AsyncTask
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import olvb.reyalp.Application
import olvb.reyalp.model.Album
import olvb.reyalp.R
import olvb.reyalp.model.Artist

class AlbumsViewModel: ViewModel() {

    var albums = MutableLiveData<List<Album>>()
        private set
    var isLoading = MutableLiveData<Boolean>()
        private set

    companion object {
        private val contentResolver = Application.instance.applicationContext.contentResolver
        private val UNKNOWN_ARTIST = Application.instance.applicationContext.getString(R.string.unknown_artist)
        private val UNKNOWN_ALBUM = Application.instance.applicationContext.getString(R.string.unknown_album)
    }

    fun loadAlbums(artist: Artist? = null) {
        isLoading.value = true
        AsyncTask.execute {
            performLoadAlbums(artist)
        }
    }

    private fun performLoadAlbums(artist: Artist? = null) {
        var selection = ""
        var selectionArgs = mutableListOf<String>()

        if (artist != null) {
            selection += "${MediaStore.Audio.Media.ARTIST_ID} = ?"
            selectionArgs.add(artist.id.toString())
        }

        val cursor = contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            null,
            selection,
            selectionArgs.toTypedArray(),
            "lower(${MediaStore.Audio.Media.ALBUM}) ASC"
        ) ?: return

        if (!cursor.moveToFirst()) {
            cursor.close()
            return
        }

        val idColumn = cursor.getColumnIndex( MediaStore.Audio.Albums._ID)
        val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
        val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)

        var albums: MutableList<Album> = mutableListOf()
        do {
            val id = cursor.getLong(idColumn)

            val title = when (id) {
                1L -> UNKNOWN_ALBUM
                else ->cursor.getString(titleColumn)
            }

            var artistName = cursor.getString(artistColumn)
            if (artistName == "<unknown>") {
                artistName = UNKNOWN_ARTIST
            }

            albums.add(Album(id, title, artistName))
        } while (cursor.moveToNext())
        cursor.close()

        this.albums.postValue(albums)
        isLoading.postValue(false)
    }
}