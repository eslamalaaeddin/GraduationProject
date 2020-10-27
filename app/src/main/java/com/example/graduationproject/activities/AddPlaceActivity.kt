package com.example.graduationproject.activities

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.graduationproject.R
import com.example.graduationproject.databinding.ActivityAddPlaceBinding

class AddPlaceActivity : AppCompatActivity() {
    private lateinit var addPlaceBinding: ActivityAddPlaceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPlaceBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_place)

//        addPlaceBinding.addPlacButton.setOnClickListener {
//            startActivity(Intent(this,HomeActivity::class.java))
//        }
        setUpToolbar()
    }

    private fun setUpToolbar(){
        setSupportActionBar(addPlaceBinding.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        addPlaceBinding.mainToolbar.setTitleTextColor(Color.WHITE)
        addPlaceBinding.mainToolbar.setSubtitleTextColor(Color.WHITE)
        addPlaceBinding.mainToolbar.overflowIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)
        addPlaceBinding.mainToolbar.navigationIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)
    }

}