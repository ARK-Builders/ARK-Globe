package com.ark.globe.jsonprocess

import com.ark.globe.coordinates.Location
import com.ark.globe.coordinates.Locations
import com.google.gson.Gson

class JSONParser {
    companion object {

        private val gson = Gson()

        fun parseLocationToJSON(location: Location): String {
            return gson.toJson(location, Location::class.java)
        }

        fun parseLocationsToJSON(locations: Locations): String{
            return gson.toJson(locations, Locations::class.java)
        }
    }
}