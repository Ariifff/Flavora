package com.arif.flavora.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arif.flavora.databinding.HistoryItemBinding
import com.arif.flavora.model.historyitems
import com.bumptech.glide.Glide

class History_Adapter (
    private val historyItems :  MutableList<historyitems>,
    private val requireContext : Context

)
    :RecyclerView.Adapter<History_Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): History_Adapter.ViewHolder {
        val binding= HistoryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        holder.bind(position)
    }


    override fun getItemCount(): Int {
        return historyItems.size
    }

    inner class ViewHolder (val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            val historyItem = historyItems[position]
            binding.apply {
                historyName.text = historyItem.historyName
                historyPrice.text = "₹${historyItem.historyPrice}"
                val imageUrl = historyItem.historyImage
                if (!imageUrl.isNullOrEmpty()) {
                    val uri = Uri.parse(imageUrl)
                    Glide.with(requireContext).load(uri).into(historyImage)
                }

            }

        }

    }
}