package com.ark.globe.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate
import com.ark.globe.R

class GlobePreferences private constructor(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(GLOBE_PREFS, MODE_PRIVATE)
    private val editor = sharedPreferences.edit()
    private val pathKEY = context.getString(R.string.path_pref_key)
    private val nightModeKEY = context.getString(R.string.dark_mode_pref_key)

    fun storePath(path: String){
        with(editor){
            putString(pathKEY, path)
            apply()
        }
    }

    fun getPath() = sharedPreferences.getString(pathKEY, null)

    fun storeNightMode(nightMode: Int){
        with(editor){
            putInt(nightModeKEY, nightMode)
            apply()
        }
    }

    fun getNightMode() = sharedPreferences.getInt(nightModeKEY, AppCompatDelegate.MODE_NIGHT_NO)

    companion object{
        private const val GLOBE_PREFS = "globe_prefs"
        private var instance: GlobePreferences? = null

        fun getInstance(context: Context): GlobePreferences{
            if(instance == null){
                instance = GlobePreferences((context))
            }
            return instance!!
        }
    }
}