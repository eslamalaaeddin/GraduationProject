package com.example.graduationproject.ui.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.adapters.CommentsAdapter
import com.example.graduationproject.adapters.PlaceImagesAdapter
import com.example.graduationproject.databinding.ActivityProductBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.listeners.CommentListener
import com.example.graduationproject.model.comments.ProductComment
import com.example.graduationproject.model.products.Comment
import com.example.graduationproject.model.products.ProductImage
import com.example.graduationproject.model.products.VisitedProduct
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.ui.bottomsheets.CommentConfigsBottomSheet
import com.example.graduationproject.viewmodel.ProductActivityViewModel
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.fragment_up_sign.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val TAG = "PlaceActivity"

class PlaceActivity : AppCompatActivity(), CommentListener {
    private lateinit var placeDetailsBinding: ActivityProductBinding
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private var placeId: Long = 0
    private lateinit var accessToken: String
    private var userId: Long = 0
    var isPlaceFavorite = false
    private var overallProductRate: Float = 0.0F
    private var userRate: Float = 0.0F
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var comments: MutableList<Comment?>? = mutableListOf()
    var userScrolled = true
    var pastVisibleItems = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    private lateinit var snapHelper: LinearSnapHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessToken = SplashActivity.getAccessToken(this).orEmpty()
        userId = SplashActivity.getUserId(this)

        placeDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_product)

        setUpToolbar()
        placeId = intent.getLongExtra("placeId", 0)

        placeDetailsBinding.addRatingToPlaceBar.setOnRatingBarChangeListener { _, rating, _ ->
            val rate = Rate(rate = rating)

            if (userRate == 0.0F) {
                addRateToPlace(rate)
            } else if (rating != userRate) {
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

        lifecycleScope.launch {
            getComments()
            getProductDetails()
        }

        placeDetailsBinding.arrowUpImageButton.setOnClickListener {
            placeDetailsBinding.commentsRecyclerView.smoothScrollToPosition(0)
            placeDetailsBinding.arrowUpImageButton.visibility = View.GONE
        }

        placeDetailsBinding.commentsRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

//                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING && layoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
//                        placeDetailsBinding.arrowUpImageButton.visibility = View.VISIBLE
//                    } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL && layoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
//                        placeDetailsBinding.arrowUpImageButton.visibility = View.VISIBLE
//                    }

                    // If scroll state is touch scroll then set userScrolled
                    // true
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        userScrolled = true
                    }

                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // Here get the child count, item count and visibleitems
                    // from layout manager
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                    // Now check if userScrolled is true and also check if
                    // the item is end then update recycler view and set
                    // userScrolled to false
                    if (userScrolled && visibleItemCount + pastVisibleItems == totalItemCount) {
                        userScrolled = false
                        updateRecyclerView()
                    }

//                    if (layoutManager.findFirstCompletelyVisibleItemPosition() < 0) {
//                        placeDetailsBinding.arrowUpImageButton.visibility = View.GONE
//                    }
                }
            }
        )

    }

    private fun updateRecyclerView() {
        lifecycleScope.launch {
            placeDetailsBinding.progressBar.visibility = View.VISIBLE
            val commentsLiveData =
                placeActivityViewModel.getProductComments(placeId.toString(), accessToken)
            commentsLiveData?.observe(this@PlaceActivity) { it ->
                placeDetailsBinding.progressBar.visibility = View.GONE
                comments?.addAll(it.orEmpty())
                commentsAdapter.notifyDataSetChanged()
            }
            placeDetailsBinding.progressBar.visibility = View.GONE
        }
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

    override fun onMoreOnCommentClicked(comment: Comment, position: Int) {
        comment.rate = overallProductRate
        val commentConfigsBottomSheet = CommentConfigsBottomSheet(this, comment, placeId, position)
        commentConfigsBottomSheet.show(supportFragmentManager, commentConfigsBottomSheet.tag)
    }

    //when user delete or add comment it will take effect immediately
    override fun onCommentModified(comment: Comment?, position: Int) {
        lifecycleScope.launch {
            //comment deleted --> updated the adapter, and delete that item
            if (comment == null) {
                commentsAdapter.removeItem(position)
                commentsAdapter.notifyItemRemoved(position)
                commentsAdapter.notifyDataSetChanged()
            }
            //comment updated
            else {
                commentsAdapter.updateItem(position, comment)
                commentsAdapter.notifyItemChanged(position, comment)
                commentsAdapter.notifyDataSetChanged()
            }
        }
    }

    private suspend fun getProductDetails() {
        val productDetailsLiveData =
            placeActivityViewModel.getProductDetails(placeId.toString(), accessToken)
        productDetailsLiveData?.observe(this@PlaceActivity) { product ->
            product?.let {
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
                        this@PlaceActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = PlaceImagesAdapter(placeId.toString(), placeImages)
                    //snapHelper.attachToRecyclerView(this)
                }

                placeDetailsBinding.commentTextView.setOnClickListener {
                    Toast.makeText(this, product.tags.toString(), Toast.LENGTH_LONG).show()
                }
            }
            Log.i(TAG, "PPPP getPlaceDetails: $product")
        }
    }

    private fun addCommentAndUpdateComments() {
        placeDetailsBinding.progressBar.visibility = View.VISIBLE
        placeDetailsBinding.addCommentFab.isEnabled = false

        val productComment = ProductComment(
            placeId = placeId,
            comment = placeDetailsBinding.placeCommentEditText.text.toString()
        )
            lifecycleScope.launch {
                val responseMessage = placeActivityViewModel.addCommentOnProduct(
                    productComment,
                    accessToken
                )
                responseMessage?.let {
//                    commentsAdapter.addComment(productComment)
//                    commentsAdapter.notifyItemInserted(0)
//                    commentsAdapter.notifyDataSetChanged()

                    placeDetailsBinding.addCommentFab.isEnabled = true
                    placeDetailsBinding.progressBar.visibility = View.GONE
                    placeDetailsBinding.placeCommentEditText.text.clear()
                    //Temp as the ProductComment is differnet from Comment
                    //it is not called
//                    runBlocking {
//                        getComments()
//                    }
//                    updateRecyclerView()
                }
            }
        dismissProgressAfterTimeOut()
    }

    private suspend fun getComments() {
        placeDetailsBinding.progressBar.visibility = View.VISIBLE
        val productCommentsLiveData = placeActivityViewModel.getProductComments(
            placeId.toString(),
            accessToken
        )
        productCommentsLiveData?.observe(this@PlaceActivity) { it ->
            placeDetailsBinding.progressBar.visibility = View.GONE
            if (it.isNullOrEmpty()) {
//                placeDetailsBinding.beTheFirstToCommentTextView.visibility =
//                    View.VISIBLE
                //be the first one to comment
            } else {
                comments = it.toMutableList()
                comments?.let { cmmnts ->
                    Log.i(TAG, "22222 getComments: ${cmmnts.size}")
                    commentsAdapter = CommentsAdapter(
                        cmmnts,
                        userId,
                        this
                    )

                    layoutManager = LinearLayoutManager(this@PlaceActivity)
                    placeDetailsBinding.commentsRecyclerView.layoutManager = layoutManager
                    placeDetailsBinding.commentsRecyclerView.adapter = commentsAdapter
                    commentsAdapter.notifyDataSetChanged()
                }

            }
        }
    }

    private suspend fun addPlaceToFavorite() {
        val responseMessage = placeActivityViewModel.addProductToFavorites(
            VisitedProduct(pid = placeId),
            accessToken
        )
        responseMessage?.let {
            //commented to work offline and faster
            // add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
            isPlaceFavorite = true
        }
    }

    private suspend fun deleteProductFromFavorite() {
        val responseMessage = placeActivityViewModel.deleteProductFromFavorites(
            placeId.toString(),
            accessToken
        )
        responseMessage?.let {
            //commented to work offline and faster
            //add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
            isPlaceFavorite = false
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
//                Toast.makeText(this@PlaceActivity, "Rate added", Toast.LENGTH_SHORT).show()
                }
            }
        }
        catch (ex : Throwable){
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
                    placeActivityViewModel.updateRatingToProduct(rate, placeId.toString(), accessToken)
                responseMessage?.let {
                    placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
//                Toast.makeText(this@PlaceActivity, "Rate updated", Toast.LENGTH_SHORT).show()
                }
            }
        }
        catch (ex : Throwable){
            placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
        }
        finally {
            placeDetailsBinding.addRatingToPlaceBar.isEnabled = true
        }

    }

    private fun dismissProgressAfterTimeOut() {
        Handler().postDelayed({
            placeDetailsBinding.progressBar.visibility = View.GONE
            placeDetailsBinding.addCommentFab.isEnabled = true
        }, Constants.TIME_OUT_SECONDS)

    }


}