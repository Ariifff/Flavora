package com.arif.adminflavora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arif.adminflavora.databinding.PendingItemsBinding
import com.arif.adminflavora.model.PendingOrder

class PendingAdapter(
    private val pendingOrders: List<PendingOrder>,
    private val onDispatchClick: (PendingOrder) -> Unit
) : RecyclerView.Adapter<PendingAdapter.PendingViewHolder>() {

    inner class PendingViewHolder(val binding: PendingItemsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingViewHolder {
        val binding = PendingItemsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return PendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingViewHolder, position: Int) {
        val order = pendingOrders[position]

        holder.binding.cutomerName.text = order.userName ?: "Unknown User"

        // Nested RecyclerView
        holder.binding.nestedItemsRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = order.items?.let { PendingItemsAdapter(it) }
        }

        holder.binding.orderAccept.setOnClickListener {
            holder.binding.orderAccept.text = "Dispatch"
            Toast.makeText(holder.itemView.context, "Order Accepted", Toast.LENGTH_SHORT).show()
            holder.binding.orderAccept.setOnClickListener {
                onDispatchClick(order)
            }
        }
    }

    override fun getItemCount() = pendingOrders.size
}
