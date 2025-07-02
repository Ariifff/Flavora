package com.arif.flavora

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arif.flavora.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity() {
    private lateinit var locationActivity: ActivityLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        locationActivity = ActivityLocationBinding.inflate(layoutInflater)
        val view = locationActivity.root
        setContentView(view)

        val location= arrayOf("Delhi","Mumbai","Kolkata","Chennai","Bangalore","Hyderabad","Varanasi","Ghazipur")
        val adapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,location)
        locationActivity.listoflocation.setAdapter(adapter)




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}