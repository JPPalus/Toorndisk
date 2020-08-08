package olvb.reyalp.files

import android.os.AsyncTask
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class FilesViewModel : ViewModel() {
    var files = MutableLiveData<List<File>>()
        private set
    var isLoading = MutableLiveData<Boolean>()
        private set


    fun loadFiles(directory: File?) {
        isLoading.value = true
        AsyncTask.execute {
            performLoadFiles(directory)
        }
    }

    private fun performLoadFiles(directory: File?) {
        val directoryOrDefault = directory ?: getExternalStorageDirectoryOrThrow()
        val files = directoryOrDefault.listFiles { file ->
            !file.isHidden
        }.toList()

        this.files.postValue(files)
        isLoading.postValue(false)
    }

    private fun getExternalStorageDirectoryOrThrow(): File {
        val mountedStates = setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
        if (Environment.getExternalStorageState() !in mountedStates) {
            throw Exception("External storage is not mounted")
        }
        return Environment.getExternalStorageDirectory()
    }
}