package levilin.wifi.info.ui.model

data class WifiInfoData(
    val ipAddress: String?,
    val macAddress: String?,
    val bssid: String?,
    val ssid: String?,
    val rssi: Int?,
    val linkSpeed: Int?,
    val phyMode: String?,
    val channel: Int?,
    val nss: Int?,
    val securityType: String?,
    val networkType: String?
)
