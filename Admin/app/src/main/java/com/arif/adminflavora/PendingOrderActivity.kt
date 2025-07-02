package com.arif.adminflavora

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.arif.adminflavora.adapter.PendingAdapter
import com.arif.adminflavora.databinding.ActivityPendingOrderBinding
import com.arif.adminflavora.model.PendingOrder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class PendingOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPendingOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        val database = Firebase.database.reference
        val adminUid = "76puAgxy8NTrC3auZBdUQtBN8cM2"

        if (adminUid != null) {
            database.child("admin").child(adminUid).child("pendingOrders")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val pendingOrdersList = mutableListOf<PendingOrder>()

                        for (orderSnap in snapshot.children) {
                            val order = orderSnap.getValue(PendingOrder::class.java)
                            order?.key = orderSnap.key  // save key to remove later
                            order?.let { pendingOrdersList.add(it) }
                        }

                        // Create adapter & set it
                        val adapter = PendingAdapter(pendingOrdersList) { order ->
                            // remove order when dispatch clicked
                            order.key?.let { key ->
                                database.child("admin").child(adminUid).child("pendingOrders").child(key)
                                    .removeValue()
                                    .addOnSuccessListener {
                                        val completedOrder = order.copy(status = "completed")
                                        database.child("admin").child(adminUid).child("completedOrders").child(key)
                                            .setValue(completedOrder)
                                            .addOnSuccessListener {
                                                Toast.makeText(this@PendingOrderActivity, "Order dispatched", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this@PendingOrderActivity, "Failed to save to completed", Toast.LENGTH_SHORT).show()
                                            }
                                        recreate() // refresh list
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@PendingOrderActivity, "Failed to remove order", Toast.LENGTH_SHORT).show()
                                    }
                            }

                        }

                        binding.pendingRecycler.layoutManager = LinearLayoutManager(this@PendingOrderActivity)
                        binding.pendingRecycler.adapter = adapter

                        // pendingOrdersList is your list of PendingOrder
                        var totalItems = 0
                        for (order in pendingOrdersList) {
                            totalItems += order.items?.size ?: 0
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@PendingOrderActivity, "Failed to load orders", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(this, "Admin not logged in", Toast.LENGTH_SHORT).show()
        }



        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
