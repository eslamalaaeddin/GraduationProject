package com.example.graduationproject.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.cache.CachingViewModel
import com.example.graduationproject.databinding.ActivityTestingBinding
import com.example.graduationproject.helpers.Constants
import com.example.graduationproject.helpers.LocaleHelper
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


private const val TAG = "TestingActivity"

class TestingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestingBinding
    private val cachingViewModel by viewModel<CachingViewModel>()
    private var accessToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_testing)
        accessToken = SplashActivity.getAccessToken(this).orEmpty()

        val prefs = getSharedPreferences("AppLanguage", 0)
        val locale = prefs.getString("Locale.Helper.Selected.Language", "")

        if (locale == "ar"){
            LocaleHelper.setLocale(this, "ar")
        }
        else{
            LocaleHelper.setLocale(this, "en")
        }

        binding.button1.setOnClickListener {
            LocaleHelper.setLocale(this, "en")
            binding.textView.text = getString(R.string.islam)
        }

        binding.button2.setOnClickListener {
            LocaleHelper.setLocale(this, "ar")
            binding.textView.text = getString(R.string.islam)
        }

    }


//    private fun getFavoriteProduct(){
//        lifecycleScope.launch {
//            cachingViewModel.getFavoriteProductsFromDb()?.observe(
//                this@TestingActivity,
//                Observer { favProducts ->
//                    Log.i(TAG, "TTTT onStart: ${favProducts.size}")
//                    Log.i(TAG, "TTTT onStart: ${favProducts}")
//                })
//        }
//    }

//    private fun getProducts(){
//        lifecycleScope.launch {
//            cachingViewModel.getProductsFromDb()?.observe(
//                this@TestingActivity,
//                Observer { products ->
//                    Log.i(TAG, "TTTT onStart: ${products.size}")
//                    Log.i(TAG, "TTTT onStart: ${products}")
//                })
//        }
//    }

//    private fun getProduct(productId: Long){
//        lifecycleScope.launch {
//            cachingViewModel.getProductFromDb(productId).observe(
//                this@TestingActivity,
//                Observer { products ->
//                    Log.i(TAG, "TTTT onStart: ${products.size}")
//                    Log.i(TAG, "TTTT onStart: ${products.first()}")
//                })
//        }
//    }
}