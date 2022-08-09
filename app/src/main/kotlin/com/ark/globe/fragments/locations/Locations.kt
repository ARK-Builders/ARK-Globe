package com.ark.globe.fragments.locations

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ark.globe.R
import com.ark.globe.adapters.LocationsAdapter
import com.ark.globe.coordinates.Coordinates
import com.ark.globe.coordinates.Location
import com.ark.globe.coordinates.Locations
import com.ark.globe.coordinates.URLParser
import com.ark.globe.jsonprocess.JSONFile
import com.ark.globe.jsonprocess.JSONParser
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.runBlocking

class Locations: Fragment() {

    private val lViewModel: LocationsViewModel by viewModels()
    private var adapter: LocationsAdapter? = null
    private var intent: Intent? = null
    private var longitude: EditText? = null
    private var latitude: EditText? = null

    private val urlChangeListener = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (intent == null && s != null && s.isNotEmpty()) {
                val coordinates = runBlocking{
                    URLParser.extractCoordinates(s.toString())
                }
                lViewModel.writeCoordinates(coordinates)
                println("ViewModel Latitude: ${coordinates?.latitude}")
                println("ViewModel Longitude: ${coordinates?.longitude}")
                onCoordinatesChanged()
            }
        }

        override fun afterTextChanged(s: Editable?){
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    }

    fun sendIntent(intent: Intent){
        this.intent = intent
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return inflater.inflate(R.layout.manual_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val locationName: EditText = view.findViewById(R.id.locationName)
        val locationDesc: EditText = view.findViewById(R.id.locationDesc)
        val urlText: EditText = view.findViewById(R.id.urlText)
        val addButton: Button = view.findViewById(R.id.addButton)
        val saveButton: FloatingActionButton = view.findViewById(R.id.saveButton)
        val layoutManager = LinearLayoutManager(requireContext())
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        longitude = view.findViewById(R.id.longitude)
        latitude = view.findViewById(R.id.latitude)

        if(intent != null) {
            val urlString = intent?.getStringExtra(Intent.EXTRA_TEXT)
            lViewModel.apply {
                println(intent?.getStringExtra(Intent.EXTRA_TEXT))
                setCoordinatesUrl(urlString)
                coordinatesURL.observe(viewLifecycleOwner) { url ->
                    writeCoordinates(
                        runBlocking {
                            URLParser.extractCoordinates(url)
                        }
                    )
                    urlText.setText(url)
                }
            }
            intent = null
        }

        lViewModel.apply {
            getLocations().observe(viewLifecycleOwner) {
                adapter = LocationsAdapter(it)
                recyclerView.apply {
                    this.layoutManager = layoutManager
                    this.adapter = this@Locations.adapter
                }
            }
            onCoordinatesChanged()
        }

        addButton.setOnClickListener {
            val mName = locationName.text.toString()
            val mDescription = locationDesc.text.toString()
            val mLatitude = latitude?.text.toString()
            val mLongitude = longitude?.text.toString()
            if(mName.isNotEmpty()) {
                if (mDescription.isNotEmpty()) {
                    if (mLongitude.isNotEmpty()) {
                        if (mLatitude.isNotEmpty()) {
                             val coordinates = Coordinates(
                                    mLatitude.toDouble(),
                                    mLongitude.toDouble()
                                )
                            val location = Location(mName, mDescription, coordinates)
                            lViewModel.addLocation(location)
                            adapter?.notifyDataSetChanged()
                            locationDesc.text = null
                            urlText.text = null
                            longitude?.text = null
                            latitude?.text = null
                            if(intent != null)
                                intent = null
                            locationDesc.requestFocus()
                        } else coordinateError(getString(R.string._longitude))
                    } else coordinateError(getString(R.string._latitude))
                } else descriptionError(getString(R.string._location_desc))
            } else descriptionError(getString(R.string._location_name))
        }

        saveButton.setOnClickListener{
            with(lViewModel) {
                var locations: Locations? = null
                getLocations().observe(viewLifecycleOwner) { it1 ->
                    if (it1.isNotEmpty()) {
                        locations = Locations(it1)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.no_locations), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                if (locations != null) {
                    val path = JSONFile.getPath(requireContext())
                    if (path != null) {
                        JSONFile.createJsonFile(
                            path,
                            JSONParser.parseLocationsToJSON(locations!!)
                        )
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.location_saved), Toast.LENGTH_SHORT
                        ).show()
                    } else
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.select_folder),
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }

        urlText.addTextChangedListener(urlChangeListener)
    }

    private fun onCoordinatesChanged(){
        lViewModel.coordinates.observe(viewLifecycleOwner) {
            if(it.latitude != null && it.longitude != null) {
                latitude?.setText(it.latitude.toString())
                longitude?.setText(it.longitude.toString())
            }
        }
    }

    private fun coordinateError(missingValue: String){
        Toast.makeText(requireContext(), getString(R.string.coordinate_error, missingValue), Toast.LENGTH_SHORT).show()
    }

    private fun descriptionError(missingValue: String){
        Toast.makeText(requireContext(), getString(R.string.description_error, missingValue), Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "Manual Entry"
    }
}