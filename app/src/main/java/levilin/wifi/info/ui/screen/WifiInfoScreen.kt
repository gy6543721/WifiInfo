package levilin.wifi.info.ui.screen

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import levilin.wifi.info.ui.screen.components.RequestPermissions

@Composable
fun WifiInfoScreen(
    modifier: Modifier = Modifier,
    viewModel: WifiInfoViewModel
) {
    val wifiInfo by viewModel.wifiInfo.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        RequestPermissions(
            context = LocalContext.current,
            permission = Manifest.permission.ACCESS_FINE_LOCATION
        )

        Column(
            horizontalAlignment = Alignment.Start
        ) {
            wifiInfo?.let { info ->
                if (info.networkType == "WiFi回線") {
                    Text(
                        text = "Wi-Fiに接続している",
                        color = Color.Red,
                    )

                    Text(text = "SSID: ${info.ssid ?: "未知"}")
                    Text(text = "BSSID: ${info.bssid ?: "未知"}")
                    Text(text = "端末IP: ${info.ipAddress ?: "未知"}")
                    Text(text = "ルーターIP: ${info.ipRouter ?: "未知"}")
                    Text(text = "DHCP: ${info.dhcp ?: "未知"}")
                    Text(text = "DNS: ${info.dns ?: "未知"}")
                    Text(text = "セキュリティタイプ: ${info.securityType ?: "未知"}")
                    Text(text = "電波強度（RSSI）: ${info.rssi ?: "未知"}dB")
                    Text(text = "帯域: ${info.frequency ?: "未知"}MHz")
                    Text(text = "転送レート: ${info.linkSpeed ?: "未知"}Mbps")
                    Text(text = "PHYモード: ${info.phyMode ?: "未知"}")
                    Text(text = "チャンネル: ${info.channel ?: "未知"}")
                    Text(text = "NSS: ${info.nss ?: "未知"}")
                } else {
                    Text(
                        text = "Wi-Fiに接続していない",
                        color = Color.Red,
                    )
                    Text(text = "ネットワークタイプ: ${info.networkType}")
                }
            } ?: Text(text = "ネットワーク情報なし")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.fetchWifiInfo() }) {
            Text(text = "Wi-Fi情報アプデ")
        }
    }
}
