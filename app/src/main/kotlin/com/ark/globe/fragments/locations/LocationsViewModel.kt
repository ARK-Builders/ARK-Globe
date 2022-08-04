package com.ark.globe.fragments.locations

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ark.globe.coordinates.Coordinates
import com.ark.globe.coordinates.Location
import com.ark.globe.coordinates.URLParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    val coordinates:
            MutableLiveData<Coordinates> by lazy {
        MutableLiveData<Coordinates>().also{
            it.value = Coordinates()
        }
    }

    val coordinatesURL = MutableLiveData<String>()

    fun writeCoordinates(coordinates: Coordinates?) {
        if (coordinates != null) {
            this.coordinates.value?.latitude = coordinates.latitude
            this.coordinates.value?.longitude = coordinates.longitude
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

    fun getFullUrl(url: String?){
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                URLParser.connect(url)
            }
        }
    }
}