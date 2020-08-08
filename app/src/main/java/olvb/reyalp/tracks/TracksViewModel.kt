package olvb.reyalp.tracks

import android.os.AsyncTask
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import olvb.reyalp.Application
import olvb.reyalp.model.Album
import olvb.reyalp.model.Artist
import olvb.reyalp.model.Track

class TracksViewModel: ViewModel() {

    var tracks = MutableLiveData<List<Track>>()
        private set
    var isLoading = MutableLiveData<Boolean>()
        private set

    companion object {
        private val contentResolver = Application.instance.applicationContext.contentResolver
    }

    fun loadTracks(artist: Artist? = null, album: Album? = null) {
        isLoading.value = true
        AsyncTask.execute {
            performLoadTracks(artist, album)
        }
    }

    private fun performLoadTracks(artist: Artist? = null, album: Album? = null) {
        var selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        var selectionArgs = mutableListOf<String>()

        if (artist != null) {
            selection += " AND ${MediaStore.Audio.Media.ARTIST_ID} = ?"
            selectionArgs.add(artist.id.toString())
        }
        if (album != null) {
            selection += " AND ${MediaStore.Audio.Media.ALBUM_ID} = ?"
            selectionArgs.add(album.id.toString())
        }

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            selection,
            selectionArgs.toTypedArray(),
            "lower(${MediaStore.Audio.Media.TITLE}) ASC"
        ) ?: return

        if (!cursor.moveToFirst()) {
            cursor.close()
            return
        }

        val pathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
        val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
        val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
        val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
        val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

        var albumsArtPath = hashMapOf<Long, String?>()

        var tracks: MutableList<Track> = mutableListOf()
        do {
            val title = cursor.getString(titleColumn)
            val path = cursor.getString(pathColumn)
            val albumId = cursor.getLong(albumIdColumn)

            var artistName = cursor.getString(artistColumn)
            if (artistName ==  "<unknown>") {
                artistName = null
            }
            var albumName = cursor.getString(albumColumn)
            if (albumId == 1L || albumName ==  "<unknown>") {
                albumName = null
            }

            val artworkPath = {
                if (albumsArtPath.containsKey(albumId)) {
                    albumsArtPath[albumId]
                } else {
                    val artPath = loadAlbumArtPath(albumId)
                    albumsArtPath[albumId] = artPath
                     artPath
                }
            }()
            tracks.add(Track(path, title, artistName, albumName, artworkPath))
        } while (cursor.moveToNext())
        cursor.close()

        this.tracks.postValue(tracks)
        isLoading.postValue(false)
    }

    private fun loadAlbumArtPath(albumId: Long): String? {
        // Unknown Album
        if(albumId == 1L) {
            return null
        }

        var selection = " ${MediaStore.Audio.Albums._ID} = ?"
        var selectionArgs = listOf(albumId.toString())
        val cursor = contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            null,
            selection,
            selectionArgs.toTypedArray(),
            null
        ) ?: return null

        if (!cursor.moveToFirst()) {
            cursor.close()
            return null
        }

        val artPathColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)
        val artPath = cursor.getString(artPathColumn)
        cursor.close()

        return artPath
    }
}