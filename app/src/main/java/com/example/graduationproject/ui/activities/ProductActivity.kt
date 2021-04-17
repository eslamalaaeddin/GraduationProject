package com.example.graduationproject.ui.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.graduationproject.R
import com.example.graduationproject.adapters.PlaceImagesAdapter
import com.example.graduationproject.databinding.ActivityProductBinding
import com.example.graduationproject.helpers.Constants
import com.example.graduationproject.models.products.ProductImage
import com.example.graduationproject.models.products.VisitedProduct
import com.example.graduationproject.models.rating.Rate
import com.example.graduationproject.ui.bottomsheets.CommentsBottomSheet
import com.example.graduationproject.viewmodels.ProductActivityViewModel
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.fragment_up_sign.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val TAG = "ProductActivity"

class ProductActivity : AppCompatActivity(){
    private lateinit var placeDetailsBinding: ActivityProductBinding
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private var placeId: Long = 0
    private lateinit var accessToken: String
    private var userId: Long = 0
    private var userName: String = ""
    private var userImageUrl: String = ""
    var isPlaceFavorite = false
    private var overallProductRate: Float = 0.0F
    private var userRate: Float = 0.0F
    private var REMOVED = false
    private var ADDED = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessToken = SplashActivity.getAccessToken(this).orEmpty()
        userId = SplashActivity.getUserId(this)
        userName = SplashActivity.getUserName(this).orEmpty()
        userImageUrl = SplashActivity.getUserImageUrl(this).orEmpty()

        placeDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_product)

        setUpToolbar()
        placeId = intent.getLongExtra("placeId", 0)

        placeDetailsBinding.addRatingToPlaceBar.setOnRatingBarChangeListener { _, rating, _ ->
            val rate = Rate(rate = rating)

            if (userRate == 0.0F) {
                addRateToPlace(rate)
            }
            else if (rating != userRate) {
                updateRateFromPlace(rate)
            }
        }

        //Get place Rate by user or zero if null
        lifecycleScope.launch { getUserSpecificRate() }

        //Add Product to favorite or remove it from fav.
        placeDetailsBinding.addRemoveFavoriteFrameLayout.setOnClickListener {
            if (isPlaceFavorite) {
                placeDetailsBinding.addToFavoriteImageView.setImageResource(R.drawable.ic_heart)
                lifecycleScope.launch { deleteProductFromFavorite() }
            } else {
                placeDetailsBinding.addToFavoriteImageView.setImageResource(R.drawable.ic_heart_filled)
                lifecycleScope.launch { addPlaceToFavorite() }
            }
        }

        placeDetailsBinding.addCommentFab.setOnClickListener {
            val commentsBottomSheet = CommentsBottomSheet(
                accessToken,
                placeId,
                userId,
                userName,
                userImageUrl
            )
            commentsBottomSheet.show(supportFragmentManager, commentsBottomSheet.tag)
           //Open comments bottom sheet
        }

        placeDetailsBinding.upButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("removedProduct", REMOVED)
            resultIntent.putExtra("addedProduct", ADDED)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        lifecycleScope.launch {
            getProductDetails()
        }
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("removedProduct", REMOVED)
        resultIntent.putExtra("addedProduct", ADDED)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun setUpToolbar() {
        setSupportActionBar(placeDetailsBinding.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        placeDetailsBinding.mainToolbar.setTitleTextColor(Color.WHITE)
        placeDetailsBinding.mainToolbar.setSubtitleTextColor(Color.WHITE)
        placeDetailsBinding.mainToolbar.overflowIcon?.setColorFilter(
            resources.getColor(R.color.white),
            PorterDuff.Mode.SRC_IN
        )
        placeDetailsBinding.mainToolbar.navigationIcon?.setColorFilter(
            resources.getColor(R.color.white),
            PorterDuff.Mode.SRC_IN
        )
    }

    private suspend fun getProductDetails() {
        dismissProgressAfterTimeOut()
        val productDetailsLiveData =
            placeActivityViewModel.getProductDetails(placeId.toString(), accessToken)
        productDetailsLiveData?.observe(this@ProductActivity) { product ->
            product?.let {

                placeDetailsBinding.progressBar.visibility = View.GONE
                placeDetailsBinding.addCommentFab.isEnabled = true

                placeDetailsBinding.detailsPlaceNameTextView.text = it.name
                placeDetailsBinding.placeDescriptionTextView.text = it.description

                placeDetailsBinding.productTagsTextView.text = it.tags.toString()

                val rate = it.rating
                Log.i(TAG, "PPPP getProductDetails: $rate")
                placeDetailsBinding.detailsPlaceRatingBar.rating = rate ?: 0F
                overallProductRate = rate ?: 0F
                Log.i(TAG, "PPPP OverallRATE: ${overallProductRate}")

                isPlaceFavorite = it.isFavorite == 1

                if (isPlaceFavorite) {
                    add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
                } else {
                    add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
                }

                placeDetailsBinding.placeImagesRecyclerView.apply {
                    val placeImages = mutableListOf<ProductImage>()
                    placeImages.add(ProductImage(name = product.image))
                    layoutManager = LinearLayoutManager(
                        this@ProductActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = PlaceImagesAdapter(placeId.toString(), placeImages)
                    //snapHelper.attachToRecyclerView(this)
                }
            }
            if (product == null){
                placeDetailsBinding.progressBar.visibility = View.GONE
            }
        }
    }

    private suspend fun addPlaceToFavorite() {
        dismissProgressForFavoriteAfterTimeOut()
        val responseMessage = placeActivityViewModel.addProductToFavorites(
            VisitedProduct(pid = placeId),
            accessToken
        )
        responseMessage?.let {
            //commented to work offline and faster
            // add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
            isPlaceFavorite = true
            ADDED = true
            REMOVED = false
//            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            placeDetailsBinding.progressBar.visibility = View.GONE
            placeDetailsBinding.addRemoveFavoriteFrameLayout.isEnabled = true
            placeDetailsBinding.addToFavoriteImageView.isEnabled = true
        }

        if (responseMessage == null){
            placeDetailsBinding.progressBar.visibility = View.GONE
            placeDetailsBinding.addRemoveFavoriteFrameLayout.isEnabled = true
            placeDetailsBinding.addToFavoriteImageView.isEnabled = true
        }
    }

    private suspend fun deleteProductFromFavorite() {
        dismissProgressForFavoriteAfterTimeOut()
        val responseMessage = placeActivityViewModel.deleteProductFromFavorites(
            placeId.toString(),
            accessToken
        )
        responseMessage?.let {
            REMOVED = true
            ADDED = false
            //commented to work offline and faster
            //add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
            isPlaceFavorite = false
//            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            placeDetailsBinding.progressBar.visibility = View.GONE
            placeDetailsBinding.addRemoveFavoriteFrameLayout.isEnabled = true
            placeDetailsBinding.addToFavoriteImageView.isEnabled = true
        }

        if (responseMessage == null){
            placeDetailsBinding.progressBar.visibility = View.GONE
            placeDetailsBinding.addRemoveFavoriteFrameLayout.isEnabled = true
            placeDetailsBinding.addToFavoriteImageView.isEnabled = true
        }
    }

    private suspend fun getUserSpecificRate() {
        val rate = placeActivityViewModel.getUserSpecificRate(
            placeId.toString(),
            accessToken
        )
        rate?.let {
            Log.i(TAG, "llll USER RATE BEFORE INITIALIZING : $userRate")
            userRate = it.rate ?: 0F
            Log.i(TAG, "llll USER RATE AFTER INITIALIZING: $userRate")
            placeDetailsBinding.addRatingToPlaceBar.rating = it.rate ?: 0F
            placeDetailsBinding.addCommentFab.visibility = View.VISIBLE
        }
    }

    private fun addRateToPlace(rate: Rate) {
        try {
            lifecycleScope.launch {
                placeDetailsBinding.addRatingToPlaceBar.isEnabled = false
                val responseMessage =
                    placeActivityViewModel.addRatingToProduct(rate, placeId.toString(), accessToken)
                responseMessage?.let {
                    placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
                    userRate = rate.rate ?: 0.0F
                Toast.makeText(this@ProductActivity, "Rate added", Toast.LENGTH_SHORT).show()
                }
                if (responseMessage == null){
                    placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
                }
            }
        }
        catch (ex: Throwable){
            placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
        }
        finally {
            placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
        }

    }

    private fun updateRateFromPlace(rate: Rate) {
        try {
            lifecycleScope.launch {
                placeDetailsBinding.addRatingToPlaceBar.isEnabled = false
                val responseMessage =
                    placeActivityViewModel.updateRatingToProduct(
                        rate,
                        placeId.toString(),
                        accessToken
                    )
                responseMessage?.let {
                    placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
                Toast.makeText(this@ProductActivity, "Rate updated", Toast.LENGTH_SHORT).show()
                }

                if (responseMessage == null){
                    placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
                }
            }
        }
        catch (ex: Throwable){
            placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
        }
//        finally {
//            placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
//        }

    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            placeDetailsBinding.progressBar.visibility = View.VISIBLE
            placeDetailsBinding.addCommentFab.isEnabled = false
            Handler().postDelayed({
                placeDetailsBinding.progressBar.visibility = View.GONE
                placeDetailsBinding.addCommentFab.isEnabled = true
            }, Constants.TIME_OUT_MILLISECONDS)
        }

    }

    private fun dismissProgressForFavoriteAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            placeDetailsBinding.progressBar.visibility = View.VISIBLE
            placeDetailsBinding.addRemoveFavoriteFrameLayout.isEnabled = false
            placeDetailsBinding.addToFavoriteImageView.isEnabled = false
            Handler().postDelayed({
                placeDetailsBinding.progressBar.visibility = View.GONE
                placeDetailsBinding.addRemoveFavoriteFrameLayout.isEnabled = true
                placeDetailsBinding.addToFavoriteImageView.isEnabled = true
            }, Constants.TIME_OUT_MILLISECONDS)
        }

    }


}