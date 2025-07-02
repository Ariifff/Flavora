package com.arif.flavora.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.arif.flavora.adapter.MenuAdapter
import com.arif.flavora.databinding.FragmentBottomSheetBinding
import com.arif.flavora.model.menuItems
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


open class BottomSheetFragment : BottomSheetDialogFragment(){
    private lateinit var binding: FragmentBottomSheetBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth  : FirebaseAuth
    private lateinit var menuItem : MutableList<menuItems>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentBottomSheetBinding.inflate(inflater,container,false)
        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        retreivedata()


        return binding.root
    }

    private fun retreivedata() {
        auth= Firebase.auth
        val adminid="76puAgxy8NTrC3auZBdUQtBN8cM2"
        database=Firebase.database.reference
        val foodref : DatabaseReference = database.child("admin").child(adminid).child("Menu")
        menuItem= mutableListOf()
        foodref.orderByChild("active").equalTo(true)
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodsnapshot in snapshot.children){
                    val item=foodsnapshot.getValue(menuItems::class.java)
                    item?.let { menuItem.add(it) }

                }
                //once data received save adapter
                setAdapter()

            }

            private fun setAdapter() {
                val adapter= MenuAdapter(menuItem,requireContext())
                binding.menurecyclerview.adapter=adapter
                binding.menurecyclerview.layoutManager=LinearLayoutManager(requireContext())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    companion object {

    }
}