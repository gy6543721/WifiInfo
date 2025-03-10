package levilin.wifi.info.utility

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.DhcpInfo
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
import levilin.wifi.info.ui.model.WifiInfoData
import java.net.InetAddress

class WifiInfoUtility(private val context: Context) {
    @SuppressLint("DefaultLocale")
    fun getWifiInfo(): WifiInfoData {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val currentNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(currentNetwork)
        val linkProperties = connectivityManager.getLinkProperties(currentNetwork)

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
            ipRouter = wifiManager.dhcpInfo.dns1.let { ip ->
                String.format(
                    "%d.%d.%d.%d",
                    ip and 0xff,
                    ip shr 8 and 0xff,
                    ip shr 16 and 0xff,
                    ip shr 24 and 0xff
                )
            },
            bssid = wifiInfo?.bssid,
            ssid = wifiInfo?.ssid?.removeSurrounding("\""),
            rssi = wifiInfo?.rssi,
            linkSpeed = wifiInfo?.linkSpeed,
            phyMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                when (wifiInfo?.wifiStandard) {
                    WIFI_STANDARD_LEGACY -> "802.11a/b/g（Wi-Fi 1-3 ・ 2.4/5GHz）"
                    WIFI_STANDARD_11N -> "802.11n（Wi-Fi 4 ・ 2.4/5GHz）"
                    WIFI_STANDARD_11AC -> "802.11ac（Wi-Fi 5 ・ 5GHz）"
                    WIFI_STANDARD_11AX -> "802.11ax（Wi-Fi 6 ・ 2.4/5/6GHz）"
                    WIFI_STANDARD_11AD -> "802.11ad（Wi-Gig ・ 60GHZ）"
                    WIFI_STANDARD_11BE -> "802.11be（Wi-Fi 7 ・ 2.4/5/6GHz）"
                    else -> "不明"
                }
            } else {
                "不明"
            },
            channel = wifiInfo?.frequency?.let { freq ->
                when (freq) {
                    in 2412..2484 -> (freq - 2412) / 5 + 1
                    in 5170..5825 -> (freq - 5170) / 5 + 36
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
            securityType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                convertSecurityType(wifiInfo?.currentSecurityType)
            } else {
                "不明"
            },
            frequency = wifiInfo?.frequency,
            dhcp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                linkProperties?.dhcpServerAddress?.hostAddress.toString()
            } else {
                wifiManager.dhcpInfo.ipAddress.toString()
            },
            dns = linkProperties?.dnsServers?.last().toString().replace(oldValue = "/", newValue = "", ignoreCase = false),
            gateway = wifiManager.dhcpInfo.gateway.let { ip ->
                String.format(
                    "%d.%d.%d.%d",
                    ip and 0xff,
                    ip shr 8 and 0xff,
                    ip shr 16 and 0xff,
                    ip shr 24 and 0xff
                )
            },
            networkType = networkType
        )
    }

    fun convertSecurityType(currentSecurityType: Int?): String {
        return when(currentSecurityType) {
            1 -> "WEP"
            2 -> "WPA/WPA2-PSK"
            3 -> "WPA/WPA2-EAP"
            4 -> "WPA3-SAE(WPA3-personal)"
            5 -> "WPA3 Enterprise 192-bit mode(CNSA)"
            6 -> "WPA3-OWE"
            7 -> "WAPI-PSK"
            8 -> "WAPI-CERT"
            9 -> "WPA3-EAP Enterprise"
            10 -> "WPA2-OSEN"
            11 -> "Passpoint Release 1/2"
            12 -> "Passpoint Release 3"
            13 -> "Wi-Fi Easy Connect(DPP)"
            else -> "不明"
        }
    }

    // サブネット用
//    fun getSubNet(wifiManager: WifiManager): String {
//        val dhcp: DhcpInfo = wifiManager.dhcpInfo
//        val broadcast = (dhcp.ipAddress and dhcp.netmask) or dhcp.netmask.inv()
//        val quads = ByteArray(4)
//        for (k in 0..3) {
//            quads[k] = (broadcast shr (k * 8) and 0xFF).toByte()
//        }
//        return InetAddress.getByAddress(quads).toString().replace(oldValue = "/", newValue = "", ignoreCase = false)
//    }
}