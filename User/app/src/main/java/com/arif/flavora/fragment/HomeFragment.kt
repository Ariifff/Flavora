package com.arif.flavora.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.arif.flavora.R
import com.arif.flavora.adapter.PopularAdapter
import com.arif.flavora.databinding.FragmentHomeBinding
import com.arif.flavora.model.menuItems
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private lateinit var allItems: MutableList<menuItems>
    private lateinit var popularItems: MutableList<menuItems>
    private lateinit var adapter: PopularAdapter
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view=binding.root
        binding.viewmenuText.setOnClickListener {
            val bottomSheetDialog=BottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager,"Test")
        }
        allItems = mutableListOf()
        popularItems = mutableListOf()
        adapter = PopularAdapter(popularItems, requireContext())
        binding.PopularRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.PopularRecyclerView.adapter=adapter
        fetchMenuItems()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imagelist=ArrayList<SlideModel>()
        imagelist.add(SlideModel(R.drawable.banner1,ScaleTypes.FIT))
        imagelist.add(SlideModel(R.drawable.banner2,ScaleTypes.FIT))
        imagelist.add(SlideModel(R.drawable.banner3,ScaleTypes.FIT))

        val imageslider=binding.imageSlider
        imageslider.setImageList(imagelist,ScaleTypes.FIT)


    }
    private fun fetchMenuItems() {
        auth=Firebase.auth
        val adminid="76puAgxy8NTrC3auZBdUQtBN8cM2"
        showLoading()

        database = Firebase.database.reference.child("admin").child(adminid).child("Menu")
        database.orderByChild("active").equalTo(true)
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allItems.clear()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(menuItems::class.java)
                    item?.let { allItems.add(it) }
                }

                // Now randomly pick 4 items
                val randomItems = allItems.shuffled().take(5)
                popularItems.clear()
                popularItems.addAll(randomItems)
                hideLoading()
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error here if needed

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