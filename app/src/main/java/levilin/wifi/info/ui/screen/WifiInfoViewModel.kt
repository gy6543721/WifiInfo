package levilin.wifi.info.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import levilin.wifi.info.ui.model.WifiInfoData
import levilin.wifi.info.utility.WifiInfoUtility

class WifiInfoViewModel(private val wifiInfoUtility: WifiInfoUtility) : ViewModel() {

    private val _wifiInfo = MutableStateFlow<WifiInfoData?>(null)
    val wifiInfo: StateFlow<WifiInfoData?> = _wifiInfo

    fun fetchWifiInfo() {
        viewModelScope.launch {
            _wifiInfo.value = wifiInfoUtility.getWifiInfo()
        }
    }
}
