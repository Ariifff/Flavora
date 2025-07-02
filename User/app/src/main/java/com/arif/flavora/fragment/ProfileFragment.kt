package com.arif.flavora.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.arif.flavora.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var database : DatabaseReference
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentProfileBinding.inflate(inflater,container,false)

        auth= Firebase.auth
        val userId=auth.currentUser?.uid?:""
        database= Firebase.database.reference

        binding.name.isEnabled=false
        binding.address.isEnabled=false
        binding.email.isEnabled=false
        binding.number.isEnabled=false

        database.child("public").child(userId).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java) ?: ""
                val email = snapshot.child("email").getValue(String::class.java) ?: ""
                val address = snapshot.child("address").getValue(String::class.java) ?: ""
                val number = snapshot.child("number").getValue(String::class.java) ?: ""

                binding.name.setText(name)
                binding.email.setText(email)
                binding.address.setText(address)
                binding.number.setText(number)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(),"Please fill all the details",Toast.LENGTH_LONG).show()
            }
            else{
                val updates = mapOf(
                    "name" to nametext.toString(),
                    "email" to emailtext.toString(),
                    "address" to addressstext.toString(),
                    "number" to numbertext.toString()
                )

                database.child("public").child(userId).updateChildren(updates)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }

                Toast.makeText(requireContext(),"Edited Successfully",Toast.LENGTH_LONG).show()
                binding.name.isEnabled=false
                binding.address.isEnabled=false
                binding.email.isEnabled=false
                binding.number.isEnabled=false
            }

        }
        binding.logOut.setOnClickListener {
            logoutdialog()


        }
        return binding.root
    }

    private fun logoutdialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { dialog, _ ->
                auth.signOut()
                Toast.makeText(requireContext(),"Logged Out",Toast.LENGTH_LONG).show()
                dialog.dismiss()

                // Go to LoginActivity and finish current Activity (optional)
                val intent = Intent(requireContext(), com.arif.flavora.LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()


    }

}