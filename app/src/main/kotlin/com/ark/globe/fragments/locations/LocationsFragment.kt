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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ark.globe.R
import com.ark.globe.adapters.LocationsAdapter
import com.ark.globe.coordinates.Coordinates
import com.ark.globe.coordinates.Location
import com.ark.globe.coordinates.URLParser
import com.ark.globe.databinding.LocationsFragmentBinding
import com.ark.globe.jsonprocess.JSONFile
import kotlinx.coroutines.runBlocking

class LocationsFragment: Fragment() {

    private val activity: AppCompatActivity by lazy{
        requireActivity() as AppCompatActivity
    }
    private val lViewModel: LocationsViewModel by activityViewModels()
    private var adapter: LocationsAdapter? = null
    private var intent: Intent? = null
    private var longitude: EditText? = null
    private var latitude: EditText? = null

    private lateinit var binding: LocationsFragmentBinding

    private val urlChangeListener = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != null && s.isNotEmpty()) {

                val coordinates = runBlocking{
                    URLParser.extractCoordinates(s.toString())
                }

                lViewModel.writeCoordinates(coordinates)

                onCoordinatesChanged()
            }
        }

        override fun afterTextChanged(s: Editable?) = Unit

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    }

    fun sendIntent(intent: Intent){
        this.intent = intent
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity.title = getString(R.string.app_name)
        binding = LocationsFragmentBinding.inflate(layoutInflater)
        return inflater.inflate(R.layout.locations_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val locationName: EditText = view.findViewById(R.id.locationName)
        val locationDesc: EditText = view.findViewById(R.id.locationDesc)
        val urlText: EditText = view.findViewById(R.id.urlText)
        val addButton: Button = view.findViewById(R.id.addButton)
        val layoutManager = LinearLayoutManager(requireContext())
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        longitude = view.findViewById(R.id.longitude)
        latitude = view.findViewById(R.id.latitude)

        urlText.addTextChangedListener(urlChangeListener)

        if(intent != null) {
            val data = intent?.getStringExtra(Intent.EXTRA_TEXT)
            urlText.setText(URLParser.getValidURL(data))
        }

        lViewModel.apply {
            addLocations(JSONFile.readJsonLocations(requireContext()))
            locations.observe(viewLifecycleOwner) {
                adapter = LocationsAdapter(it)
                recyclerView.apply {
                    this.layoutManager = layoutManager
                    this.adapter = this@LocationsFragment.adapter
                }
            }
        }

        addButton.setOnClickListener {
            val mName = locationName.text.toString()
            val mDescription = locationDesc.text.toString()
            val mLatitude = latitude?.text.toString()
            val mLongitude = longitude?.text.toString()
            if(mName.isNotEmpty()) {
                if (mLongitude.isNotEmpty()) {
                    if (mLatitude.isNotEmpty()) {
                        val coordinates = Coordinates(
                            mLatitude.toDouble(),
                            mLongitude.toDouble()
                        )
                        val location = Location(mName, mDescription, coordinates)

                        JSONFile.saveLocation(requireContext(), location)

                        lViewModel.addLocation(location)

                        adapter?.notifyDataSetChanged()
                        lViewModel.writeCoordinates(null)
                        locationName.text = null
                        locationDesc.text = null
                        urlText.text = null
                        longitude?.text = null
                        latitude?.text = null
                        if(intent != null)
                            intent = null
                        locationName.requestFocus()
                    } else coordinateError(getString(R.string._longitude))
                } else coordinateError(getString(R.string._latitude))
            } else descriptionError(getString(R.string._location_name))
        }

        /*saveButton.setOnClickListener{
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
        }*/
    }

    private fun onCoordinatesChanged(){
        lViewModel.coordinates.observe(viewLifecycleOwner) {
            if (it != null) {
                latitude?.setText(it.latitude.toString())
                longitude?.setText(it.longitude.toString())
            }
            else{
                latitude?.text = null
                longitude?.text = null
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
        const val TAG = "Locations Fragment"
    }
}