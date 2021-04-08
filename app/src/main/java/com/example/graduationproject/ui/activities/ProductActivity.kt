package com.example.graduationproject.ui.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.graduationproject.R
import com.example.graduationproject.adapters.CommentsAdapter
import com.example.graduationproject.adapters.PlaceImagesAdapter
import com.example.graduationproject.databinding.ActivityProductBinding
import com.example.graduationproject.helper.listeners.CommentListener
import com.example.graduationproject.model.comments.ProductComment
import com.example.graduationproject.model.products.Comment
import com.example.graduationproject.model.products.ProductImage
import com.example.graduationproject.model.products.VisitedProduct
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.ui.bottomsheets.CommentConfigsBottomSheet
import com.example.graduationproject.viewmodel.ProductActivityViewModel
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val TAG = "PlaceActivity"
class PlaceActivity : AppCompatActivity(), CommentListener {
    private lateinit var placeDetailsBinding: ActivityProductBinding
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private var placeId: Long = 0
    private lateinit var accessToken :String
    private var userId :Long = 0
    var isPlaceFavorite = false
    private var overallProductRate : Float = 0.0F
    private var userRate : Float = 0.0F
    private var commentsAdapter : CommentsAdapter? = null
    private lateinit var snapHelper: LinearSnapHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessToken = SplashActivity.getAccessToken(this).orEmpty()
        userId = SplashActivity.getUserId(this)


        Log.i(TAG, "CCCC getProductDetails: $userId")

        placeDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_product)

        setUpToolbar()
        placeId = intent.getLongExtra("placeId", 0)

        placeDetailsBinding.addRatingToPlaceBar.setOnRatingBarChangeListener { _, rating, _ ->
            val rate = Rate(rate = rating)

            if (userRate == 0.0F) {
                addRateToPlace(rate)
            }
            else {
                updateRateFromPlace(rate)
            }

        }

        //Get place Rate by user or zero if null
        lifecycleScope.launch { getUserSpecificRate() }

        //Add Product to favorite or remove it from fav.
        placeDetailsBinding.addToFavoriteImageView.setOnClickListener {
            if (isPlaceFavorite) {
                lifecycleScope.launch { deleteProductFromFavorite() }
            } else {
                lifecycleScope.launch { addPlaceToFavorite() }
            }
        }


        placeDetailsBinding.placeCommentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("RestrictedApi")
            override fun afterTextChanged(text: Editable?) {
                if (text.toString().isEmpty()) {
                    placeDetailsBinding.addCommentFab.visibility = View.GONE
                } else {
                    placeDetailsBinding.addCommentFab.visibility = View.VISIBLE
                }
            }
        })
        placeDetailsBinding.addCommentFab.setOnClickListener {
            addCommentAndUpdateComments()
        }

        placeDetailsBinding.upButtonImageButton.setOnClickListener { finish() }

    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch { getComments() }
        lifecycleScope.launch{ getProductDetails() }
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

    override fun onMoreOnCommentClicked(comment: Comment) {
        comment.rate = overallProductRate
        val commentConfigsBottomSheet = CommentConfigsBottomSheet(this, comment, placeId)
        commentConfigsBottomSheet.show(supportFragmentManager, commentConfigsBottomSheet.tag)
    }

    //when user delete or add comment it will take effect immediately
    override fun onCommentModified(comment: String?) {
        lifecycleScope.launch {
            //comment deleted --> updated the adapter, and delete that item
            if (comment == null){

            }
            //comment updated
            else{

            }
            getComments()
        }
    }

    //when user rate or update rating it will take effect immediately
