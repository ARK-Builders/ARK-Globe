package com.ark.globe.coordinates

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class URLParser {
    companion object{
        private const val GOOGLE_MAPS = "maps.google.com"
        private const val GOOGLE_MAPS_SHORT = "goo.gl"
        private const val OPENSTREETMAP = "www.openstreetmap.org"
        private const val OSM_AND_NET = "osmand.net"
        private const val URL_PROTOCOL_1 = "http"
        private const val URL_PROTOCOL_2 = "https"
        private var coordinatesString = ""
        private var latitude: String? = null
        private var longitude: String? = null

        private val ioDispatcher = Dispatchers.IO

        suspend fun extractCoordinates(url: String?): Coordinates?{
            var coordinates: Coordinates? = null
            val validUrl = getValidURL(url)

            withContext(ioDispatcher) {

                parseGoogleMapsLinks(validUrl)

                if (validUrl != null && validUrl.contains(OPENSTREETMAP)) {
                    //openstreetmap link processing
                    coordinatesString = validUrl.substringAfter("#map").substringAfter("/")
                    latitude = coordinatesString.substringBefore("/")
                    longitude = coordinatesString.substringAfter("/")
                }

                if (validUrl != null && validUrl.contains(OSM_AND_NET)) {
                    //osmand.net link processing
                    coordinatesString = validUrl.substringAfter("lat=").substringBefore("&z")
                    latitude = coordinatesString.substringBefore("&lon=")
                    longitude = coordinatesString.substringAfter("&lon=")
                }

                if (latitude?.toDoubleOrNull() != null && longitude?.toDoubleOrNull() != null) {
                    coordinates = Coordinates(latitude?.toDouble(), longitude?.toDouble())
                }
            }
            return coordinates
        }

        private fun parseGoogleMapsLinks(url: String?){

            if (url != null && url.contains(GOOGLE_MAPS) && url.contains("/?q=")) {
                //google maps link processing
                coordinatesString = url.substringAfter("/?q=").substringBefore("&")
                latitude = coordinatesString.substringBefore(",")
                longitude = coordinatesString.substringAfter(",")
            }

            if (url != null && url.contains(GOOGLE_MAPS_SHORT)) {

                val fullUrl = getFullUrl(url)

                if (fullUrl != null && fullUrl.contains("/@")) {
                    coordinatesString = fullUrl.substringAfter("/@").substringBefore("z/")
                    latitude = coordinatesString.substringBefore(",")
                    longitude = coordinatesString.substringAfter(",").substringBefore(",")
                }

                if(fullUrl != null && fullUrl.contains("/?q="))
                    parseGoogleMapsLinks(fullUrl)
            }

        }

        private fun getValidURL(urlStr: String?): String?{
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

        private fun connect( urlStr: String?): String?{
            var conn: HttpURLConnection? = null
            return try{
                val url = URL(urlStr)
                conn = url.openConnection() as HttpURLConnection
                conn.instanceFollowRedirects = false
                var redirect = false
                val status = conn.responseCode

                if(status != HttpURLConnection.HTTP_OK)
                    if(status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                        redirect = true

                if(redirect)
                     conn.getHeaderField("Location")
                else null
            }
            catch (e: Exception){
                e.printStackTrace()
                null
            }
            finally {
                conn?.disconnect()
            }
        }

        private fun getFullUrl(shortUrl: String?): String?{
            return connect(shortUrl)
        }
    }
}