package com.flynid.laska.presentation.activity

import android.graphics.Color
import android.os.Bundle
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var connectionUtils: ConnectionUtils
    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        splashScreen.setOnExitAnimationListener { splashView ->
            splashView.view.animate().alpha(0f).setDuration(700).withEndAction {
                splashView.remove()
            }
        }
        splashScreen.setKeepOnScreenCondition {
            !isReady
        }

        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            try {
                viewModel.downloadActualReading()
            } catch (e: Exception) {
                if (connectionUtils.isInternetAvailable()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Калі ласка, паспрабуйце пазней",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Калі ласка, праверце інтрэрнэт",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.Companion.dark(Color.TRANSPARENT)
        )
        setContentView(R.layout.activity_main)
        initApp()

    }

    private fun initApp() {
        lifecycleScope.launch {
            if (viewModel.isReadyToPlay()) {
                isReady = true
            } else {
                delay(2000)
                isReady = true
            }

        }
    }

}