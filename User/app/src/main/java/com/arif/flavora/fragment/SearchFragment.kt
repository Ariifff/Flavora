package com.arif.flavora.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.arif.flavora.R
import com.arif.flavora.adapter.MenuAdapter
import com.arif.flavora.databinding.FragmentSearchBinding
import com.arif.flavora.model.menuItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var menuItemList: MutableList<menuItems>
    private lateinit var filteredList: MutableList<menuItems>
    private lateinit var database: DatabaseReference
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)


        menuItemList = mutableListOf()
        filteredList = mutableListOf()
        adapter = MenuAdapter(filteredList, requireContext())


        // adapter= MenuAdapter(filteredMenuFoodName,filteredMenuItemPrice,filteredMenuImage,requireContext())
        binding.menurecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.menurecyclerview.adapter = adapter


        fetchfromfirebase()
        //setup for search view
        setupSearchView()

        return binding.root
    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItems(newText)
                return true
            }
        })

    }

    private fun filterMenuItems(query: String) {
        filteredList.clear()
        if (!query.isNullOrEmpty()) {
            val search = query.lowercase().trim()
            val results = menuItemList.filter {
                it.foodname?.lowercase()?.contains(search) == true
            }
            filteredList.addAll(results)
        } else {
            filteredList.addAll(menuItemList) // If search is empty, show all
        }
        adapter.notifyDataSetChanged()
    }


    private fun fetchfromfirebase() {
        showLoading()

        auth = Firebase.auth
        val adminid="76puAgxy8NTrC3auZBdUQtBN8cM2"
        database = Firebase.database.reference.child("admin").child(adminid).child("Menu")
        database.orderByChild("active").equalTo(true)
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItemList.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(menuItems::class.java)
                    item?.let { menuItemList.add(it) }
                }

                filteredList.clear()
                filteredList.addAll(menuItemList)
                adapter.notifyDataSetChanged()

                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoading()
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
