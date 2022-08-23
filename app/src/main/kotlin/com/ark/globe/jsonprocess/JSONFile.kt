package com.ark.globe.jsonprocess

import android.content.Context
import android.util.Log
import com.ark.globe.coordinates.Location
import com.ark.globe.preferences.GlobePreferences
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension

class JSONFile {
    companion object{
        private const val JSON_EXT = "json"
        private const val FILE_NAME = "Location "

        private fun createJsonFile(path: Path?, jsonString: String){
            var numberOfFiles = 0
            if(path != null) {
                Files.list(path).forEach {
                    if (it.fileName.extension == JSON_EXT) {
                        numberOfFiles++
                    }
                }
                val file = path.toFile()
                val jsonFile = File(file, "$FILE_NAME$numberOfFiles.$JSON_EXT")
                if (!jsonFile.exists()) {
                    try {
                        val fileWriter = FileWriter(jsonFile)
                        val bufferedWriter = BufferedWriter(fileWriter)
                        with(bufferedWriter) {
                            write(jsonString)
                            close()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun saveLocation(context: Context, location: Location?){
            if (location != null) {
                val path = getPath(context)
                if (path != null) {
                    createJsonFile(
                        path,
                        JSONParser.parseLocationToJSON(location)
                    )
                }
            }
        }

        fun readJsonLocations(context: Context):List<Location> {
            var numberOfFiles = 0
            val locations = mutableListOf<Location>()
            val path = getPath(context)
            if (path != null) {
                Files.list(path). forEach{ filePath ->
                    if(filePath.fileName.extension == JSON_EXT) {
                        try {
                            val jsonFile = filePath.toFile()
                            val fileReader = FileReader(jsonFile)
                            val bufferedReader = BufferedReader(fileReader)
                            val jsonLocation = StringBuilder()
                            with(bufferedReader) {
                                forEachLine {
                                    jsonLocation.append(it)
                                }
                                locations.add(JSONParser.parseFromJsonToLocation(jsonLocation.toString()))
                                Log.d("File ${numberOfFiles++}:",  jsonLocation.toString())
                                close()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            return locations
        }

        private fun getPath(context: Context):Path?{
            val prefs = GlobePreferences.getInstance(context)
            val pathString = prefs.getPath()
            var path: Path? = null
            try {
                val file = File(pathString!!)
                file.mkdir()
                path = file.toPath()
            }
            catch(e: Exception) {
                e.printStackTrace()
            }
            return path
        }
    }
}