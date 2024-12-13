package com.example.speaktoo.ui.adapter

import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.speaktoo.R
import com.example.speaktoo.data.history.History
import com.example.speaktoo.utils.HistoryDiffCallback
import java.util.Locale

class HistoryAdapter : ListAdapter<History, HistoryAdapter.HistoryViewHolder>(
    HistoryDiffCallback()
) {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val level = view.findViewById<TextView>(R.id.level_text)
        val timestamp = view.findViewById<TextView>(R.id.timestamp_text)
        val score = view.findViewById<TextView>(R.id.score_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = getItem(position)
        holder.level.text = history.level
        holder.timestamp.text = history.timestamp
        holder.score.text = String.format(locale = Locale.getDefault(), "%d", history.score)
    }
}
