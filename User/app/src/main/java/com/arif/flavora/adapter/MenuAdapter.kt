package com.arif.flavora.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arif.flavora.DetailsActivity
import com.arif.flavora.databinding.MenuItemBinding
import com.arif.flavora.model.menuItems
import com.bumptech.glide.Glide

class MenuAdapter(
    private val menuItems : List<menuItems>,
    private val requireContext : Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(MenuItemBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return menuItems.size

    }
    inner class MenuViewHolder (private val binding : MenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position=adapterPosition
                if(position!=RecyclerView.NO_POSITION){
                    OpenDetailsActivity(position)
                }
            }
        }

        private fun OpenDetailsActivity(position: Int) {
            val menuitem = menuItems[position]
            //a intent to open details activity
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("menuname", menuitem.foodname)
                putExtra("menuprice", menuitem.foodprice)
                putExtra("menuimage", menuitem.foodimage)
                putExtra("menudescription", menuitem.fooddiscription)
            }
            //start details activity
            requireContext.startActivity(intent)
            
        }
        //set data to the recycler view

        fun bind(position: Int){
            val menuitem=menuItems[position]
            binding.apply {

                menuname.text=menuitem.foodname
                menuprice.text="₹${menuitem.foodprice}"
                val Uri = Uri.parse(menuitem.foodimage)
                Glide.with(requireContext).load(Uri).into(menuimage)


            }


        }

    }


}

