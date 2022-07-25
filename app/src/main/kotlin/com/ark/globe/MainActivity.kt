package com.ark.globe

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.ark.globe.databinding.ActivityMainBinding
import com.ark.globe.fragments.Settings
import com.ark.globe.fragments.locations.Locations
import com.ark.globe.preferences.GlobePreferences

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val locationsFragment = Locations()
    private val settingsFragment = Settings()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        delegate.localNightMode = GlobePreferences.getInstance(this).getNightMode()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener{
            onBackPressed()
        }

        locationsFragment.sendIntent(intent)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().apply {
                add(R.id.container, locationsFragment, Locations.TAG)
                commit()
            }
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