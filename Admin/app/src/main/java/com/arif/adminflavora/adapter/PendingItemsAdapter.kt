package com.arif.adminflavora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arif.adminflavora.databinding.ItemPendingFoodBinding
import com.arif.adminflavora.model.OrderItem
import com.arif.adminflavora.model.PendingOrder
import com.bumptech.glide.Glide

class PendingItemsAdapter(private val items: List<OrderItem>) :
    RecyclerView.Adapter<PendingItemsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemPendingFoodBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemPendingFoodBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.binding.foodQuantity.text = "${item.foodquantity}"
        Glide.with(holder.binding.foodImage.context)
            .load(item.foodImage)
            .placeholder(com.arif.adminflavora.R.drawable.ic_placeholder)
            .into(holder.binding.foodImage)
    }

    override fun getItemCount() = items.size
}
