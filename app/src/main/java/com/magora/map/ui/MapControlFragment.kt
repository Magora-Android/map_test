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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.magora.map.R
import com.magora.map.viewmodel.LocationEvents
import com.magora.map.viewmodel.VmMap

/*
 * Created by Magora Systems. 
 * 03.09.19.
 */
class MapControlFragment : BaseFragment(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var viewModel: VmMap
    private var btnMyLocation: View? = null

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
    }

    private fun subscribeToEvents() {
        viewModel.mapStyleLiveData.observe(this, Observer { onMapOptionsLoaded(it) })
        viewModel.myLocationLiveData.observe(this, Observer { onMyLocationEvent(it) })
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
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(event.location.latitude, event.location.longitude)))
            }
            is LocationEvents.Error -> {
                btnMyLocation?.isEnabled = true
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
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
    }

    companion object {

        fun createFragment() = MapControlFragment()
    }
}