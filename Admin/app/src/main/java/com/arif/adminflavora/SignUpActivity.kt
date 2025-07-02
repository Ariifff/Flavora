package com.arif.adminflavora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arif.adminflavora.databinding.ActivitySignUpBinding
import com.arif.adminflavora.model.userModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth

    private lateinit var email : String
    private lateinit var password : String
    private lateinit var userName : String
    private lateinit var nameOfrestaurant : String
    private lateinit var database : DatabaseReference

    private lateinit var binding : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= Firebase.auth
        database=Firebase.database.reference

        binding.buttoncreate.setOnClickListener {
            userName=binding.ownerName.text.toString().trim()
            nameOfrestaurant=binding.restaurantName.text.toString().trim()
            email=binding.userEmail.text.toString().trim()
            password=binding.userPassword.text.toString().trim()

            if(userName.isBlank() || nameOfrestaurant.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please fill all details mentioned above",Toast.LENGTH_LONG).show()

            }
            else{
                createaccount(email,password)
            }
        }


        binding.textHaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val location= arrayOf("Delhi","Mumbai","Kolkata","Chennai","Bangalore","Hyderabad","Varanasi","Ghazipur")
        val adapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,location)
        binding.listoflocation.setAdapter(adapter)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createaccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Account Created Successfully",Toast.LENGTH_LONG).show()
                saveuserdata()
                val intent=Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                    val error = task.exception?.localizedMessage ?: "Unknown error"
                    Toast.makeText(this, "$error", Toast.LENGTH_LONG).show()
                    Log.e("Account", "createAccount:failure", task.exception)

            }
        }
    }

    private fun saveuserdata() {
        userName=binding.ownerName.text.toString().trim()
        nameOfrestaurant=binding.restaurantName.text.toString().trim()
        email=binding.userEmail.text.toString().trim()
        password=binding.userPassword.text.toString().trim()
        val user= userModel(userName,nameOfrestaurant,email,password)
        val userId=FirebaseAuth.getInstance().currentUser!!.uid
        database.child("admin").child(userId).setValue(user)

    }
}