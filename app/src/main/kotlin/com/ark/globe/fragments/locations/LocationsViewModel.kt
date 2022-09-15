package com.ark.globe.fragments.locations

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ark.globe.coordinates.Coordinates
import com.ark.globe.coordinates.Location
import com.ark.globe.repositories.LocationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(): ViewModel() {

    private val iODispatcher = Dispatchers.IO
    @Inject lateinit var locationsRepository: LocationsRepository
    private val locationsList = mutableListOf<Location>()

    private val _locations: MutableLiveData<List<Location>> by lazy {
        MutableLiveData<List<Location>>()
    }
    val locations: LiveData<List<Location>>  = _locations

    private val _coordinates: MutableLiveData<Coordinates?> by lazy {
        MutableLiveData<Coordinates?>()
    }

    val coordinates: LiveData<Coordinates?> = _coordinates

    fun writeCoordinates(coordinates: Coordinates?) {
        _coordinates.value = coordinates
    }

    fun addLocation(location: Location){
        locationsList.add(location)
        _locations.value  = locationsList
    }

    fun extractCoordinates(url: String?, coordinates: (Coordinates?) -> Unit){
        viewModelScope.launch {
            withContext(iODispatcher){
                coordinates(locationsRepository.extractCoordinates(url))
            }
        }
    }

    fun getValidUrl(urlStr: String?) = locationsRepository.getValidURL(urlStr)

    fun saveLocation(context: Context, location: Location){
        viewModelScope.launch {
            withContext(iODispatcher) {
                locationsRepository.saveLocation(context, location)
            }
        }
    }

    fun readJsonLocations(context: Context){
        if(locationsList.isNotEmpty())
            locationsList.clear()
        viewModelScope.launch {
            withContext(iODispatcher){
                locationsList.addAll(locationsRepository.readJsonLocations(context))
            }
        }
        _locations.postValue(locationsList)
    }
}