package com.magora.map.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.magora.map.R

/*
 * Created by Magora Systems. 
 * 03.09.19.
 */
class MapControlFragment : BaseFragment(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        initButtons(view)
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initButtons(v: View) {

    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        initMapUi(map)
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