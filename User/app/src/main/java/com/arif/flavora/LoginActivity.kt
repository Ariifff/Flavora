package com.arif.flavora

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arif.flavora.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var GoogleSignInClient : GoogleSignInClient

    private lateinit var loginActivity : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loginActivity= ActivityLoginBinding.inflate(layoutInflater)
        val view=loginActivity.root
        setContentView(view)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        auth= Firebase.auth
        GoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        loginActivity.textNoAccount.setOnClickListener {
            val intent= Intent(this,SignActivity::class.java)
            startActivity(intent)
            finish()

        }
        loginActivity.buttonlogin.setOnClickListener {

            val email = loginActivity.editTextTextEmailAddress.text.toString().trim()
            val password = loginActivity.editTextTextPassword.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            } else {
                showLoading()
                loginUser(email, password)
            }

        }
        loginActivity.googleLogin.setOnClickListener {
            showLoading()
            GoogleSignInClient.signOut().addOnCompleteListener {
                val signIntent = GoogleSignInClient.signInIntent
                launcher.launch(signIntent)
            }
        }
        if(auth.currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun loginUser(email: String, password: String) {
        loginActivity.loginProgress.visibility = View.VISIBLE
        loginActivity.buttonlogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideLoading()

                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        // 👇 Change this to "admin" or "public" depending on the app
                        val userType = "public"  // or "public" for the user app

                        val dbRef = FirebaseDatabase.getInstance()
                            .getReference(userType)
                            .child(uid)

                        dbRef.get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                // ✅ Valid user of expected type
                                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                // ❌ Not allowed: exists in Firebase Auth but not under correct node
                                FirebaseAuth.getInstance().signOut()
                                Toast.makeText(
                                    this,
                                    "Access denied: Not a $userType account",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }.addOnFailureListener {
                            FirebaseAuth.getInstance().signOut()
                            Toast.makeText(
                                this,
                                "Error accessing user data",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    val exception = task.exception
                    val errorCode = (exception as? FirebaseAuthException)?.errorCode

                    when (errorCode) {
                        "ERROR_USER_NOT_FOUND" -> {
                            Toast.makeText(
                                this,
                                "User not found. Please sign up first.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        "ERROR_WRONG_PASSWORD" -> {
                            Toast.makeText(
                                this,
                                "Incorrect password. Please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        "ERROR_INVALID_EMAIL" -> {
                            Toast.makeText(this, "Invalid email format.", Toast.LENGTH_LONG).show()
                        }

                        else -> {
                            Toast.makeText(
                                this,
                                "Login failed: ${exception?.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
    }
    private fun showLoading() {
        loginActivity.progressOverlay.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(200).start()
        }
        loginActivity.buttonlogin.isEnabled = false
    }

    private fun hideLoading() {
        loginActivity.progressOverlay.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                loginActivity.progressOverlay.visibility = View.GONE
            }
            .start()
        loginActivity.buttonlogin.isEnabled = true
    }
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount = task.result
                    val credential= GoogleAuthProvider.getCredential(account.idToken,null)
                    auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if(authTask.isSuccessful) {
                            hideLoading()
                            Toast.makeText(this,"Successfully Sign-In with Google",Toast.LENGTH_LONG).show()
                            startActivity(Intent(this,MainActivity::class.java))
                            finish()
                        }
                        else {
                            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }else {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }

        }
}