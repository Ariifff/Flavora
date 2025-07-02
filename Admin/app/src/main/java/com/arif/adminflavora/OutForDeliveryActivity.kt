package com.arif.adminflavora

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.arif.adminflavora.adapter.DeliveryAdapter
import com.arif.adminflavora.databinding.ActivityOutForDeliveryBinding

class OutForDeliveryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOutForDeliveryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOutForDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val customerNames= arrayListOf(
            "Arif",
            "Harsh(Kaalu)",
            "Al Amin(Chhotu)",
            "Kaif(badmosh)",
            "Faraz(Ganjaa)",
            "Amir(Piggy)"
        )
        val moneyStatus= arrayListOf(
            "Received",
            "Not Received",
            "Pending",
            "Pending",
            "Received",
            "Not Received"
        )

        val adapter=DeliveryAdapter(customerNames,moneyStatus)
        binding.deliveryRecycler.adapter=adapter
        binding.deliveryRecycler.layoutManager= LinearLayoutManager(this)

        binding.backButton.setOnClickListener {
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}