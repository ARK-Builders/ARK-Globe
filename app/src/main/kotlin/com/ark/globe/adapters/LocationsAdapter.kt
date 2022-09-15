package com.ark.globe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ark.globe.R
import com.ark.globe.coordinates.Location
import com.ark.globe.databinding.CoordinateViewBinding

class LocationsAdapter(private val locations: List<Location>):
    RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.coordinate_view, parent, false)
        return LocationsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(
        holder: LocationsViewHolder,
        position: Int
    ) {
        val coordinates = "${locations[position].coordinates.latitude}, ${locations[position].coordinates.longitude}"
        holder.apply{
            locationName.text = locations[position].name
            locationDesc.text = locations[position].description
            this.coordinates.text = coordinates
        }
    }

    class LocationsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val binding by viewBinding{
            CoordinateViewBinding.bind(itemView)
        }
        val locationName  = binding.locationName
        val locationDesc = binding.locationDesc
        val coordinates = binding.coordinates
    }
}