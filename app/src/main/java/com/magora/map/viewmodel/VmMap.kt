package com.magora.map.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.MapStyleOptions
import com.magora.map.R
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/*
 * Created by Magora Systems. 
 * 03.09.19.
 */
class VmMap(app: Application) : AndroidViewModel(app), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val _mapStyleLiveData = MutableLiveData<MapStyleOptions>()
    val mapStyleLiveData: LiveData<MapStyleOptions>
        get() = _mapStyleLiveData

    fun onMapReady() {
        loadMapStyle()
    }

    private fun loadMapStyle() {
        launch {
            val options = withContext(Dispatchers.IO) {
                MapStyleOptions.loadRawResourceStyle(getApplication(), R.raw.style_json)
            }
            _mapStyleLiveData.value = options
        }
    }
}