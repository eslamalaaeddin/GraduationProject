package com.example.graduationproject.ui.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
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
import kotlinx.android.synthetic.main.place_details_item_layout.view.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


const val image1 =
    "https://www.history.com/.image/c_fill%2Ccs_srgb%2Cfl_progressive%2Ch_400%2Cq_auto:good%2Cw_620/MTU3ODc5MDg2NDMxODcyNzM1/egyptian-pyramids-hero.jpg"
const val image2 =
    "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/sunset-at-the-pyramids-giza-cairo-egypt-royalty-free-image-1588090066.jpg?crop=1.00xw:0.891xh;0,0.101xh&resize=640:*"
const val image3 = "https://images.memphistours.com/large/34d5b5a3fbaa4b3b5d9487bf924b0145.jpg"
const val image4 =
    "https://st2.depositphotos.com/3974537/10978/v/600/depositphotos_109787082-stock-video-pyramids-at-night-with-moon.jpg"
const val image5 = image1
const val image6 =
    "https://t3.ftcdn.net/jpg/02/66/45/50/360_F_266455083_yASCBTitC7vtyI7dBL9kzk1SDXLS3m6s.jpg"


private const val TAG = "PlaceActivity"
class PlaceActivity : AppCompatActivity(), CommentListener {
    private lateinit var placeDetailsBinding: ActivityProductBinding
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private var placeId: Long = 0
    private lateinit var accessToken :String
    var isPlaceFavorite = false
    var placeRate : Int? = 0
    private lateinit var snapHelper: LinearSnapHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessToken = SplashActivity.getAccessToken(this).orEmpty()
        placeDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_product)

        setUpToolbar()
        placeId = intent.getLongExtra("placeId", 0)
        placeDetailsBinding.addRatingToPlaceBar.setOnRatingBarChangeListener { _, rating, _ ->
            val rate = Rate(rate = rating.toInt())
            if (placeRate == null) {
                addRateToPlace(rate)
            }
            else{
                deleteRateFromPlace(rate)
            }
        }


        //Get place Rate by user or zero if null
        lifecycleScope.launch { getPlaceRate() }

        placeDetailsBinding.dummyFinish.setOnClickListener {
            finish()
        }

        //Add Product to favorite or remove it from fav.
        placeDetailsBinding.addToFavoriteImageView.setOnClickListener {
            if (isPlaceFavorite) {
                lifecycleScope.launch { deletePlaceFromFavorite() }
            } else {
                lifecycleScope.launch { addPlaceToFavorite() }
            }
        }

//        snapHelper = object : LinearSnapHelper() {
//            override fun findTargetSnapPosition(
//                layoutManager: RecyclerView.LayoutManager,
//                velocityX: Int,
//                velocityY: Int
//            ): Int {
//                val centerView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
//                val position = layoutManager.getPosition(centerView)
//                var targetPosition = -1
//                if (layoutManager.canScrollHorizontally()) {
//                    targetPosition = if (velocityX < 0) {
//                        position - 1
//                    } else {
//                        position + 1
//                    }
//                }
//                if (layoutManager.canScrollVertically()) {
//                    targetPosition = if (velocityY < 0) {
//                        position - 1
//                    } else {
//                        position + 1
//                    }
//                }
//                val firstItem = 0
//                val lastItem = layoutManager.itemCount - 1
//                targetPosition = lastItem.coerceAtMost(targetPosition.coerceAtLeast(firstItem))
//                return targetPosition
//            }
//        }

        //lifecycleScope.launch { getPlaceImages() }




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

//        placeDetailsBinding.swipeRefreshLayout.setOnRefreshListener {
//            onStart()
//            placeDetailsBinding.swipeRefreshLayout.isRefreshing = false
//        }

        placeDetailsBinding.upButtonImageButton.setOnClickListener { finish() }

    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch { getComments() }
        lifecycleScope.launch{ getPlaceDetails() }
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
        comment.rate = placeRate?.toFloat()
        val commentConfigsBottomSheet = CommentConfigsBottomSheet(this, comment, placeId)
        commentConfigsBottomSheet.show(supportFragmentManager, commentConfigsBottomSheet.tag)
    }

    //when user delete or add comment it will take effect immediately
    override fun onCommentModified() {
        lifecycleScope.launch {
            getComments()
        }
    }

    //when user rate or update rating it will take effect immediately
    override fun onProductRated() {
        lifecycleScope.launch {
            getPlaceDetails()
            getPlaceRate()
            getComments()
        }
    }


    private suspend fun getPlaceDetails(){
            val placeDetailsLiveData =
                placeActivityViewModel.getProductDetails(placeId.toString(), accessToken)
            placeDetailsLiveData?.observe(this@PlaceActivity) { place ->
                place?.let {
                    placeDetailsBinding.detailsPlaceNameTextView.text = it.name
                    placeDetailsBinding.placeDescriptionTextView.text = it.description
                    val rate =  it.rating
                    placeDetailsBinding.detailsPlaceRatingBar.rating =rate?.toFloat() ?: 0F
                    isPlaceFavorite = it.isFavorite == 1

                    if (isPlaceFavorite) {
                        add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
                    } else {
                        add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
                    }

                    placeDetailsBinding.placeImagesRecyclerView.apply {
                        val placeImages = mutableListOf<ProductImage>()
                        placeImages.add(ProductImage(name = place.image))
                        layoutManager = LinearLayoutManager(this@PlaceActivity,LinearLayoutManager.HORIZONTAL,false)
                        adapter = PlaceImagesAdapter(placeId.toString(), placeImages)
                        //snapHelper.attachToRecyclerView(this)
                    }
                }
                Log.i(TAG, "getPlaceDetails: $place" )
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
                getComments()
            }
        }
    }

    private suspend fun getComments(){
        val placeCommentsLiveData = placeActivityViewModel.getProductComments(
            placeId.toString(),
            1,
            accessToken
        )
        placeCommentsLiveData?.observe(this@PlaceActivity) { comments ->
            placeDetailsBinding.commentsRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@PlaceActivity)
                adapter = CommentsAdapter(
                    79312841268376,
                    comments.orEmpty(),
                    this@PlaceActivity
                )
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

    private suspend fun deletePlaceFromFavorite(){
            val responseMessage = placeActivityViewModel.deleteProductFromFavorites(
                placeId.toString(),
                accessToken
            )
            responseMessage?.let {
                add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
                isPlaceFavorite = false
            }
    }

    private suspend fun getPlaceRate(){
        val rate = placeActivityViewModel.getProductRate(
            placeId.toString(),
            accessToken
        )
        rate?.let {
            addRatingToPlaceBar.visibility = View.VISIBLE
            placeRate = it.rate ?: 0
            placeDetailsBinding.addRatingToPlaceBar.rating = it.rate?.toFloat() ?: 0F
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

    private fun deleteRateFromPlace(rate: Rate){
        lifecycleScope.launch {
            val responseMessage =
                placeActivityViewModel.updateRatingToProduct(rate, placeId.toString(), accessToken)
            responseMessage?.let {
//                Toast.makeText(this@PlaceActivity, "Rate updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

}