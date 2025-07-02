package com.arif.flavora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arif.flavora.databinding.NotificationItemBinding

class NotificationAdapter(
    private var notificationMessages: ArrayList<String>,
    private var notificationImages: ArrayList<Int>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return notificationMessages.size
    }

    inner class NotificationViewHolder(private val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.notiText.text = notificationMessages[position]
            binding.notiImage.setImageResource(notificationImages[position])
        }
    }
}
