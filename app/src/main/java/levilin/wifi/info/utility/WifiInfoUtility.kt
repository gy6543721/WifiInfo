package levilin.wifi.info.utility

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.ScanResult.WIFI_STANDARD_11AC
import android.net.wifi.ScanResult.WIFI_STANDARD_11AX
import android.net.wifi.ScanResult.WIFI_STANDARD_11N
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.core.content.ContextCompat.checkSelfPermission
import levilin.wifi.info.ui.model.WifiInfoData

class WifiInfoUtility(private val context: Context) {
    @SuppressLint("DefaultLocale")
    fun getWifiInfo(): WifiInfoData {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo

        val networkType = when {
            networkCapabilities == null -> "No Network"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            else -> "Unknown"
        }

        return WifiInfoData(
            ipAddress = wifiInfo?.ipAddress?.let { ip ->
                String.format(
                    "%d.%d.%d.%d",
                    ip and 0xff,
                    ip shr 8 and 0xff,
                    ip shr 16 and 0xff,
                    ip shr 24 and 0xff
                )
            },
            macAddress = wifiInfo?.macAddress,
            bssid = wifiInfo?.bssid,
            ssid = wifiInfo?.ssid?.removeSurrounding("\""),
            rssi = wifiInfo?.rssi,
            linkSpeed = wifiInfo?.linkSpeed,
            phyMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                when (wifiInfo?.wifiStandard) {
                    WIFI_STANDARD_11AC -> "802.11ac"
                    WIFI_STANDARD_11AX -> "802.11ax"
                    WIFI_STANDARD_11N -> "802.11n"
                    else -> "Unknown"
                }
            } else {
                "Unknown"
            },
            channel = wifiInfo?.frequency?.let { freq ->
                when {
                    freq in 2412..2484 -> (freq - 2412) / 5 + 1
                    freq in 5170..5825 -> (freq - 5170) / 5 + 36
                    else -> null
                }
            },
            nss = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                wifiInfo?.maxSupportedTxLinkSpeedMbps?.let { speed ->
                    when {
                        speed >= 2400 -> 4
                        speed >= 1200 -> 3
                        speed >= 600 -> 2
                        else -> 1
                    }
                }
            } else {
                null
            },
            securityType = if (checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                wifiManager.configuredNetworks?.firstOrNull { it.networkId == wifiInfo?.networkId }
                    ?.let { config ->
                        when {
                            config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK) -> "WPA"
                            config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) -> "WPA-EAP"
                            config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X) -> "802.1X"
                            else -> "Open"
                        }
                    }
            } else {
                "No Permission"
            },
            networkType = networkType
        )
    }
}