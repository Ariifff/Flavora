package com.arif.flavora.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.arif.flavora.R
import com.arif.flavora.adapter.NotificationAdapter
import com.arif.flavora.databinding.FragmentNotificationBinding


class NotificationFragment : BottomSheetFragment() {
    private lateinit var binding : FragmentNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentNotificationBinding.inflate(inflater,container,false)
        val notifications = listOf("Your order has been cancelled","Your order has been picked by driver","Your order has been placed succcessfully")
        val notificationimages = listOf(R.drawable.sademoji,R.drawable.truck,R.drawable.congrats)
        val adapter= NotificationAdapter(ArrayList(notifications),ArrayList(notificationimages))
        binding.notificationRecycler.adapter=adapter
        binding.notificationRecycler.layoutManager= LinearLayoutManager(requireContext())
        binding.notificationRecycler.isNestedScrollingEnabled=false
        return binding.root
    }

    companion object {
    }
}