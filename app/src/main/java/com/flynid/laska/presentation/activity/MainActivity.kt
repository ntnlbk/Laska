package com.flynid.laska.presentation.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.flynid.laska.R
import com.flynid.laska.presentation.uils.ConnectionUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var connectionUtils: ConnectionUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.Companion.dark(Color.TRANSPARENT)
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
            splashOverlay.animate()
                .alpha(0f)
                .setDuration(500)
                .withEndAction {
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