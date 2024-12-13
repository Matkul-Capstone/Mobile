package com.example.speaktoo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.speaktoo.R
import com.example.speaktoo.data.level.Level

class LevelAdapter(
    context: Context,
    private val levelArrayList: ArrayList<Level>
) : ArrayAdapter<Level>(context, 0, levelArrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Use the provided convertView if available, otherwise inflate a new view
        val listItemView = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.card_level_item,
            parent,
            false
        )

        // Get the current item
        val levelModel = getItem(position)

        // Find views in the layout
        val levelTV = listItemView.findViewById<TextView>(R.id.level_text)
        val levelIV = listItemView.findViewById<ImageView>(R.id.level_image)

        // Safely populate views if the data exists
        levelModel?.let {
            levelTV.text = it.level_name // Fixed naming convention for clarity
            levelIV.setImageResource(it.imgid) // Fixed naming convention for clarity
        }

        return listItemView
    }
}
