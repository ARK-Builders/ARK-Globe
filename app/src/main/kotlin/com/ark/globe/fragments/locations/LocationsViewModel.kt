package com.ark.globe.fragments.locations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ark.globe.coordinates.Coordinates
import com.ark.globe.coordinates.Location

class LocationsViewModel: ViewModel() {

    private val coordinateList:
        MutableLiveData<MutableList<Coordinates>> by lazy{
            MutableLiveData<MutableList<Coordinates>>().also{
                it.value = mutableListOf()
            }
    }

    private val locationList:
            MutableLiveData<MutableList<Location>> by lazy{
        MutableLiveData<MutableList<Location>>().also{
            it.value = mutableListOf()
        }
    }

    private val _coordinates:
            MutableLiveData<Coordinates> by lazy {
        MutableLiveData<Coordinates>().also{
            it.value = Coordinates()
        }
    }
    val coordinates: LiveData<Coordinates> = _coordinates

    private val _coordinatesURL = MutableLiveData<String>()
    val coordinatesURL: LiveData<String> = _coordinatesURL

    fun writeCoordinates(coordinates: Coordinates?) {
        if (coordinates != null) {
            _coordinates.value = coordinates
        }
    }

    private fun addLocations(locations: List<Location>){
        if(locations.isNotEmpty()){
            locationList.value = locations as MutableList<Location>?
        }
    }

    fun addLocation(location: Location){
        locationList.value?.add(location)
        addLocations(locationList.value!!)
    }

    fun setCoordinatesUrl(url: String?){
        _coordinatesURL.postValue(url)
    }

    fun addCoordinates(coordinates: Coordinates){
        coordinateList.value?.add(coordinates)
    }

    fun addCoordinates(coordinates: List<Coordinates>) {
        if (coordinates.isNotEmpty()) {
            coordinateList.value?.addAll(coordinates)
        }
    }

    fun getCoordinatesList() = coordinateList

    fun getLocations() = locationList
}