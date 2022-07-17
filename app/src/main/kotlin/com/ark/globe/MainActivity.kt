package com.ark.globe

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.ark.globe.databinding.ActivityMainBinding
import com.ark.globe.fragments.manualentry.ManualEntry

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainerView, ManualEntry(), ManualEntry.TAG)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}