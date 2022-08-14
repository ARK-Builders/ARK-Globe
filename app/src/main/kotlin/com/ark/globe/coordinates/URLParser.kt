package com.ark.globe.coordinates

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.net.HttpURLConnection.HTTP_OK

class URLParser {
    companion object{
        private const val GOOGLE_MAPS = ".google."
        private const val GOOGLE_MAPS_SHORT = "goo.gl"
        private const val OPEN_STREET_MAP = "www.openstreetmap.org"
        private const val OPEN_STREET_MAP_SHORT = "osm.org"
        private const val OSM_AND_NET = "osmand.net"
        private const val URL_PROTOCOL_1 = "http"
        private const val URL_PROTOCOL_2 = "https"
        private const val DELIMITER_1 = "/@"
        private const val DELIMITER_2 = ","
        private const val DELIMITER_3 = "/"
        private var coordinatesString = ""
        private var latitude: String? = null
        private var longitude: String? = null
        private val ioDispatcher = Dispatchers.IO

        suspend fun extractCoordinates(url: String?): Coordinates?{
            var coordinates: Coordinates? = null
            val validUrl = getValidURL(url)
            println("Valid URL: $validUrl")
            withContext(ioDispatcher) {
                if(validUrl != null && (validUrl.contains(GOOGLE_MAPS) || validUrl.contains(GOOGLE_MAPS_SHORT)))
                    parseGoogleMapsLinks(validUrl)

                if (validUrl != null && (validUrl.contains(OPEN_STREET_MAP)
                            || validUrl.contains(OPEN_STREET_MAP_SHORT)))
                    //openstreetmap link processing
                    //https://www.openstreetmap.org/#map=8/-13.397/34.31
                    parseOpenStreetMapLinks(validUrl)

                if (validUrl != null && validUrl.contains(OSM_AND_NET))
                    //osmand.net link processing
                    //https://osmand.net/go?lat=36.54151&lon=31.99651&z=17
                    parseOSMANDNETLinks(validUrl)

                if (latitude?.toDoubleOrNull() != null && longitude?.toDoubleOrNull() != null) {
                    coordinates = Coordinates(latitude?.toDouble(), longitude?.toDouble())
                }
            }
            return coordinates
        }

        private fun parseGoogleMapsLinks(url: String?){
            if(url != null && url.contains(GOOGLE_MAPS)) {
                val uri = Uri.parse(url)
                val coordinates = uri.getQueryParameter("q")
                val coords = coordinates?.split(",")
                latitude = coords?.get(0)
                longitude = coords?.get(1)
            }
            else {
                loadUrl(url)
            }
        }

        private fun parseOpenStreetMapLinks(url: String?){
            if(url != null && url.contains(OPEN_STREET_MAP)){
                val uri = Uri.parse(url)
                val frag = uri.fragment
                if(frag != null)
                    coordinatesString = frag
                val splitString = coordinatesString.split(DELIMITER_3)
                latitude = splitString[1]
                longitude = splitString[2]
            }
            if (url != null && url.contains(OPEN_STREET_MAP_SHORT))
                loadUrl(url)
        }

        private fun parseOSMANDNETLinks(url: String?){
            val uri = Uri.parse(url)
            latitude = uri.getQueryParameter("lat")
            longitude = uri.getQueryParameter("lon")
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

        private fun loadUrl(url: String?){
            val client: OkHttpClient
            val request: Request
            var response: okhttp3.Response? = null
            if(url != null) {
                client = OkHttpClient()
                request = Request.Builder()
                    .url(url)
                    .build()
                try {
                    response = client.newCall(request).execute()
                    val bReader = BufferedReader(response.body?.charStream())
                    val status = response.code
                    val actualUrl = response.request.url.toString()
                    println("Status: $status")
                    println("URL: $actualUrl")
                    var isNotParsed = false
                    if (status == HTTP_OK)
                        bReader.forEachLine {
                            //println("Line: $it")
                            isNotParsed = parseGoogleCoordinates(it)
                            if(!isNotParsed)
                                return@forEachLine
                        }

                    if(isNotParsed) {
                        println("Is not parsed...")
                        if (actualUrl.contains(GOOGLE_MAPS)) {
                            parseGoogleMapsLinks(actualUrl)
                            return
                        }

                        if (actualUrl.contains(OPEN_STREET_MAP)) {
                            parseOpenStreetMapLinks(actualUrl)
                            return
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    response?.close()
                }
            }
        }

        private fun parseGoogleCoordinates(line: String): Boolean{
            return if(line.contains(DELIMITER_1)){
                coordinatesString = line.substringAfter(DELIMITER_1).substringBefore(DELIMITER_3)
                val coords = coordinatesString.split(DELIMITER_2)
                latitude = coords[0]
                longitude = coords[1]
                println("Start: $coordinatesString")
                println("Latitude: $latitude Longitude: $longitude")
                true
            }
            else false
        }
    }
}
