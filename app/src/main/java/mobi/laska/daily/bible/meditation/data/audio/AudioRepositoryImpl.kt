package mobi.laska.daily.bible.meditation.data.audio

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import mobi.laska.daily.bible.meditation.domain.audio.AudioDownloadRepository
import mobi.laska.daily.bible.meditation.domain.audio.AudioDownloadState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRepositoryImpl @OptIn(UnstableApi::class)
@Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadManager: DownloadManager
): AudioDownloadRepository {

    @OptIn(UnstableApi::class)
    override fun downloadAudio(
        url: String
    ) {
        val uri = url.toUri()

        val request = DownloadRequest.Builder(
            url,
            uri
        )
            .setMimeType(MimeTypes.AUDIO_MPEG)
            .build()

        DownloadService.sendAddDownload(
            context,
            LaskaDownloadService::class.java,
            request,
            false
        )
    }

    @OptIn(UnstableApi::class)
    override fun removeDownloadAudio(url: String) {
        DownloadService.sendRemoveDownload(
            context,
            LaskaDownloadService::class.java,
            url,
            false
        )
    }

    @OptIn(UnstableApi::class)
    override fun observeDownload(url: String): Flow<AudioDownloadState> = callbackFlow {
        val initialDownload = downloadManager.downloadIndex.getDownload(url)
        trySend(mapToDomainStatus(initialDownload))

        val listener = object : DownloadManager.Listener {
            override fun onDownloadChanged(
                downloadManager: DownloadManager,
                download: Download,
                finalException: Exception?
            ) {
                if (download.request.id == url) {
                    trySend(mapToDomainStatus(download))
                }
            }
        }

        downloadManager.addListener(listener)
        awaitClose { downloadManager.removeListener(listener) }
    }

    @OptIn(UnstableApi::class)
    private fun mapToDomainStatus(download: Download?): AudioDownloadState {
        if (download == null) return AudioDownloadState.Idle

        return when (download.state) {
            Download.STATE_QUEUED, Download.STATE_DOWNLOADING ->
                AudioDownloadState.Downloading(download.percentDownloaded)

            Download.STATE_COMPLETED ->
                AudioDownloadState.Completed

            Download.STATE_FAILED ->
                AudioDownloadState.Failed

            else -> AudioDownloadState.Idle
        }
    }

    @OptIn(UnstableApi::class)
    override suspend fun isAudioDownloaded(url: String): Boolean {
        while (!downloadManager.isInitialized) {
            delay(100) // Small delay to prevent blocking
        }

        val download = downloadManager.downloadIndex.getDownload(url)
        return download != null && download.state == Download.STATE_COMPLETED
    }

    @OptIn(UnstableApi::class)
    override fun deleteAllCachedAudio() {
        DownloadService.sendRemoveAllDownloads(
            context,
            LaskaDownloadService::class.java,
            false
        )
    }
}