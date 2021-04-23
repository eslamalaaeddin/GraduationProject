package com.example.graduationproject.ui.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.graduationproject.R
import com.example.graduationproject.cache.CachingViewModel
import com.example.graduationproject.databinding.ActivityTestingBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.LocaleHelper
import com.example.graduationproject.notification.NotificationsHandler
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val TAG = "TestingActivity"

class TestingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestingBinding
    private val cachingViewModel by viewModel<CachingViewModel>()
    private var accessToken = ""
    private lateinit var handler: NotificationsHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_testing)
        accessToken = SplashActivity.getAccessToken(this).orEmpty()

        binding.button.setOnClickListener {
            handler.fireServerSideNotification()
        }

        binding.button2.setOnClickListener {
            // 1
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->

                if (task.isSuccessful) {
                    // 3
                    val token = task.result?.toString()
                    Toast.makeText(baseContext, token, Toast.LENGTH_LONG).show()
                    handler = NotificationsHandler(
                        notifierId = "54912808635191",
                        notifierName = "Islam AlaaEddin",
                        notifierImageUrl = "${Constants.BASE_USER_IMAGE_URL}1618983959-54912808635191.jpg",
                        notifiedId = "51749856081962",
                        notifiedToken = token
                    )
                    Log.i(TAG, "333333 onCreate: $token")
                }
                // 2
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

            })
        }
    }



}


//    private fun getFavoriteProduct(){
//        lifecycleScope.launch {
//            cachingViewModel.getFavoriteProductsFromDb()?.observe(
//                this@TestingActivity,
//                Observer { favProducts ->
//                    Log.i(com.example.graduationproject.notification.com.example.graduationproject.notification.TAG, "TTTT onStart: ${favProducts.size}")
//                    Log.i(com.example.graduationproject.notification.com.example.graduationproject.notification.TAG, "TTTT onStart: ${favProducts}")
//                })
//        }
//    }

//    private fun getProducts(){
//        lifecycleScope.launch {
//            cachingViewModel.getProductsFromDb()?.observe(
//                this@TestingActivity,
//                Observer { products ->
//                    Log.i(com.example.graduationproject.notification.com.example.graduationproject.notification.TAG, "TTTT onStart: ${products.size}")
//                    Log.i(com.example.graduationproject.notification.com.example.graduationproject.notification.TAG, "TTTT onStart: ${products}")
//                })
//        }
//    }

//    private fun getProduct(productId: Long){
//        lifecycleScope.launch {
//            cachingViewModel.getProductFromDb(productId).observe(
//                this@TestingActivity,
//                Observer { products ->
//                    Log.i(com.example.graduationproject.notification.com.example.graduationproject.notification.TAG, "TTTT onStart: ${products.size}")
//                    Log.i(com.example.graduationproject.notification.com.example.graduationproject.notification.TAG, "TTTT onStart: ${products.first()}")
//                })
//        }
//    }
