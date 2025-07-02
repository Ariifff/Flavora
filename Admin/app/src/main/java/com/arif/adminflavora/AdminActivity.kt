package com.arif.adminflavora

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arif.adminflavora.databinding.ActivityAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AdminActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminBinding
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database= Firebase.database.reference
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.name.isEnabled=false
        binding.address.isEnabled=false
        binding.email.isEnabled=false
        binding.number.isEnabled=false

        val adminId = "76puAgxy8NTrC3auZBdUQtBN8cM2"


        database.child("admin").child(adminId)
            .addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("userName").getValue(String::class.java) ?: ""
                val email = snapshot.child("email").getValue(String::class.java) ?: ""
                val address = snapshot.child("address").getValue(String::class.java) ?: ""
                val number = snapshot.child("number").getValue(String::class.java) ?: ""


                binding.name.setText(name)
                binding.email.setText(email)
                binding.address.setText(address)
                binding.number.setText(number)


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })

        binding.edit.setOnClickListener {
            binding.name.isEnabled=true
            binding.address.isEnabled=true
            binding.email.isEnabled=true
            binding.number.isEnabled=true

            binding.name.requestFocus()
            binding.name.setSelection(binding.name.text.length)

        }

        binding.save.setOnClickListener {

            var nametext=binding.name.text
            var addressstext=binding.address.text
            var emailtext=binding.email.text
            var numbertext=binding.number.text
            if(nametext.isBlank() || addressstext.isBlank() || emailtext.isBlank() || numbertext.isBlank()){
                Toast.makeText(this,"Please fill all the details",Toast.LENGTH_LONG).show()
            }
            else{
                showLoading()
                val updates = mapOf(
                    "userName" to nametext.toString(),
                    "email" to emailtext.toString(),
                    "address" to addressstext.toString(),
                    "number" to numbertext.toString()
                )

                database.child("admin").child(adminId).updateChildren(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                hideLoading()
                Toast.makeText(this,"Edited Successfully",Toast.LENGTH_LONG).show()
                binding.name.isEnabled=false
                binding.address.isEnabled=false
                binding.email.isEnabled=false
                binding.number.isEnabled=false

            }
            finish()

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


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