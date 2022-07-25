package com.ark.globe

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ark.globe.databinding.ActivityMainBinding
import com.ark.globe.filehandling.FilePicker
import com.ark.globe.fragments.Settings
import com.ark.globe.fragments.locations.Locations
import com.ark.globe.preferences.GlobePreferences
import space.taran.arkfilepicker.onArkPathPicked

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val locationsFragment = Locations()
    private val settingsFragment = Settings()

    init{
        FilePicker.readPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if(isGranted){
                FilePicker.show(supportFragmentManager)
            }
            else {
                FilePicker.permissionDeniedError(this)
                this.finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        delegate.localNightMode = GlobePreferences.getInstance(this).getNightMode()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener{
            onBackPressed()
        }

        FilePicker.show(this, supportFragmentManager)

        locationsFragment.sendIntent(intent)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().apply {
                add(R.id.container, locationsFragment, Locations.TAG)
                commit()
            }
        }

        supportFragmentManager.onArkPathPicked(this){
            val globePrefs = GlobePreferences.getInstance(this)
            globePrefs.storePath("$it${Settings.FOLDER_NAME}")
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