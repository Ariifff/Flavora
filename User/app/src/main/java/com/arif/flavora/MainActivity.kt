package com.arif.flavora

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.arif.flavora.databinding.ActivityMainBinding
import com.arif.flavora.fragment.NotificationFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var NavController=findNavController(R.id.fragmentContainerView9)
        var bottomnav=findViewById<BottomNavigationView>(R.id.bottomNavigationView2)
        bottomnav.setupWithNavController(NavController)

        binding.notificationImage.setOnClickListener {
            val bottomSheetDialog= NotificationFragment()
            bottomSheetDialog.show(supportFragmentManager,"Test")
        }

        onBackPressedDispatcher.addCallback(this,object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                showdialog()
            }

        })

        ViewCompat.setOnApplyWindowInsetsListener(bottomnav) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0,0,0, systemBars.bottom)
            insets
        }
    }

    private fun showdialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit")
        builder.setMessage("Are you sure you want to exit?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Close the app
            finishAffinity()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog
        }
        builder.show()
    }
}