//    override fun onProductRated() {
//        lifecycleScope.launch {
//            getPlaceDetails()
//            getPlaceRate()
//            getComments()
//        }
//    }


    private suspend fun getProductDetails(){
            val productDetailsLiveData =
                placeActivityViewModel.getProductDetails(placeId.toString(), accessToken)
            productDetailsLiveData?.observe(this@PlaceActivity) { product ->
                product?.let {
                    placeDetailsBinding.detailsPlaceNameTextView.text = it.name
                    placeDetailsBinding.placeDescriptionTextView.text = it.description


                    val rate =  it.rating
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
                        layoutManager = LinearLayoutManager(this@PlaceActivity,LinearLayoutManager.HORIZONTAL,false)
                        adapter = PlaceImagesAdapter(placeId.toString(), placeImages)
                        //snapHelper.attachToRecyclerView(this)
                    }
                }
                Log.i(TAG, "PPPP getPlaceDetails: $product" )
            }
    }

    private fun addCommentAndUpdateComments(){
        val placeComment = ProductComment(
            placeId = placeId,
            comment = placeDetailsBinding.placeCommentEditText.text.toString()
        )
        lifecycleScope.launch {
            val responseMessage = placeActivityViewModel.addCommentOnProduct(
                placeComment,
                accessToken
            )
            responseMessage?.let {
                placeDetailsBinding.placeCommentEditText.text.clear()
//                TODO("NOTIFY ITEM INSERTED")

                getComments()
            }
        }
    }

//    private suspend fun getComments(){
//        val placeCommentsLiveData = placeActivityViewModel.getProductComments(
//            placeId.toString(),
//            1,
//            accessToken
//        )
//        placeCommentsLiveData?.observe(this@PlaceActivity) { comments ->
//            placeDetailsBinding.commentsRecyclerView.apply {
//                layoutManager = LinearLayoutManager(this@PlaceActivity)
//                adapter = CommentsAdapter(
//                    79312841268376,
//                    comments.orEmpty(),
//                    this@PlaceActivity
//                )
//            }
//        }
//    }

    private suspend fun getComments(){
        val productCommentsLiveData = placeActivityViewModel.getCommentsPagedList(
            placeId.toString(),
            accessToken
        )
        productCommentsLiveData?.observe(this@PlaceActivity) { comments ->
            placeDetailsBinding.commentsRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@PlaceActivity)
                commentsAdapter = CommentsAdapter(
                    userId,
                    this@PlaceActivity
                )
                commentsAdapter?.let {
                    it.submitList(comments)
                    adapter = it
                }
            }
        }
    }

//    private suspend fun getPlaceImages(){
//        val productImagesLiveData =  placeActivityViewModel.getPlaceImages(placeId.toString(), accessToken)
//        productImagesLiveData?.observe(this@PlaceActivity){placeImages ->
//            placeDetailsBinding.placeImagesRecyclerView.apply {
//                layoutManager = LinearLayoutManager(this@PlaceActivity,LinearLayoutManager.HORIZONTAL,false)
//                adapter = PlaceImagesAdapter(placeId.toString(),placeImages.orEmpty())
//                snapHelper.attachToRecyclerView(this)
//            }
//        }
//    }

    private suspend fun addPlaceToFavorite(){
            val responseMessage = placeActivityViewModel.addProductToFavorites(
                VisitedProduct(pid = placeId),
                accessToken
            )
            responseMessage?.let {
                add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
                isPlaceFavorite = true
            }
    }

    private suspend fun deleteProductFromFavorite(){
            val responseMessage = placeActivityViewModel.deleteProductFromFavorites(
                placeId.toString(),
                accessToken
            )
            responseMessage?.let {
                add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
                isPlaceFavorite = false
            }
    }

    private suspend fun getUserSpecificRate(){
        val rate = placeActivityViewModel.getUserSpecificRate(
            placeId.toString(),
            accessToken
        )
        rate?.let {
            Log.i(TAG, "llll USER RATE BEFORE INITIALIZING : $userRate")
            userRate = it.rate ?: 0F
            Log.i(TAG, "llll USER RATE AFTER INITIALIZING: $userRate")
            placeDetailsBinding.addRatingToPlaceBar.rating = it.rate ?: 0F
        }

    }

    private fun addRateToPlace(rate: Rate){
        lifecycleScope.launch {
            val responseMessage =
                placeActivityViewModel.addRatingToProduct(rate, placeId.toString(), accessToken)
            responseMessage?.let {
//                Toast.makeText(this@PlaceActivity, "Rate added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateRateFromPlace(rate: Rate){
        lifecycleScope.launch {
            val responseMessage =
                placeActivityViewModel.updateRatingToProduct(rate, placeId.toString(), accessToken)
            responseMessage?.let {
//                Toast.makeText(this@PlaceActivity, "Rate updated", Toast.LENGTH_SHORT).show()
            }
        }
    }


}