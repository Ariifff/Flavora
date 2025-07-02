package com.arif.flavora

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arif.flavora.databinding.ActivityDetailsBinding
import com.arif.flavora.model.cartitems
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var foodName: String? = null
    private var foodPrice: String? = null
    private var foodDescription: String? = null
    private var foodImage: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        auth = Firebase.auth
        database = Firebase.database.reference
        foodName = intent.getStringExtra("menuname")
        foodPrice = intent.getStringExtra("menuprice")
        foodDescription = intent.getStringExtra("menudescription")
        foodImage = intent.getStringExtra("menuimage")

        with(binding) {
            foodnameText.text = foodName
            foodDesc.text = foodDescription
            val ImageUri = Uri.parse(foodImage)
            Glide.with(this@DetailsActivity).load(ImageUri).into(imageView4)
        }


        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.addtocart.setOnClickListener {
            addtocart()
        }


        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addtocart() {
        val database= FirebaseDatabase.getInstance().reference
        val userId=auth.currentUser?.uid?:""

        //create a cart item object
        val cartitem= cartitems(foodName.toString(),foodPrice.toString(),foodImage.toString(),1)

        //save data to cart item in firebase
        database.child("public").child(userId).child("cartitems").push().setValue(cartitem)
            .addOnSuccessListener {
                Toast.makeText(this,"Item added to cart",Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this,"Failed to add item to cart",Toast.LENGTH_SHORT).show()
            }


    }
}