package mobi.laska.daily.bible.meditation.data.audio

import android.app.Notification
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.exoplayer.scheduler.Scheduler
import mobi.laska.daily.bible.meditation.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class LaskaDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    "download_channel",
    R.string.app_name,
    0
) {

    @Inject
    lateinit var exoDownloadManager: DownloadManager

    override fun getDownloadManager(): DownloadManager {
        return exoDownloadManager
    }

    override fun getScheduler(): Scheduler? {
        return PlatformScheduler(this, JOB_ID)
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        val helper = DownloadNotificationHelper(this, "download_channel")

        return helper.buildProgressNotification(
            this,
            R.drawable.ic_launcher_foreground,
            null,
            null,
            downloads,
            notMetRequirements
        )
    }

    companion object {
        const val FOREGROUND_NOTIFICATION_ID = 1
        const val JOB_ID = 1001
    }
}