package com.magora.map.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.magora.map.R
import com.magora.map.utils.MarkerDescription
import com.magora.map.viewmodel.LocationEvents
import com.magora.map.viewmodel.VmMap
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.include_bottom_sheet.*

/*
 * Created by Magora Systems. 
 * 03.09.19.
 */
class MapControlFragment : BaseFragment(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var viewModel: VmMap
    private var btnMyLocation: View? = null
    private var btnCancel: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this)[VmMap::class.java]
        initMap()
        initButtons(view)
        subscribeToEvents()
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initButtons(v: View) {
        btnMyLocation = v.findViewById<View>(R.id.btn_my_location)?.apply {
            setOnClickListener {
                viewModel.onCurrentLocationButtonClicked()
            }
        }

        btnCancel = v.findViewById<View>(R.id.btn_cancel)?.apply {
            setOnClickListener {
                if (it.alpha == 1f && it.visibility == View.VISIBLE) {
                    hideMarkerInfo()
                }
            }
        }
    }

    private fun subscribeToEvents() {
        viewModel.mapStyleLiveData.observe(this, Observer { onMapOptionsLoaded(it) })
        viewModel.myLocationLiveData.observe(this, Observer { onMyLocationEvent(it) })
        viewModel.markersLiveData.observe(this, Observer { onMarkersLoaded(it) })
    }

    private fun onMapOptionsLoaded(options: MapStyleOptions?) {
        kotlin.runCatching {
            if (!googleMap.setMapStyle(options)) {
                Log.i("MapFragment", "Can't apply map style")
            }
        }
    }

    private fun onMyLocationEvent(event: LocationEvents) {
        when (event) {
            LocationEvents.Loading -> {
                btnMyLocation?.isEnabled = false
            }
            is LocationEvents.Loaded -> {
                btnMyLocation?.isEnabled = true
                val position = CameraPosition(LatLng(event.location.latitude, event.location.longitude), 10.0f, 0f, 0f)
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position))
            }
            is LocationEvents.Error -> {
                btnMyLocation?.isEnabled = true
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onMarkersLoaded(markers: List<MarkerDescription>) {
        googleMap.clear()
        markers.forEach { description ->
            val marker = googleMap.addMarker(description.options)
            marker.tag = description
        }
    }

    private fun onMarkerClicked(description: MarkerDescription) {
        textAlertLabel.text = description.type
        textAlertTitle.text = description.title

        if (rootContainer.currentState == R.id.set_info_hidden) {
            rootContainer.transitionToState(R.id.set_info_visible_half)
        }
    }

    private fun hideMarkerInfo() {
        if (rootContainer.currentState != R.id.set_info_hidden) {
            rootContainer.transitionToState(R.id.set_info_hidden)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        initMapUi(map)
        viewModel.onMapReady()
    }

    private fun initMapUi(map: GoogleMap) {
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false
        map.uiSettings.isMapToolbarEnabled = false

        map.setOnMarkerClickListener { marker ->
            (marker.tag as? MarkerDescription)?.let(::onMarkerClicked)
            true
        }
    }

    companion object {

        fun createFragment() = MapControlFragment()
    }
}