package com.magora.map.viewmodel

import android.location.Location

/*
 * Created by Magora Systems. 
 * 03.09.19.
 */
sealed class LocationEvents {
    object Loading: LocationEvents()
    class Loaded(val location: Location) : LocationEvents()
    class Error(val message: String) : LocationEvents()
}