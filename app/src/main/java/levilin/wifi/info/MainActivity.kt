package levilin.wifi.info

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import levilin.wifi.info.utility.WifiInfoUtility
import levilin.wifi.info.ui.screen.WifiInfoScreen
import levilin.wifi.info.ui.screen.WifiInfoViewModel
import levilin.wifi.info.ui.theme.WifiInfoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WifiInfoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    WifiInfoScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(color = MaterialTheme.colorScheme.background)
                            .statusBarsPadding()
                            .navigationBarsPadding(),
                        viewModel = WifiInfoViewModel(
                            wifiInfoUtility = WifiInfoUtility(context = LocalContext.current)
                        )
                    )
                }
            }
        }
    }
}
