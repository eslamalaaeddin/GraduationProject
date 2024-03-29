package com.example.graduationproject.ui.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.graduationproject.adapters.RecommendedProductsAdapter
import com.example.graduationproject.cache.CachingViewModel
import com.example.graduationproject.databinding.ActivityProductBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.Utils.getAccessToken
import com.example.graduationproject.helper.Utils.getUserId
import com.example.graduationproject.helper.Utils.getUserImageUrl
import com.example.graduationproject.helper.Utils.getUserName
import com.example.graduationproject.models.products.FavoriteProduct
import com.example.graduationproject.models.products.PostFavoriteProduct
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.models.products.ProductImage
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
    private val productActivityViewModel by viewModel<ProductActivityViewModel>()
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
    private lateinit var loadingHandler: Handler
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var recProductsAdapter: RecommendedProductsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessToken = getAccessToken(this).orEmpty()
        userId = getUserId(this)
        userName = getUserName(this).orEmpty()
        userImageUrl = getUserImageUrl(this).orEmpty()

        loadingHandler = Handler(Looper.getMainLooper())

        placeDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_product)

        setUpToolbar()
        productId = intent.getLongExtra("productId", 0)
        connectionState = intent.getIntExtra("connectionState", -1)
        fromFavorite = intent.getBooleanExtra("fromFavorite", false)

        linearLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        //OFFLINE
        if (connectionState == 0) {
            placeDetailsBinding.recommendedForYouTextView.visibility = View.INVISIBLE
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
                                    adapter = PlaceImagesAdapter(placeImages)
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
            placeDetailsBinding.recommendedForYouTextView.visibility = View.VISIBLE
            lifecycleScope.launch {
                getUserSpecificRateOnline()
                getProductDetailsOnline()
                getRecommendedProductsByProduct()
                syncRates()
            }
        }

        placeDetailsBinding.addRatingToPlaceBar.setOnRatingBarChangeListener { _, rating, _ ->
            //it sends a put request each time as it is changed by the getUserSpecificRate()
            val rate = Rate(rate = rating)
            if (connectionState != 0) {
                //-1 means that no rate yet
                if (userRate == -1.0F) {
                    addRateToProduct(rate)
                }
                //to check if the user rates with the same previous rate
                else if (rating != userRate) {
                    updateRateToProduct(rate)
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
                lifecycleScope.launch { addProductToFavorite() }
            }
        }

        //Open comments bottom sheet
        placeDetailsBinding.addCommentFab.setOnClickListener {
            val commentsBottomSheet = CommentsBottomSheet(
                accessToken,
                productId,
                userId,
                userName,
                userImageUrl
            )
            commentsBottomSheet.show(supportFragmentManager, commentsBottomSheet.tag)
        }

        placeDetailsBinding.upButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("removedProduct", REMOVED)
            resultIntent.putExtra("addedProduct", ADDED)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

    }


    private fun getRecommendedProductsByProduct() {
        try {
            lifecycleScope.launch {
                //dismissProgressAfterTimeOut()
                productActivityViewModel.getRecommendedProductsByProductPagedList(
                    productId.toString(),
                    accessToken
                )
                    ?.observe(this@ProductActivity)
                    { recProducts ->

                        placeDetailsBinding.recommendedProductsByProductRecyclerView.apply {
                            layoutManager = linearLayoutManager

                            recProductsAdapter = RecommendedProductsAdapter(itemLayout = R.layout.item_recommended_by_product)
                            recProductsAdapter?.let {
                                placeDetailsBinding.progressBar.visibility = View.VISIBLE
                                it.submitList(recProducts)
                                adapter = it
                                lifecycleScope.launchWhenStarted {
                                    loadingHandler.postDelayed({
                                        placeDetailsBinding.progressBar.visibility = View.GONE
                                    },750)
                                }
                            }
                        }
                    }
            }
        } catch (ex: Throwable) {
            Log.i(TAG, "getRecommendedPlaces: ${ex.localizedMessage}")
        } finally {
            dismissProgressAfterTimeOut()
        }

    }

    //will be called in case of ONLINE MODE ONLY
    private fun syncRates() {
        lifecycleScope.launch {
            delay(1000)
            val productsLiveData = cachingViewModel.getProductFromDb(productId)
            productsLiveData.observe(this@ProductActivity) {
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

    //Get product data and bind it to views
    private suspend fun getProductDetailsOnline() {
        dismissProgressAfterTimeOut()
        val productDetailsLiveData =
            productActivityViewModel.getProductDetails(productId.toString(), accessToken)
        productDetailsLiveData?.observe(this@ProductActivity) { product ->
            product?.let {
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
                placeDetailsBinding.detailsPlaceRatingBar.rating = rate ?: 0F
                overallProductRate = rate ?: 0F

                isPlaceFavorite = it.favorite == 1
                if (isPlaceFavorite) {
                    add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
                } else {
                    add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
                }

                //to add product image
                placeDetailsBinding.placeImagesRecyclerView.apply {
                    val placeImages = mutableListOf<ProductImage>()
                    placeImages.add(ProductImage(name = product.image))
                    layoutManager = LinearLayoutManager(
                        this@ProductActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = PlaceImagesAdapter(placeImages)
                }
            }
            if (product == null) {
                placeDetailsBinding.progressBar.visibility = View.GONE
            }
        }
    }

    private suspend fun addProductToFavorite() {
        dismissProgressForFavoriteAfterTimeOut()
        val responseMessage = productActivityViewModel.addProductToFavorites(
            PostFavoriteProduct(pid = productId),
            accessToken
        )
        responseMessage?.let {
            currentFavoriteProduct?.let { fav ->
                currentProduct?.let { product ->
                    product.userRate = userRate
                    product.favorite = 1
                    cachingViewModel.insertAsTransaction(fav, product)
                }
            }
            isPlaceFavorite = true
            ADDED = true
            REMOVED = false
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
        val responseMessage = productActivityViewModel.deleteProductFromFavorites(
            productId.toString(),
            accessToken
        )
        responseMessage?.let {
            REMOVED = true
            ADDED = false

            currentFavoriteProduct?.let { fav ->
                currentProduct?.let { product ->
                    cachingViewModel.deleteAsTransaction(fav, product)
                    Log.i(TAG, "LLLL PRODUCT REMOVED TRANSACTION: ")
                }
            }

            isPlaceFavorite = false
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
        val rate = productActivityViewModel.getUserSpecificRate(
            productId.toString(),
            accessToken
        )
        rate?.let {
            userRate = it.rate ?: 0F
            placeDetailsBinding.addRatingToPlaceBar.rating = it.rate ?: 0F
            placeDetailsBinding.addCommentFab.visibility = View.VISIBLE
        }
    }

    private fun addRateToProduct(rate: Rate) {
        try {
            lifecycleScope.launch {
                placeDetailsBinding.addRatingToPlaceBar.isEnabled = false
                val responseMessage =
                    productActivityViewModel.addRatingToProduct(
                        rate,
                        productId.toString(),
                        accessToken
                    )
                responseMessage?.let {
                    placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
                    userRate = rate.rate ?: 0.0F
                    Toast.makeText(this@ProductActivity, getString(R.string.rate_added), Toast.LENGTH_SHORT).show()
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

    private fun updateRateToProduct(rate: Rate) {
        try {
            lifecycleScope.launch {
                placeDetailsBinding.addRatingToPlaceBar.isEnabled = false
                val responseMessage =
                    productActivityViewModel.updateRatingToProduct(
                        rate,
                        productId.toString(),
                        accessToken
                    )
                responseMessage?.let {
                    placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
                    Toast.makeText(this@ProductActivity, getString(R.string.rate_updated), Toast.LENGTH_SHORT).show()
                }

                if (responseMessage == null) {
                    placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
                }
            }
        } catch (ex: Throwable) {
            placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
        }
    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            placeDetailsBinding.progressBar.visibility = View.VISIBLE
            loadingHandler.postDelayed({
                placeDetailsBinding.progressBar.visibility = View.GONE
            }, Constants.TIME_OUT_MILLISECONDS)
        }

    }

    private fun dismissProgressForFavoriteAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            placeDetailsBinding.progressBar.visibility = View.VISIBLE
            placeDetailsBinding.addRemoveFavoriteFrameLayout.isEnabled = false
            placeDetailsBinding.addToFavoriteImageView.isEnabled = false
            loadingHandler.postDelayed({
                placeDetailsBinding.progressBar.visibility = View.GONE
                placeDetailsBinding.addRemoveFavoriteFrameLayout.isEnabled = true
                placeDetailsBinding.addToFavoriteImageView.isEnabled = true
            }, Constants.TIME_OUT_MILLISECONDS)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        loadingHandler.removeCallbacksAndMessages(null)
    }


}