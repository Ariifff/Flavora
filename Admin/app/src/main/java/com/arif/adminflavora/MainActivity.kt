package com.arif.adminflavora

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arif.adminflavora.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth= Firebase.auth
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.additemButton.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
        binding.allItem.setOnClickListener {
            val intent = Intent(this, AllitemActivity::class.java)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showdialog()
            }
        })

        binding.orderDispatch.setOnClickListener {
            val intent = Intent(this, OutForDeliveryActivity::class.java)
            startActivity(intent)
        }
        binding.adminprofile.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }
//        binding.newUser.setOnClickListener {
//            val intent = Intent(this, CreateUserActivity::class.java)
//            startActivity(intent)
//        }
        binding.pendingTextview.setOnClickListener {
            val intent = Intent(this, PendingOrderActivity::class.java)
            startActivity(intent)
        }
        binding.logoutButton.setOnClickListener {

            showdialogforlogout()
        }

        binding.reset.setOnClickListener {
            showdialogreset()
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    override fun onResume() {
        super.onResume()
        val database = Firebase.database.reference
        val adminUid = "76puAgxy8NTrC3auZBdUQtBN8cM2"
        database.child("admin").child(adminUid).child("pendingOrders")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.childrenCount.toInt()
                    binding.pendingCount.text = count.toString()

                }

                override fun onCancelled(error: DatabaseError) {
                    // Optional: handle error
                }
            })
        database.child("admin").child(adminUid).child("completedOrders")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val completedCount = snapshot.childrenCount.toInt()
                    binding.completed.text = completedCount.toString()

                    var totalEarning = 0.0

                    for (orderSnap in snapshot.children) {
                        val order = orderSnap.getValue(com.arif.adminflavora.model.PendingOrder::class.java)
                        order?.items?.forEach { item ->
                            totalEarning += item.foodPrice?.toDoubleOrNull() ?: 0.0
                        }
                    }

                    // Display
                    binding.totalEarning.text = "₹${totalEarning.toInt()}"

                }

                override fun onCancelled(error: DatabaseError) {
                    // Optional: handle error
                }
            })
    }

    private fun reset(){
        val database = Firebase.database.reference
        val adminUid = "76puAgxy8NTrC3auZBdUQtBN8cM2"
        val completedRef = database.child("admin").child(adminUid).child("completedOrders")

        // Remove all completed orders
        completedRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Completed orders reset successfully!", Toast.LENGTH_SHORT).show()
                // Optionally reset the UI text too:
                binding.completed.text = "0"
                binding.totalEarning.text = "₹0"
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to reset: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showdialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit")
        builder.setMessage("Are you sure you want to exit?")
        builder.setPositiveButton("Yes") { _, _ ->
             // Close the app
            finishAffinity()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog
        }
        builder.show()
    }

    private fun showdialogforlogout() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit")
        builder.setMessage("Are you sure you want to Logout?")
        builder.setPositiveButton("Yes") { _, _ ->
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog
        }
        builder.show()
    }
    private fun showdialogreset() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit")
        builder.setMessage("Are you sure you want to reset?")
        builder.setPositiveButton("Yes") { _, _ ->
            reset()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog
        }
        builder.show()
    }

}