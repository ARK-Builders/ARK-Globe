package com.ark.globe.fragments.locations

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ark.globe.coordinates.Coordinates
import com.ark.globe.coordinates.Location
import com.ark.globe.repositories.Repository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(): ViewModel() {

    private val iODispatcher = Dispatchers.IO
    @Inject lateinit var repository: Repository
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
                coordinates(repository.extractCoordinates(url))
            }
        }
    }

    fun getValidUrl(urlStr: String?) = repository.getValidURL(urlStr)

    fun saveLocation(context: Context, location: Location){
        viewModelScope.launch {
            withContext(iODispatcher) {
                repository.saveLocation(context, location)
            }
        }
    }

    fun readJsonLocations(context: Context){
        if(locationsList.isNotEmpty())
            locationsList.clear()
        viewModelScope.launch {
            withContext(iODispatcher){
                locationsList.addAll(repository.readJsonLocations(context))
            }
        }
        _locations.postValue(locationsList)
    }
}