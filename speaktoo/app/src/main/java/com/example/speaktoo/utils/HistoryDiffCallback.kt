package com.example.speaktoo.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.speaktoo.data.history.History

class HistoryDiffCallback : DiffUtil.ItemCallback<History>() {

    override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem.timestamp == newItem.timestamp // Assuming 'id' is unique for each history item
    }

    override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem == newItem
    }
}
