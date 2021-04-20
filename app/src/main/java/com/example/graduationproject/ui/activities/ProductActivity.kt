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
import com.example.graduationproject.cache.CachingViewModel
import com.example.graduationproject.databinding.ActivityProductBinding
import com.example.graduationproject.helpers.Constants
import com.example.graduationproject.models.products.FavoriteProduct
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.models.products.ProductImage
import com.example.graduationproject.models.products.VisitedProduct
import com.example.graduationproject.models.rating.Rate
import com.example.graduationproject.ui.bottomsheets.CommentsBottomSheet
import com.example.graduationproject.viewmodels.ProductActivityViewModel
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.fragment_up_sign.*
import kotlinx.android.synthetic.main.home_product_item.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val TAG = "ProductActivity"

class ProductActivity : AppCompatActivity() {
    private lateinit var placeDetailsBinding: ActivityProductBinding
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private val cachingViewModel by viewModel<CachingViewModel>()
    private var productId: Long = 0
    private var connectionState: Int = 0
    private lateinit var accessToken: String
    private var userId: Long = 0
    private var userName: String = ""
    private var userImageUrl: String = ""
    private var currentFavoriteProduct: FavoriteProduct? = null
    private var currentProduct: Product? = null
    var isPlaceFavorite = false
    private var overallProductRate: Float = 0.0F
    private var fromFavorite = false
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
        productId = intent.getLongExtra("placeId", 0)
        connectionState = intent.getIntExtra("connectionState", -1)
        fromFavorite = intent.getBooleanExtra("fromFavorite", false)

        //OFFLINE
        if (connectionState == 0) {
            Log.i(TAG, "UUUU onCreate: OFFLINE PRODUCT")
            try {
                lifecycleScope.launch {
                    cachingViewModel.getProductFromDb(productId)
                        .observe(this@ProductActivity) { product ->
                            product?.let {
                                Log.i(TAG, "UUUU onCreate: $it")
                                currentProduct = product
                                currentFavoriteProduct = FavoriteProduct(
                                    id = it.id,
                                    image = it.image,
                                    name = it.name,
                                    rating = it.rating
                                )
                                placeDetailsBinding.progressBar.visibility = View.GONE

                                placeDetailsBinding.detailsPlaceNameTextView.text = it.name
                                placeDetailsBinding.placeDescriptionTextView.text = it.description
                                //comedy,fantasy,drama ==> Comedy, Fantasy, Drama
                                placeDetailsBinding.productTagsTextView.text =
                                    it.tags.orEmpty().splitToSequence(",").toList()
                                        .joinToString(", ") { item -> item.capitalize() }

                                val rate = it.rating
                                Log.i(TAG, "PPPP getProductDetails: $rate")
                                placeDetailsBinding.detailsPlaceRatingBar.rating = rate ?: 0F
                                overallProductRate = rate ?: 0F
                                Log.i(TAG, "PPPP OverallRATE: ${overallProductRate}")

                                it.userRate?.let { rate ->
                                    placeDetailsBinding.addRatingToPlaceBar.rating =
                                        if (rate == -1.0F) 0F else rate
                                }

                                placeDetailsBinding.addRemoveFavoriteFrameLayout.visibility =
                                    View.GONE


                                placeDetailsBinding.placeImagesRecyclerView.apply {
                                    val placeImages = mutableListOf<ProductImage>()
                                    placeImages.add(ProductImage(name = product.image))
                                    layoutManager = LinearLayoutManager(
                                        this@ProductActivity,
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )
                                    adapter = PlaceImagesAdapter(productId.toString(), placeImages)
                                    //snapHelper.attachToRecyclerView(this)
                                }
                            }
                            if (product == null) {
                                placeDetailsBinding.progressBar.visibility = View.GONE
                            }
                        }
                }
            } catch (ex: Throwable) {
                Log.e(TAG, ex.localizedMessage.orEmpty())
            }
        }
        //ONLINE
        else {
            lifecycleScope.launch {
                Log.i(TAG, "UUUU onCreate: ONLINE PRODUCT")
                getUserSpecificRateOnline()
                getProductDetailsOnline()
                syncRates()
            }
        }

