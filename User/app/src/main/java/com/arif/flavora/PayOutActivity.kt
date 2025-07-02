package com.arif.flavora

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arif.flavora.databinding.ActivityPayOutBinding
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PayOutActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var binding: ActivityPayOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        // Payment dropdown
        val dropdown = findViewById<MaterialAutoCompleteTextView>(R.id.paymentDropdown)
        val options = arrayOf("Cash On Delivery", "UPI")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
        dropdown.setAdapter(adapter)
        dropdown.setOnClickListener { dropdown.showDropDown() }

        binding.backButton.setOnClickListener { finish() }

        // Place order
        binding.orderButton.setOnClickListener {
            val deliveryName = binding.name.text.toString()
            val deliveryAddress = binding.address.text.toString()
            val totalPrice = intent.getStringExtra("total_price")?.toDoubleOrNull() ?: 0.0

            if (deliveryName.isBlank() || deliveryAddress.isBlank()) {
                Toast.makeText(this, "Please fill delivery details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            placeOrder(deliveryName, deliveryAddress, totalPrice)
        }

        val totalPrice = intent.getStringExtra("total_price") ?: "0.00"
        binding.totalAmount.text = "₹$totalPrice"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun placeOrder(deliveryName: String, deliveryAddress: String, totalPrice: Double) {
        val userId = auth.currentUser?.uid ?: return
        val cartRef = database.child("public").child(userId).child("cartitems")
        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemsList = mutableListOf<Map<String, Any?>>()

                for (itemSnap in snapshot.children) {
                    val item = itemSnap.getValue(com.arif.flavora.model.cartitems::class.java)
                    if (item != null) {
                        val map = mapOf(
                            "foodName" to item.foodName,
                            "foodPrice" to item.foodPrice,
                            "foodImage" to item.foodImage,
                            "foodquantity" to item.foodquantity
                        )
                        itemsList.add(map)
                    }
                }

                val orderDataForUserHistory = mapOf(
                    "totalPrice" to totalPrice,
                    "timestamp" to System.currentTimeMillis(),
                    "deliveryName" to deliveryName,
                    "deliveryAddress" to deliveryAddress,
                    "items" to itemsList
                )

                val orderDataForAdmin = mapOf(
                    "userId" to userId,
                    "userName" to deliveryName,
                    "totalPrice" to totalPrice,
                    "timestamp" to System.currentTimeMillis(),
                    "deliveryAddress" to deliveryAddress,
                    "items" to itemsList
                )

                // Save to user history
                database.child("public").child(userId).child("history").push()
                    .setValue(orderDataForUserHistory)
                    .addOnSuccessListener {
                        // Save to admin pending orders
                        database.child("admin").child("76puAgxy8NTrC3auZBdUQtBN8cM2").child("pendingOrders").push()
                            .setValue(orderDataForAdmin)
                            .addOnSuccessListener {
                                // Remove cart items
                                cartRef.removeValue()
                                Toast.makeText(this@PayOutActivity, "Order placed successfully!", Toast.LENGTH_SHORT).show()

                                // Navigate home
                                val intent = Intent(this@PayOutActivity, MainActivity::class.java)
                                intent.putExtra("open_home", true)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@PayOutActivity, "Failed to save order for admin", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@PayOutActivity, "Failed to save order history", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PayOutActivity, "Failed to read cart items", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
