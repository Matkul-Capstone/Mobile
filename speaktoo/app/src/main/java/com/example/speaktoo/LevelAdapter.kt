package com.example.speaktoo

import com.example.speaktoo.LevelModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class LevelAdapter(
    context: Context,
    private val levelModelArrayList: ArrayList<LevelModel>
) : ArrayAdapter<LevelModel>(context, 0, levelModelArrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Use the provided convertView if available, otherwise inflate a new view
        val listItemView = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.card_menu_item,
            parent,
            false
        )

        // Get the current item
        val levelModel = getItem(position)

        // Find views in the layout
        val levelTV = listItemView.findViewById<TextView>(R.id.level_image)
        val levelIV = listItemView.findViewById<ImageView>(R.id.level_text)

        // Safely populate views if the data exists
        levelModel?.let {
            levelTV.setText(it.getLevelName()) // Fixed naming convention for clarity
            levelIV.setImageResource(it.getImgId()) // Fixed naming convention for clarity
        }

        return listItemView
    }
}
