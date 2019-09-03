package com.magora.map.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import androidx.annotation.ColorInt
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * Created by Magora Systems. 
 * 03.09.19.
 */

data class MarkerDescription(val options: MarkerOptions, val type: String, val title: String, val isAlert: Boolean)

enum class Alerts(val color: Int) {
    MINOR_DEFAULT(Color.parseColor("#19B775")),
    POLICE_ACTIVITY(Color.parseColor("#608AF5")),
    EXTREME_DEFAULT(Color.parseColor("#FF2E79")),
    MODERATE_DEFAULT(Color.parseColor("#34ACE0")),
    TRAFFIC_DEFAULT(Color.parseColor("#EDBE19")),
    ARSON_DEFAULT(Color.parseColor("#ED2F2F"));
}

class MarkerGenerator {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val diameter = 20.dp
    private val radius = 10f.dp
    private val center = 10f.dp

    suspend fun generateMarkers(centerLocation: Location): List<MarkerDescription> = withContext(Dispatchers.IO) {
        listOf(
            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.MINOR_DEFAULT.color), type = "Alert", title = "Small alert", isAlert = false),
            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.MINOR_DEFAULT.color), type = "Alert", title = "Small alert", isAlert = false),
            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.MINOR_DEFAULT.color), type = "Alert", title = "Small alert", isAlert = false),

            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.POLICE_ACTIVITY.color), type = "Alert", title = "Police alert", isAlert = false),
            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.POLICE_ACTIVITY.color), type = "Alert", title = "Police alert", isAlert = false),
            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.POLICE_ACTIVITY.color), type = "Alert", title = "Police alert", isAlert = false),

            MarkerDescription(
                generateMarkerOptions(centerLocation, Alerts.EXTREME_DEFAULT.color),
                type = "Extreme Alert",
                title = "High extreme alert",
                isAlert = true
            ),
            MarkerDescription(
                generateMarkerOptions(centerLocation, Alerts.EXTREME_DEFAULT.color),
                type = "Extreme Alert",
                title = "High extreme alert",
                isAlert = true
            ),
            MarkerDescription(
                generateMarkerOptions(centerLocation, Alerts.EXTREME_DEFAULT.color),
                type = "Extreme Alert",
                title = "High extreme alert",
                isAlert = true
            ),

            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.MODERATE_DEFAULT.color), type = "Alert", title = "Moderate alert", isAlert = false),
            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.MODERATE_DEFAULT.color), type = "Alert", title = "Moderate alert", isAlert = false),
            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.MODERATE_DEFAULT.color), type = "Alert", title = "Moderate alert", isAlert = false),

            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.TRAFFIC_DEFAULT.color), type = "Alert", title = "Traffic alert", isAlert = false),
            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.TRAFFIC_DEFAULT.color), type = "Alert", title = "Traffic alert", isAlert = false),
            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.TRAFFIC_DEFAULT.color), type = "Alert", title = "Traffic alert", isAlert = false),

            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.ARSON_DEFAULT.color), type = "Alert", title = "Arson alert", isAlert = false),
            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.ARSON_DEFAULT.color), type = "Alert", title = "Arson alert", isAlert = false),
            MarkerDescription(generateMarkerOptions(centerLocation, Alerts.ARSON_DEFAULT.color), type = "Alert", title = "Arson alert", isAlert = false)
        )
    }

    private fun generateMarkerOptions(centerLocation: Location, color: Int) =
        MarkerOptions()
            .position(centerLocation.randomNearby())
            .icon(BitmapDescriptorFactory.fromBitmap(generateMarkerBitmap(color)))

    private fun generateMarkerBitmap(@ColorInt color: Int): Bitmap {
        val result = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
        result.eraseColor(Color.TRANSPARENT)

        val canvas = Canvas(result)
        paint.color = color
        paint.alpha = 128
        canvas.drawCircle(center, center, radius, paint)
        paint.alpha = 255
        canvas.drawCircle(center, center, radius - 4, paint)

        return result
    }
}

