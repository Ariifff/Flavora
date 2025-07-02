package com.arif.flavora.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.arif.flavora.adapter.CartAdapter
import com.arif.flavora.adapter.History_Adapter
import com.arif.flavora.databinding.FragmentHistoryBinding
import com.arif.flavora.model.historyitems
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: History_Adapter
    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var historyItems :  MutableList<historyitems>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHistoryBinding.inflate(inflater,container,false)

        auth = Firebase.auth
        database=Firebase.database.reference

        showLoading()

        retreivedata()
        return binding.root
    }

    private fun retreivedata() {
        val userId = auth.currentUser?.uid ?: return
        val hisref: DatabaseReference = database.child("public").child(userId).child("history")
        historyItems = mutableListOf()

        hisref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val itemsSnapshot = orderSnapshot.child("items")
                    for (itemSnap in itemsSnapshot.children) {
                        val name = itemSnap.child("foodName").getValue(String::class.java)
                        val price = itemSnap.child("foodPrice").getValue(String::class.java)
                        val image = itemSnap.child("foodImage").getValue(String::class.java)
                        val historyItem = historyitems(name, price, image)
                        historyItems.add(historyItem)
                    }
                }

                //reverse to get recent first
                historyItems.reverse()
                // ✅ Set recent order (if list is not empty)
                if (historyItems.isNotEmpty()) {
                    val recentItem = historyItems[0]
                    binding.recentName.text = recentItem.historyName
                    binding.recentPrice.text = "₹${recentItem.historyPrice}"

                    // Load image using Glide or similar
                    Glide.with(requireContext())
                        .load(recentItem.historyImage)
                        .into(binding.recentImage)
                }

                setAdapter()

                hideLoading()
            }

            private fun setAdapter() {
                val previousItems = if (historyItems.size > 1) {
                    historyItems.subList(1, historyItems.size)
                } else {
                    emptyList()
                }

                // limit to 5 most recent
                val limitedItems = if (historyItems.size > 5) {
                    historyItems.take(5).toMutableList()
                } else {
                    historyItems
                }
                val adapter = History_Adapter(limitedItems, requireContext())
                binding.historyrecycler.adapter = adapter
                binding.historyrecycler.layoutManager = LinearLayoutManager(requireContext())
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
            }
        })
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