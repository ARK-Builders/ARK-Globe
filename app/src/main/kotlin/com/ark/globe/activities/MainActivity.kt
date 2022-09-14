package com.ark.globe.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ark.globe.R
import com.ark.globe.contracts.PermissionContract
import com.ark.globe.databinding.ActivityMainBinding
import com.ark.globe.filehandling.FilePicker
import com.ark.globe.fragments.Settings
import com.ark.globe.fragments.locations.LocationsFragment
import com.ark.globe.preferences.GlobePreferences
import dagger.hilt.android.AndroidEntryPoint
import space.taran.arkfilepicker.presentation.onArkPathPicked

@AndroidEntryPoint
class MainActivity: AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind)
    private val locationsFragment = LocationsFragment()
    private val settingsFragment = Settings()

    init{
        FilePicker.readPermLauncher_SDK_R = registerForActivityResult(PermissionContract()){ isGranted ->
            if(isGranted)
                FilePicker.show()
            else{
                FilePicker.permissionDeniedError(this)
                finish()
            }
        }

        FilePicker.readPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if(isGranted){
                FilePicker.show()
            }
            else{
                FilePicker.permissionDeniedError(this)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        delegate.localNightMode = GlobePreferences.getInstance(this).getNightMode()
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener{
            onBackPressed()
        }

        locationsFragment.sendIntent(intent)

        if(GlobePreferences.getInstance(this).getPath() == null)
            FilePicker.show(this, supportFragmentManager)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().apply {
                add(R.id.container, locationsFragment, LocationsFragment.TAG)
                commit()
            }
        }

        supportFragmentManager.onArkPathPicked(this) {
            val globePrefs = GlobePreferences.getInstance(this)
            globePrefs.storePath("$it")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings -> {
                supportFragmentManager.beginTransaction().apply {
                    val backStackName = settingsFragment.javaClass.name
                    val popBackStack = supportFragmentManager.popBackStackImmediate(backStackName, 0)
                    if(!popBackStack) {
                        replace(R.id.container, settingsFragment, Settings.TAG)
                        addToBackStack(backStackName)
                        commit()
                    }
                    else{
                        show(settingsFragment)
                        commit()
                    }
                }
            }
        }
        return true
    }
}