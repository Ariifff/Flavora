package com.arif.flavora.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.arif.flavora.databinding.CartItemBinding
import com.arif.flavora.model.cartitems
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CartAdapter(
    private val cartItems: MutableList<cartitems>,
    private val requirecontext: Context
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val itemQuantities = IntArray(cartItems.size){1}
    var quantityChangeListener: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding= CartItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CartViewHolder(binding)

    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    inner class CartViewHolder (private val binding : CartItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val cartItem = cartItems[position]
            binding.apply {
                foodNameText.text=cartItem.foodName
                foodPriceText.text=cartItem.foodPrice
                quantityTextview.text=cartItem.foodquantity.toString()
                val Uri = Uri.parse(cartItem.foodImage)
                Glide.with(requirecontext).load(Uri).into(foodImage)


                fun increasequant(position: Int){
                    itemQuantities[position]++
                    cartItems[position].foodquantity++
                    quantityTextview.text=itemQuantities[position].toString()
                    saveQuantityToFirebase(cartItems[position])
                    quantityChangeListener?.invoke()
                }
                fun decreasequant(position: Int){
                    if(itemQuantities[position]>1) {
                        itemQuantities[position]--
                        cartItems[position].foodquantity--
                        quantityTextview.text = itemQuantities[position].toString()
                        saveQuantityToFirebase(cartItems[position])
                        quantityChangeListener?.invoke()
                    }

                }
                fun removeitem(position: Int){
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
                    val itemKey = cartItem.key ?: return

                    // Delete from Firebase
                    val dbRef = FirebaseDatabase.getInstance()
                        .getReference("public")
                        .child(userId)
                        .child("cartitems")
                        .child(itemKey)

                    dbRef.removeValue().addOnSuccessListener {
                        // Remove locally only after successful Firebase delete
                        cartItems.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, cartItems.size)

                        //recalculate the price
                        quantityChangeListener?.invoke()

                    }.addOnFailureListener {
                        Toast.makeText(requirecontext, "Failed to remove item", Toast.LENGTH_SHORT).show()
                    }

                }
                binding.buttonPlus.setOnClickListener {
                    increasequant(position)
                }
                binding.buttonMinus.setOnClickListener {
                    decreasequant(position)
                }
                binding.buttonDelete.setOnClickListener {
                        val itemPosition=adapterPosition
                        if(itemPosition!=RecyclerView.NO_POSITION){
                            showDeleteDialog(binding.root.context){
                                removeitem(itemPosition)
                            }
                        }

                }

            }
        }

    }
    fun showDeleteDialog(context: Context, onDeleteConfirmed: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item from your cart?")
            .setPositiveButton("Delete") { dialog, _ ->
                onDeleteConfirmed()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
    private fun saveQuantityToFirebase(item: cartitems) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val itemKey = item.key ?: return

        val dbRef = FirebaseDatabase.getInstance()
            .getReference("public")
            .child(userId)
            .child("cartitems")
            .child(itemKey)
            .child("foodquantity")

        dbRef.setValue(item.foodquantity)
    }


}