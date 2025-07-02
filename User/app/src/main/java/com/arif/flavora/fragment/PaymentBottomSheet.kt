package com.arif.flavora.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arif.flavora.MainActivity
import com.arif.flavora.R
import com.arif.flavora.databinding.FragmentPaymentBottomSheetBinding

class PaymentBottomSheet : BottomSheetFragment() {

    private lateinit var binding: FragmentPaymentBottomSheetBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentBottomSheetBinding.inflate(inflater, container, false)

        binding.buttonHome.setOnClickListener {

            val intent= Intent(requireContext(),MainActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    companion object {
    }
}