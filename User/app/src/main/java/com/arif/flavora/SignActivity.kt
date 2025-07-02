package com.arif.flavora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arif.flavora.databinding.ActivitySignBinding
import com.arif.flavora.model.usermodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignActivity : AppCompatActivity() {
    private lateinit var signActivity: ActivitySignBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var userName : String
    private lateinit var userEmail : String
    private lateinit var userPassword : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        signActivity = ActivitySignBinding.inflate(layoutInflater)
        val view = signActivity.root
        setContentView(view)

        //initialise auth
        auth= Firebase.auth
        //initialise database
        database = Firebase.database.reference


        signActivity.textHaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        signActivity.buttoncreate.setOnClickListener {
            userName=signActivity.name.text.toString()
            userEmail=signActivity.email.text.toString().trim()
            userPassword=signActivity.password.text.toString().trim()

            if (userName.isBlank() || userEmail.isBlank() || userPassword.isBlank()){
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }else{
                createAccount()
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createAccount() {
        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener {task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Account Created Successfully",Toast.LENGTH_LONG).show()
                saveuserdata()
                val intent=Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this,"Account Creation Failed",Toast.LENGTH_LONG).show()
                Log.d("Account","createAccount:failure",task.exception)
            }

        }
    }

    private fun saveuserdata() {
        userName=signActivity.name.text.toString()
        userEmail=signActivity.email.text.toString().trim()
        userPassword=signActivity.password.text.toString().trim()
        val user= usermodel(userName,userEmail,userPassword)
        val userId=auth.currentUser?.uid
        if (userId != null) {
            database.child("public").child(userId).setValue(user)
        }

    }
}