package com.arif.flavora.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.arif.flavora.PayOutActivity
import com.arif.flavora.adapter.CartAdapter
import com.arif.flavora.databinding.FragmentCartBinding
import com.arif.flavora.model.cartitems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var database : DatabaseReference
    private lateinit var cartItems : MutableList<cartitems>
    private lateinit var auth : FirebaseAuth
    private var totalPrice: Double = 0.0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCartBinding.inflate(inflater,container,false)

        retreivedata()

        return binding.root
    }



    private fun retreivedata() {
        database= Firebase.database.reference
        auth= Firebase.auth
        val userId=auth.currentUser?.uid?:""
        val cartref : DatabaseReference = database.child("public").child(userId).child("cartitems")
        cartItems= mutableListOf()
        cartref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodsnapshot in snapshot.children) {
                    val item = foodsnapshot.getValue(cartitems::class.java)
                    item?.key = foodsnapshot.key // 🔑 Save the push key
                    item?.let { cartItems.add(it) }

                }


                // Proceed button
                binding.buttonProceed.setOnClickListener {
                    val intent = Intent(requireContext(), PayOutActivity::class.java)
                    intent.putExtra("total_price", totalPrice.toString())
                    startActivity(intent)
                }

                //once data received save adapter
                setAdapter()

                updateTotalAmount()

            }

            private fun setAdapter() {
                val adapter= CartAdapter(cartItems,requireContext())

                adapter.quantityChangeListener = {
                    updateTotalAmount()
                }
                binding.cartRecyclerView.adapter=adapter
                binding.cartRecyclerView.layoutManager=LinearLayoutManager(requireContext())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun updateTotalAmount() {
        totalPrice = cartItems.sumOf { item ->
            val cleanedPrice = item.foodPrice
                ?.replace("₹", "")
                ?.replace(",", "")
                ?.trim()
                ?.toDoubleOrNull() ?: 0.0
            cleanedPrice * item.foodquantity
        }

    }

}