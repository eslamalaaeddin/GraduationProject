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
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val TAG = "TestingActivity"

class TestingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestingBinding
    private val cachingViewModel by viewModel<CachingViewModel>()
    private var accessToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_testing)
        accessToken = SplashActivity.getAccessToken(this).orEmpty()

    }

    override fun onStart() {
        super.onStart()
//        getFavoriteProduct()
//        getProducts()
//        getProduct(83066)
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