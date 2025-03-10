package levilin.wifi.info.utility

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.ScanResult.WIFI_STANDARD_11AC
import android.net.wifi.ScanResult.WIFI_STANDARD_11AD
import android.net.wifi.ScanResult.WIFI_STANDARD_11AX
import android.net.wifi.ScanResult.WIFI_STANDARD_11BE
import android.net.wifi.ScanResult.WIFI_STANDARD_11N
import android.net.wifi.ScanResult.WIFI_STANDARD_LEGACY
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import levilin.wifi.info.ui.model.WifiInfoData

class WifiInfoUtility(private val context: Context) {
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("DefaultLocale")
    fun getWifiInfo(): WifiInfoData {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo

        val networkType = when {
            networkCapabilities == null -> "ネットワークなし"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi回線"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "携帯回線"
            else -> "不明"
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
                    WIFI_STANDARD_LEGACY -> "802.11a/b/g"
                    WIFI_STANDARD_11N -> "802.11n（WiFi-4・2.4/5GHz）"
                    WIFI_STANDARD_11AC -> "802.11ac（WiFi-5・5GHz）"
                    WIFI_STANDARD_11AX -> "802.11ax（WiFi-6・2.4/5/6GHz）"
                    WIFI_STANDARD_11AD -> "802.11ad（WiGig・60GHZ）"
                    WIFI_STANDARD_11BE -> "802.11be（WiFi-7・2.4/5/6GHz）"
                    else -> "不明"
                }
            } else {
                "不明"
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
            securityType = convertSecurityType(wifiInfo?.currentSecurityType),
            frequency = wifiInfo?.frequency,
            networkType = networkType
        )
    }

    fun convertSecurityType(currentSecurityType: Int?): String? {
        return when(currentSecurityType) {
            1 -> "WEP"
            2 -> "PSK"
            3 -> "EAP"
            4 -> "SAE"
            5 -> "EAP_WPA3 ENTERPRISE 192 BIT"
            6 -> "OWE"
            7 -> "WAPI_PSK"
            8 -> "WAPI_CERT"
            9 -> "EAP_WPA3 ENTERPRISE"
            10 -> "OSEN"
            11 -> "PASSPOINT_R1_R2"
            12 -> "PASSPOINT_R3"
            13 -> "DPP"
            else -> "不明"
        }
    }
}