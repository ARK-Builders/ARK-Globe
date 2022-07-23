package com.ark.globe.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.ark.globe.R
import com.ark.globe.contracts.FileAccessContract
import com.ark.globe.filehandling.FilePicker
import com.ark.globe.fragments.ui.PathPreference
import com.ark.globe.preferences.GlobePreferences
import space.taran.arkfilepicker.onArkPathPicked

class Settings : PreferenceFragmentCompat() {

    private val activity: AppCompatActivity by lazy {
        requireActivity() as AppCompatActivity
    }

    init{
        FilePicker.readPermLauncherSDK_R = registerForActivityResult(FileAccessContract()){
            if(FilePicker.isReadPermissionGranted(requireActivity() as AppCompatActivity)){
                FilePicker.show(parentFragmentManager)
            }
            else FilePicker.permissionDeniedError(requireActivity() as AppCompatActivity)
        }

        FilePicker.readPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if(isGranted){
                FilePicker.show(parentFragmentManager)
            }
            else FilePicker.permissionDeniedError(requireActivity() as AppCompatActivity)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
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
            globePrefs.storePath("$it$FOLDER_NAME")
            pathPref?.setPath(globePrefs.getPath())
        }
    }

    companion object{
        const val TAG = "Settings"
        private const val FOLDER_NAME = "/My Locations"
    }
}