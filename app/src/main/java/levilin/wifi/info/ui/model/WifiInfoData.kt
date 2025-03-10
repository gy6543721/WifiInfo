package levilin.wifi.info.ui.model

data class WifiInfoData(
    val ipAddress: String?,
    val ipRouter: String,
    val bssid: String?,
    val ssid: String?,
    val rssi: Int?,
    val linkSpeed: Int?,
    val phyMode: String?,
    val channel: Int?,
    val nss: Int?,
    val securityType: String?,
    val frequency: Int?,
    val dhcp: String?,
    val dns: String?,
    val gateway: String?,
    val networkType: String?
)
