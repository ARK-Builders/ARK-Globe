package com.ark.globe.fragments.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.ark.globe.R
import com.ark.globe.preferences.GlobePreferences
import java.io.File
import java.nio.file.Path

class PathPreference(context: Context, attrs: AttributeSet): Preference(context, attrs) {
    private var path: TextView? = null
    private var title: TextView? = null

    fun setPath(path: String?){
        if(path != null)
            this.path?.text = path
    }

    fun setTitle(title: String?){
        if(title != null)
            this.title?.text = title
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        title = holder.findViewById(R.id.title) as TextView
        path = holder.findViewById(R.id.pathValue) as TextView
        setPath(GlobePreferences.getInstance(context).getPath())
    }
}
