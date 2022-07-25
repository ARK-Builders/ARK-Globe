package com.ark.globe.jsonprocess

import android.content.Context
import com.ark.globe.preferences.GlobePreferences
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension

class JSONFile {
    companion object{
        private const val JSON_EXT = "json"
        private const val FILE_NAME = "Location "

        fun createJsonFile(path: Path?, jsonString: String){
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

        fun readJsonFile(path: Path?) {
            var numberOfFiles = 0
            if (path != null) {
                Files.list(path). forEach{
                    if(it.fileName.extension == JSON_EXT) {
                        try {
                            val jsonFile = it.toFile()
                            val fileReader = FileReader(jsonFile)
                            val bufferedReader = BufferedReader(fileReader)
                            with(bufferedReader) {
                                println("${readText()} ${numberOfFiles++}")
                                close()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        fun getPath(context: Context):Path?{
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