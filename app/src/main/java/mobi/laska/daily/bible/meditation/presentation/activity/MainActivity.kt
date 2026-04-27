package mobi.laska.daily.bible.meditation.presentation.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mobi.laska.daily.bible.meditation.R
import mobi.laska.daily.bible.meditation.presentation.uils.ConnectionUtils
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var connectionUtils: ConnectionUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val rootView = findViewById<View>(android.R.id.content)

        val isTablet = resources.getBoolean(R.bool.is_tablet)
        val hasTaskbarOS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            if (isTablet && hasTaskbarOS) {
                view.setPadding(0, insets.top, 0, insets.bottom)
            }

            windowInsets
        }


        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.Companion.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )
        setContentView(R.layout.activity_main)

        val splashOverlay = findViewById<View>(R.id.splash_overlay)
        preloadDataAndManageSplash(splashOverlay)

    }

    private fun preloadDataAndManageSplash(splashOverlay: View) {
        lifecycleScope.launch {
            val minimumSplashTimer = async { delay(1500L) }
            val dataFetchJob = async {
                try {
                    viewModel.downloadActualReading()

                    if (!viewModel.isReadyToPlay()) {
                        delay(2000L)
                    }
                } catch (e: Exception) {
                    showNetworkError()
                }
            }
            minimumSplashTimer.await()
            dataFetchJob.await()
            splashOverlay.animate().alpha(0f).setDuration(500).withEndAction {
                    splashOverlay.visibility = View.GONE
                }
        }
    }

    private fun showNetworkError() {
        val message = if (connectionUtils.isInternetAvailable()) {
            "Калі ласка, паспрабуйце пазней"
        } else {
            "Калі ласка, праверце інтрэрнэт"
        }
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}