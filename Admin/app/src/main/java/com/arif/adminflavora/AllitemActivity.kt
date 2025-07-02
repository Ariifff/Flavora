package com.arif.adminflavora

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.arif.adminflavora.adapter.MenuItemAdapter
import com.arif.adminflavora.databinding.ActivityAllitemBinding
import com.arif.adminflavora.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class AllitemActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var menuItems: ArrayList<AllMenu> = ArrayList()
    private lateinit var binding: ActivityAllitemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllitemBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backButton.setOnClickListener { finish() }

        auth = Firebase.auth

        // Use your fixed admin UID directly
        val adminId = "76puAgxy8NTrC3auZBdUQtBN8cM2"
        databaseReference = FirebaseDatabase.getInstance()
            .reference.child("admin").child(adminId).child("Menu")

        // Show loading before starting to fetch data
        showLoading()

        retrieveMenuItems()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun retrieveMenuItems() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(AllMenu::class.java)
                    if (menuItem != null && menuItem.foodname != null) {
                        menuItem.key = foodSnapshot.key // save key for update
                        menuItems.add(menuItem)
                    }
                }
                setAdapter()

                // Hide loading once data is loaded and adapter is set
                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("AllitemActivity", "Database error: ${error.message}")
                // Hide loading once data is loaded and adapter is set
                hideLoading()
            }
        })
    }

    private fun setAdapter() {
        val adapter = MenuItemAdapter(this, menuItems, databaseReference)
        binding.menuRecycler.layoutManager = LinearLayoutManager(this)
        binding.menuRecycler.adapter = adapter
    }

    private fun showLoading() {
        binding.loadingBackground.visibility = View.VISIBLE
        binding.loadingAnimation.visibility = View.VISIBLE
        binding.loadingAnimation.playAnimation()
    }

    private fun hideLoading() {
        binding.loadingAnimation.cancelAnimation()
        binding.loadingAnimation.visibility = View.GONE
        binding.loadingBackground.visibility = View.GONE
    }


}
