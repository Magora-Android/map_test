package com.magora.map.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng

/*
 * Created by Magora Systems. 
 * 03.09.19.
 */
fun Location.randomNearby(): LatLng {
    fun sign() = if(Math.random() > 0.5) 1 else -1

    val lat = latitude + sign() * Math.random()
    val lon = longitude + sign() * Math.random()

    return LatLng(lat, lon)
}