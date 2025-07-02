package com.arif.adminflavora.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.arif.adminflavora.databinding.MenuItemBinding
import com.arif.adminflavora.model.AllMenu
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    private val database: DatabaseReference
) : RecyclerView.Adapter<MenuItemAdapter.ViewHolder>() {

    private val itemQuantities = IntArray(menuList.size) { 1 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    inner class ViewHolder(private val binding: MenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val menuItem = menuList[position]
            val quantity = itemQuantities[position]

            binding.apply {
                foodNameText.text = menuItem.foodname
                foodPriceText.text = " ₹${menuItem.foodprice}"
                quantityTextview.text = quantity.toString()

                val uri = Uri.parse(menuItem.foodimage)
                Glide.with(context).load(uri).into(foodImage)

                activeSwitch.isChecked = menuItem.active
                activeSwitch.setOnCheckedChangeListener { _, isChecked ->
                    menuItem.key?.let { key ->
                        database.child(key).child("active").setValue(isChecked)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Item updated", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to update item", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                buttonPlus.setOnClickListener {
                    if (itemQuantities[position] < 10) {
                        itemQuantities[position]++
                        quantityTextview.text = itemQuantities[position].toString()
                    }
                }

                buttonMinus.setOnClickListener {
                    if (itemQuantities[position] > 1) {
                        itemQuantities[position]--
                        quantityTextview.text = itemQuantities[position].toString()
                    }
                }

                buttonDelete.setOnClickListener {
                    showDeleteConfirmationDialog(position)
                }
            }
        }
    }

    private fun deleteQuantity(position: Int) {
        val itemToDelete = menuList[position]
        val key = itemToDelete.key  // this was set when fetching items

        if (key != null) {
            database.child(key)
                .removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                    menuList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, menuList.size)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Item key not found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showDeleteConfirmationDialog(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this item?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            deleteQuantity(position)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }


}
