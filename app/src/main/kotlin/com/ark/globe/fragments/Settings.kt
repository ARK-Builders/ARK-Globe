package com.ark.globe.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.ark.globe.R
import com.ark.globe.filehandling.FilePicker
import com.ark.globe.fragments.locations.LocationsViewModel
import com.ark.globe.fragments.ui.PathPreference
import com.ark.globe.jsonprocess.JSONFile
import com.ark.globe.preferences.GlobePreferences
import space.taran.arkfilepicker.onArkPathPicked

class Settings : PreferenceFragmentCompat() {

    private val activity: AppCompatActivity by lazy {
        requireActivity() as AppCompatActivity
    }

    private val lViewModel: LocationsViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        activity.title = getString(R.string.settings)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pathKEY = getString(R.string.path_pref_key)
        val nightModeKEY = getString(R.string.dark_mode_pref_key)
        val pathPref: PathPreference? = findPreference(pathKEY)
        val darkModePref: SwitchPreferenceCompat? = findPreference(nightModeKEY)
        val supportActionBar = activity.supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pathPref?.setOnPreferenceClickListener {
            FilePicker.show(requireActivity() as AppCompatActivity, parentFragmentManager)
            true
        }

        darkModePref?.setOnPreferenceChangeListener { preference, _ ->
            preference as SwitchPreferenceCompat
            val nightMode = if(preference.isChecked)
                AppCompatDelegate.MODE_NIGHT_NO
            else AppCompatDelegate.MODE_NIGHT_YES
            GlobePreferences.getInstance(requireContext()).storeNightMode(nightMode)
            (requireActivity() as AppCompatActivity).delegate.localNightMode = nightMode
            true
        }

        parentFragmentManager.onArkPathPicked(viewLifecycleOwner) {
            val globePrefs = GlobePreferences.getInstance(requireContext())
            globePrefs.storePath("$it")
            lViewModel.addLocations(JSONFile.readJsonLocations(requireContext()))
            pathPref?.setPath(globePrefs.getPath())
        }
    }

    companion object{
        const val TAG = "Settings"
    }
}