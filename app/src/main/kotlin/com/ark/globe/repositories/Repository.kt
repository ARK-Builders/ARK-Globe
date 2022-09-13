package com.ark.globe.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ark.globe.BuildConfig
import com.ark.globe.coordinates.Coordinates
import com.ark.globe.coordinates.Location
import com.ark.globe.jsonprocess.JSONFile
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.net.HttpURLConnection
import javax.inject.Inject

class Repository @Inject constructor() {
    private var coordinatesString = ""
    private var latitude: String? = null
    private var longitude: String? = null

    fun extractCoordinates(url: String?): Coordinates?{
        var coordinates: Coordinates? = null
        val validUrl = getValidURL(url)
        Log.d("Valid URL", validUrl.toString())
        latitude = null
        longitude = null
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
        Log.d("Parsed Coordinates", "$latitude, $longitude")
        return coordinates
    }

    private fun parseGoogleMapsLinks(url: String?){
        if(url != null) {
            if (url.contains(GOOGLE_MAPS)) {
                val uri = Uri.parse(url)
                val coordinates = uri.getQueryParameter("q")
                val coords = coordinates?.split(",")
                if(coords != null) {
                    latitude = coords[0]
                    longitude = coords[1]
                }
            } else {
                loadUrl(url)
            }
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
            client = OkHttpClient.Builder().build()
            request = Request.Builder()
                .url(url)
                .build()
            try {
                response = client.newCall(request).execute()

                val bReader = BufferedReader(response.body?.charStream())
                val status = response.code
                var actualUrl = ""
                Log.d("Status", status.toString())
                var isParsed = false
                if (status == HttpURLConnection.HTTP_OK) {
                    actualUrl = response.request.url.toString()
                    Log.d("Actual URL", actualUrl)
                    bReader.forEachLine {
                        //println("Line: $it")
                        if (it.contains("/@"))
                            Log.d("Redirect", it.substringAfter("/@"))
                        isParsed = parseGoogleCoordinates(it)
                        if (isParsed) {
                            Log.d("Is Parsed", isParsed.toString())
                            return@forEachLine
                        }
                    }
                }
//  https://maps.app.goo.gl/XTudbauXz5ZdfXAWA
//  https://www.google.com/maps?q=Google+Building+1600,+1600+Plymouth+St,+Mountain+View,+CA+94043,+USA&ftid=0x808fba002c047109:0x8a6e9df8c478269&hl=en&gl=us&g_ep=GAA%3D&shorturl=1https://www.google.com/maps?q=Google+Building+1600,+1600+Plymouth+St,+Mountain+View,+CA+94043,+USA&ftid=0x808fba002c047109:0x8a6e9df8c478269&hl=en&gl=us&g_ep=GAA%3D&shorturl=1https://www.google.com/maps?q=Google+Building+1600,+1600+Plymouth+St,+Mountain+View,+CA+94043,+USA&ftid=0x808fba002c047109:0x8a6e9df8c478269&hl=en&gl=us&g_ep=GAA%3D&shorturl=1
                if(!isParsed) {
                    Log.d("Is parsed", isParsed.toString())
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
            true
        }
        else false
    }

    fun saveLocation(context: Context, location: Location?){
        JSONFile.saveLocation(context, location)
    }

    fun readJsonLocations(context: Context): List<Location>{
        return JSONFile.readJsonLocations(context)
    }

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
    }
}