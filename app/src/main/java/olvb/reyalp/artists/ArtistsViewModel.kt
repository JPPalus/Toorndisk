package olvb.reyalp.artists

import android.os.AsyncTask
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import olvb.reyalp.Application
import olvb.reyalp.R
import olvb.reyalp.model.Artist

class ArtistsViewModel: ViewModel() {

    var artists = MutableLiveData<List<Artist>>()
        private set
    var isLoading = MutableLiveData<Boolean>()
        private set

    companion object {
        private val contentResolver = Application.instance.applicationContext.contentResolver
        private val UNKNOWN_ARTIST = Application.instance.applicationContext.getString(R.string.unknown_artist)
        private val VARIOUS_ARTISTS = Application.instance.applicationContext.getString(R.string.various_artists)
    }

    fun loadArtists() {
        isLoading.value = true
        AsyncTask.execute {
            performLoadArtists()
        }
    }

    private fun performLoadArtists() {
        val cursor = contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            "lower(${MediaStore.Audio.Media.ARTIST}) ASC"
        ) ?: return

        if (!cursor.moveToFirst()) {
            cursor.close()
            return
        }

        val idColumn = cursor.getColumnIndex(MediaStore.Audio.Artists._ID)
        val nameColumn = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)

        var artists: MutableList<Artist> = mutableListOf()
        do {
            val id = cursor.getLong(idColumn)

            var name = cursor.getString(nameColumn)
            if (name == "<unknown>") {
                name = UNKNOWN_ARTIST
            }
//            val name = when (id) {
//                1L -> UNKNOWN_ARTIST
//                else -> cursor.getString(nameColumn)
//            }

            artists.add(Artist(id, name))
        } while (cursor.moveToNext())
        cursor.close()

        this.artists.postValue(artists)
        isLoading.postValue(false)
    }
}