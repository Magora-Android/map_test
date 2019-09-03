package com.magora.map.viewmodel

import android.app.Application
import android.location.Location
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.MapStyleOptions
import com.magora.map.R
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/*
 * Created by Magora Systems. 
 * 03.09.19.
 */
class VmMap(app: Application) : AndroidViewModel(app), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val locationServices: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(getApplication<Application>().applicationContext)

    private val _mapStyleLiveData = MutableLiveData<MapStyleOptions>()
    val mapStyleLiveData: LiveData<MapStyleOptions>
        get() = _mapStyleLiveData

    private val _myLocationLiveData = MutableLiveData<LocationEvents>()
    val myLocationLiveData: LiveData<LocationEvents>
        get() = _myLocationLiveData

    fun onMapReady() {
        loadMapStyle()
        loadCurrentLocation()
    }

    private fun loadMapStyle() {
        launch {
            val options = withContext(Dispatchers.IO) {
                MapStyleOptions.loadRawResourceStyle(getApplication(), R.raw.style_json)
            }
            _mapStyleLiveData.value = options
        }
    }

    private fun loadCurrentLocation() {
        _myLocationLiveData.value = LocationEvents.Loading
        launch(CoroutineExceptionHandler { _, error -> onCurrentLocationLoadError(error) }) {
            val location = withContext(Dispatchers.IO) {
                getLocation()
            }
            location?.let(::onCurrentLocationLoaded) ?: onCurrentLocationLoadError(null)
        }
    }

    private fun onCurrentLocationLoaded(location: Location) {
        _myLocationLiveData.value = LocationEvents.Loaded(location)
    }

    private fun onCurrentLocationLoadError(error: Throwable?) {
        _myLocationLiveData.value = LocationEvents.Error(getString(R.string.error_cant_get_location))
        error?.printStackTrace()
    }

    fun onCurrentLocationButtonClicked() {
        loadCurrentLocation()
    }

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancelChildren()
    }

    private fun getString(@StringRes id: Int) = getApplication<Application>().resources.getString(id)

    private suspend fun getLocation() = suspendCancellableCoroutine<Location?> { cancellation ->
        locationServices.lastLocation.addOnSuccessListener { location ->
            cancellation.resume(location)
        }.addOnFailureListener { error ->
            cancellation.resumeWithException(error)
        }
    }
}