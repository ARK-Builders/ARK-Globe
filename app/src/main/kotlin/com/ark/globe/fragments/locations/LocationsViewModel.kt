package com.ark.globe.fragments.locations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ark.globe.coordinates.Coordinates
import com.ark.globe.coordinates.Location

class LocationsViewModel: ViewModel() {

    private val locationsList = mutableListOf<Location>()
    private val _locations:
            MutableLiveData<List<Location>> by lazy{
        MutableLiveData<List<Location>>().also{
            it.value = listOf()
        }
    }
    val locations: LiveData<List<Location>> = _locations

    private val _coordinates:
            MutableLiveData<Coordinates> by lazy {
        MutableLiveData<Coordinates>().also{
            it.value = Coordinates()
        }
    }

    val coordinates: LiveData<Coordinates> = _coordinates

    fun writeCoordinates(coordinates: Coordinates?) {
        _coordinates.value = coordinates
    }

    fun addLocations(locations: List<Location>){
        locationsList.addAll(locations)
        _locations.value = locationsList
    }

    fun addLocation(location: Location){
        locationsList.add(location)
        _locations.value  = locationsList
    }

}