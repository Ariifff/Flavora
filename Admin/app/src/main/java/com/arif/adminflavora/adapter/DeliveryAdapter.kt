package com.arif.adminflavora.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arif.adminflavora.databinding.ItemDeliveryBinding

class DeliveryAdapter(private val customerNames: ArrayList<String>,private val moneyStatus: ArrayList<String>): RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding=ItemDeliveryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DeliveryViewHolder(binding)
    }


    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(position)
    }
    override fun getItemCount(): Int {
        return customerNames.size
    }
    inner class DeliveryViewHolder (private val binding : ItemDeliveryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.apply {
                custName.text=customerNames[position]
                statusMoney.text=moneyStatus[position %moneyStatus.size]

                val colorMap= mapOf(
                    "Not Received" to Color.RED,
                    "Received" to Color.GREEN,
                    "Pending" to Color.YELLOW
                )
                statusMoney.setTextColor(colorMap[moneyStatus[position]]?: Color.BLACK)
                deliveryStatus.backgroundTintList= ColorStateList.valueOf(colorMap[moneyStatus[position]]?: Color.BLACK)

            }
        }

    }
}