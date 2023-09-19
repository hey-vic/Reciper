package com.myprojects.reciper.ui.shared

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.myprojects.reciper.data.RecipeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class CachedImagesCleaningWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val repository: RecipeRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.e("CachedImagesCleaningWorker", "Worker started")
        val usedUris = repository.getAllImageUris()
        withContext(Dispatchers.IO) {
            val allFiles = context.filesDir.listFiles()
            val unusedUris = allFiles
                ?.map { it.toUri() }
                ?.filter { !usedUris.contains(it.toString()) }
            unusedUris?.let { uris ->
                for (uri in uris) {
                    deletePhotoFromInternalStorage(uri)
                }
            }
        }
        return Result.success()
    }

    private fun deletePhotoFromInternalStorage(uri: Uri): Boolean {
        return try {
            Log.e("CachedImagesCleaningWorker", "Deleting $uri")
            uri.toFile().delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}