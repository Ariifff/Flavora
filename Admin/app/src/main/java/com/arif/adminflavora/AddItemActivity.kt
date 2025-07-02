package com.arif.adminflavora

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arif.adminflavora.databinding.ActivityAddItemBinding
import com.arif.adminflavora.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AddItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemBinding

    //fooditems details
    private lateinit var foodname: String
    private lateinit var foodprice: String
    private var foodimageuri: Uri? = null
    private lateinit var fooddiscription: String

    //firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=Firebase.auth
        database=Firebase.database.reference

        binding.addItemButton.setOnClickListener {
            foodname = binding.editText.text.toString()
            foodprice = binding.editText2.text.toString()
            fooddiscription = binding.textDesc.text.toString()

            if (!(foodname.isBlank() || foodprice.isBlank() || fooddiscription.isBlank())) {
                // Show loading before starting to fetch data
                showLoading()

                uploadData()


            } else {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }


        binding.selectImage.setOnClickListener {
            pickimage.launch("image/*")

        }
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun uploadData() {
        val adminid=auth.currentUser!!.uid
        // get referece to the menu
        val MenuRef = database.child("admin").child(adminid).child("Menu")

        //generate unique key for the menu
        val menuId = MenuRef.push().key!!

        if (foodimageuri != null) {
            val storageref = FirebaseStorage.getInstance().reference
            val imageRef = storageref.child("foodimages/${menuId}.jpg")
            val uploadTask = imageRef.putFile(foodimageuri!!)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val foodData = AllMenu(
                        foodname = foodname,
                        foodprice = foodprice,
                        foodimage = downloadUrl.toString(),
                        fooddiscription = fooddiscription

                    )

                        MenuRef.child(menuId).setValue(foodData).addOnCompleteListener {
                            // Hide loading once data is loaded and adapter is set
                            hideLoading()
                            Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                            .addOnFailureListener {
                                // Hide loading once data is loaded and adapter is set
                                hideLoading()
                                Toast.makeText(this, "Item Not Added", Toast.LENGTH_SHORT).show()
                            }
                }

            }.addOnFailureListener {
                Toast.makeText(this, "Image Not Uploaded", Toast.LENGTH_SHORT).show()
            }

        }else {
            Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
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
    val pickimage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri != null) {
                binding.selectedImage.setImageURI(uri)
                foodimageuri = uri
            }

        }
}