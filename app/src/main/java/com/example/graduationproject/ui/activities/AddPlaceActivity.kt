package com.example.graduationproject.ui.activities

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.databinding.ActivityAddPlaceBinding
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.viewmodel.AddPlaceViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddPlaceActivity : AppCompatActivity() {
    private lateinit var addPlaceBinding: ActivityAddPlaceBinding
    private val addPlaceViewModel by viewModel<AddPlaceViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = SplashActivity.getAccessToken(this).orEmpty()
        addPlaceBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_place)

//        addPlaceBinding.addPlacButton.setOnClickListener {
//            startActivity(Intent(this,HomeActivity::class.java))
//        }
        setUpToolbar()

//        lifecycleScope.launch {
//            val rate = Rate(rate =  2.5)
//            val responseMessage = addPlaceViewModel.updateRatingToPlace(placeId = placeId, rate, accessToken)
//            if (responseMessage != null) {
//                Toast.makeText(this@AddPlaceActivity, responseMessage.message, Toast.LENGTH_SHORT).show()
//            }
//        }
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