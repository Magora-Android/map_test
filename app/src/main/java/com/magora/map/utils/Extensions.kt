package com.magora.map.utils

import android.content.res.Resources
import android.location.Location
import android.util.TypedValue
import com.google.android.gms.maps.model.LatLng
import kotlin.math.roundToInt

/*
 * Created by Magora Systems. 
 * 03.09.19.
 */

val Int.dp
    inline get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).roundToInt()

val Float.dp
    inline get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

fun Location.randomNearby(): LatLng {
    fun sign() = if(Math.random() > 0.5) 1 else -1

    val lat = latitude + sign() * Math.random()
    val lon = longitude + sign() * Math.random()

    return LatLng(lat, lon)
}