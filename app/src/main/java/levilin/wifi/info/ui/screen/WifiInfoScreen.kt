package levilin.wifi.info.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
        wifiInfo?.let { info ->
            if (info.networkType == "Wi-Fi") {
                Text(text = "SSID: ${info.ssid ?: "未知"}")
                Text(text = "BSSID: ${info.bssid ?: "未知"}")
                Text(text = "IPアドレス: ${info.ipAddress ?: "未知"}")
                Text(text = "MACアドレス: ${info.macAddress ?: "未知"}")
                Text(text = "RSSI: ${info.rssi ?: "未知"}")
                Text(text = "転送レート: ${info.linkSpeed ?: "未知"}(Mbps)")
                Text(text = "PHYモード: ${info.phyMode ?: "未知"}")
                Text(text = "チャンネル: ${info.channel ?: "未知"}")
                Text(text = "NSS: ${info.nss ?: "未知"}")
                Text(text = "セキュリティタイプ: ${info.securityType ?: "未知"}")
            } else {
                Text(text = "ネットワークタイプ: ${info.networkType}")
            }
        } ?: Text(text = "ネットワーク情報なし")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.fetchWifiInfo() }) {
            Text(text = "Wi-Fi情報アプデ")
        }
    }
}
