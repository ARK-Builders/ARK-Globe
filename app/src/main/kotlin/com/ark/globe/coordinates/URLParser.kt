package com.ark.globe.coordinates

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class URLParser {
    companion object{
        private const val GOOGLE_MAPS = "maps.google.com"
        private const val OPENSTREETMAP = "www.openstreetmap.org"
        private const val URL_PROTOCOL_1 = "http"
        private const val URL_PROTOCOL_2 = "https"

        suspend fun extractCoordinates(url: String?): Coordinates?{
            return withContext(Dispatchers.Default) {
                var latitude: String? = null
                var longitude: String? = null

                if (url != null && url.contains(GOOGLE_MAPS)) {
                    //google maps link processing
                    val coordinatesString = url.substringAfter("?q=").substringBefore("&")
                    latitude = coordinatesString.substringBefore(",")
                    longitude = coordinatesString.substringAfter(",")
                }

                if (url != null && url.contains(OPENSTREETMAP)) {
                    //openstreetmap link processing
                    val coordinatesString = url.substringAfter("#map").substringAfter("/")
                    latitude = coordinatesString.substringBefore("/")
                    longitude = coordinatesString.substringAfter("/")
                }

                if (latitude?.toDoubleOrNull() != null && longitude?.toDoubleOrNull() != null)
                    Coordinates(latitude.toDouble(), longitude.toDouble())
                else
                    null
            }
        }

        fun getValidURL(urlStr: String?): String?{
            return if(urlStr != null){
                if(urlStr.contains(URL_PROTOCOL_2)) {
                    URL_PROTOCOL_2 + urlStr.substringAfter(URL_PROTOCOL_2)
                }
                else if(urlStr.contains(URL_PROTOCOL_1)){
                    URL_PROTOCOL_1 + urlStr.substringAfter(URL_PROTOCOL_1)
                }
                else null
            }
            else null
        }
    }
}