package com.arif.flavora.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arif.flavora.DetailsActivity
import com.arif.flavora.databinding.PopularItemBinding
import com.arif.flavora.model.menuItems
import com.bumptech.glide.Glide

class PopularAdapter(
    private val items: MutableList<menuItems>,
    private val context: Context
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val binding = PopularItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PopularViewHolder(binding)
    }
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PopularAdapter.PopularViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java).apply {
                putExtra("menuname", item.foodname)
                putExtra("menuimage", item.foodimage)
                putExtra("menuprice", item.foodprice)
                putExtra("menudescription", item.fooddiscription)
            }
            context.startActivity(intent)
        }

    }
    inner class PopularViewHolder(val binding: PopularItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: menuItems) {
            binding.foodNameText.text = item.foodname
            binding.foodPriceText.text = "₹${item.foodprice}"
            // Load image using Glide
            Glide.with(binding.foodImage.context)
                .load(item.foodimage)  // assuming this is a String URL
                .into(binding.foodImage)
        }

    }


}