        placeDetailsBinding.addRatingToPlaceBar.setOnRatingBarChangeListener { _, rating, _ ->
            //it sends a put request each time as it is changed by the getUserSpecificRate()
            val rate = Rate(rate = rating)
            if (connectionState != 0) {
                if (userRate == -1.0F) {
                    addRateToPlace(rate)
                } else if (rating != userRate) {
                    updateRateFromPlace(rate)
                }
            }

        }

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
                productId,
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

    }

    //will be called in case of ONLINE MODE ONLY
    private fun syncRates() {
        lifecycleScope.launch {
            delay(1000)
            val productsLiveData = cachingViewModel.getProductFromDb(productId)
            productsLiveData.observe(this@ProductActivity) {
                Log.i(TAG, "syncRates: $it")
                it?.let {
                    val productFromDb = it
                    currentProduct?.let { serverProduct ->
                        currentFavoriteProduct?.let { currentFav ->
                            if (shouldSync(productFromDb, serverProduct)) {
                                productFromDb.userRate = userRate
                                productFromDb.rating = serverProduct.rating
                                currentFav.rating = serverProduct.rating

                                lifecycleScope.launch {
                                    cachingViewModel.insertAsTransaction(currentFav, productFromDb)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun shouldSync(dbProduct: Product, serverProduct: Product): Boolean {
        return dbProduct.userRate != userRate || dbProduct.rating != serverProduct.rating
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
            resources.getColor(R.color.white_font_text),
            PorterDuff.Mode.SRC_IN
        )
        placeDetailsBinding.mainToolbar.navigationIcon?.setColorFilter(
            resources.getColor(R.color.white_font_text),
            PorterDuff.Mode.SRC_IN
        )
    }

    private suspend fun getProductDetailsOnline() {
        dismissProgressAfterTimeOut()
        val productDetailsLiveData =
            placeActivityViewModel.getProductDetails(productId.toString(), accessToken)
        productDetailsLiveData?.observe(this@ProductActivity) { product ->
            product?.let {
                //create object of favorite product to added it to room
                //I HAVE TO ADD OR REMOVE PRODUCT ITSELF INTO DB

                currentProduct = product

                currentFavoriteProduct = FavoriteProduct(
                    id = it.id,
                    image = it.image,
                    name = it.name,
                    rating = it.rating
                )
                placeDetailsBinding.progressBar.visibility = View.GONE
//                placeDetailsBinding.addCommentFab.isEnabled = true

                placeDetailsBinding.detailsPlaceNameTextView.text = it.name
                placeDetailsBinding.placeDescriptionTextView.text = it.description
                //comedy,fantasy,drama ==> Comedy, Fantasy, Drama
                placeDetailsBinding.productTagsTextView.text =
                    it.tags.orEmpty().splitToSequence(",").toList()
                        .joinToString(", ") { item -> item.capitalize() }

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
                    adapter = PlaceImagesAdapter(productId.toString(), placeImages)
                    //snapHelper.attachToRecyclerView(this)
                }
            }
            if (product == null) {
                placeDetailsBinding.progressBar.visibility = View.GONE
            }
        }
    }

    private suspend fun addPlaceToFavorite() {
        dismissProgressForFavoriteAfterTimeOut()
        val responseMessage = placeActivityViewModel.addProductToFavorites(
            VisitedProduct(pid = productId),
            accessToken
        )
        responseMessage?.let {
            //commented to work offline and faster
            // add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
            currentFavoriteProduct?.let { fav ->
                currentProduct?.let { product ->
                    product.userRate = userRate
                    cachingViewModel.insertAsTransaction(fav, product)
                    Log.i(TAG, "LLLL PRODUCT ADDED TRANSACTION: ")
                }
            }
            isPlaceFavorite = true
            ADDED = true
            REMOVED = false
//            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            placeDetailsBinding.progressBar.visibility = View.GONE
            placeDetailsBinding.addRemoveFavoriteFrameLayout.isEnabled = true
            placeDetailsBinding.addToFavoriteImageView.isEnabled = true
        }

        if (responseMessage == null) {
            placeDetailsBinding.progressBar.visibility = View.GONE
            placeDetailsBinding.addRemoveFavoriteFrameLayout.isEnabled = true
            placeDetailsBinding.addToFavoriteImageView.isEnabled = true
        }
    }

    private suspend fun deleteProductFromFavorite() {
        dismissProgressForFavoriteAfterTimeOut()
        val responseMessage = placeActivityViewModel.deleteProductFromFavorites(
            productId.toString(),
            accessToken
        )
        responseMessage?.let {
            REMOVED = true
            ADDED = false

            currentFavoriteProduct?.let { fav ->
                currentProduct?.let { product ->
//                    cachingViewModel.deleteFromFavorites(fav)
//                    cachingViewModel.deleteFromProducts(product)
                    cachingViewModel.deleteAsTransaction(fav, product)
                    Log.i(TAG, "LLLL PRODUCT REMOVED TRANSACTION: ")
                }
            }

            //commented to work offline and faster
            //add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
            isPlaceFavorite = false
//            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            placeDetailsBinding.progressBar.visibility = View.GONE
            placeDetailsBinding.addRemoveFavoriteFrameLayout.isEnabled = true
            placeDetailsBinding.addToFavoriteImageView.isEnabled = true
        }

        if (responseMessage == null) {
            placeDetailsBinding.progressBar.visibility = View.GONE
            placeDetailsBinding.addRemoveFavoriteFrameLayout.isEnabled = true
            placeDetailsBinding.addToFavoriteImageView.isEnabled = true
        }
    }

    private suspend fun getUserSpecificRateOnline() {
        val rate = placeActivityViewModel.getUserSpecificRate(
            productId.toString(),
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
                    placeActivityViewModel.addRatingToProduct(
                        rate,
                        productId.toString(),
                        accessToken
                    )
                responseMessage?.let {
                    placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
                    userRate = rate.rate ?: 0.0F
                    Toast.makeText(this@ProductActivity, "Rate added", Toast.LENGTH_SHORT).show()
                }
                if (responseMessage == null) {
                    placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
                }
            }
        } catch (ex: Throwable) {
            placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
        } finally {
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
                        productId.toString(),
                        accessToken
                    )
                responseMessage?.let {
                    placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
                    Toast.makeText(this@ProductActivity, "Rate updated", Toast.LENGTH_SHORT).show()
                }

                if (responseMessage == null) {
                    placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
                }
            }
        } catch (ex: Throwable) {
            placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
        }
//        finally {
//            placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
//        }

    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            placeDetailsBinding.progressBar.visibility = View.VISIBLE
//            placeDetailsBinding.addCommentFab.isEnabled = false
            Handler().postDelayed({
                placeDetailsBinding.progressBar.visibility = View.GONE
//                placeDetailsBinding.addCommentFab.isEnabled = true
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