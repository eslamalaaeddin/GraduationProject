package com.example.graduationproject.ui.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
import com.example.graduationproject.databinding.ActivityPlaceBinding
import com.example.graduationproject.helper.listeners.CommentClickListener
import com.example.graduationproject.model.comments.PlaceComment
import com.example.graduationproject.model.places.Comment
import com.example.graduationproject.model.places.PlaceImage
import com.example.graduationproject.model.places.VisitedPlace
import com.example.graduationproject.ui.bottomsheets.CommentConfigurationsBottomSheet
import com.example.graduationproject.viewmodel.PlaceActivityViewModel
import kotlinx.android.synthetic.main.activity_place.*
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


class PlaceActivity : AppCompatActivity(), CommentClickListener {
    private lateinit var placeDetailsBinding: ActivityPlaceBinding
    private lateinit var commentsAdapter: CommentsAdapter
    private val placeActivityViewModel by viewModel<PlaceActivityViewModel>()
    private var placeId: Long = 0
    var isPlaceFavorite = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = SplashActivity.getAccessToken(this).orEmpty()
        placeDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_place)

        setUpToolbar()
        placeId = intent.getLongExtra("placeId", 0)

        lifecycleScope.launch {
            val rate = placeActivityViewModel.getUserSpecificRateToPlace(
                placeId.toString(),
                accessToken
            )
            rate?.let {
                addRatingToPlaceBar.visibility = View.VISIBLE
                placeDetailsBinding.addRatingToPlaceBar.rating = it.rate?.toFloat() ?: 0F
            }
        }

        /*
            Place Details

                {
                  "city": "c1",
                  "country": "cn1",
                  "id": 1,
                  "is_favorite": 1,
                  "lat": 0,
                  "lng": 0,
                  "name": "p1",
                  "rating": 3.3333
                }


         */
        /*
       Place Details
        lifecycleScope.launch {
            val placeDetailsLiveData = placeActivityViewModel.getPlaceDetails(placeId, accessToken)
            placeDetailsLiveData.observe(this@PlaceActivity){place ->
                place?.let {
                    placeDetailsBinding.detailsPlaceNameTextView.text = it.name
                    placeDetailsBinding.detailsPlaceRatingBar.rating = it.rating?.toFloat() ?: 0F
                }
            }
        }
        */

        placeDetailsBinding.detailsPlaceNameTextView.text = "p1"
        placeDetailsBinding.detailsPlaceRatingBar.rating = 3.3333F

        //isFavorite

        lifecycleScope.launch {
            val favoritePlacesLiveData = placeActivityViewModel.getUserFavoritePlaces(accessToken)
            favoritePlacesLiveData.observe(this@PlaceActivity){favoritePlaces ->
                val placesIds = favoritePlaces.orEmpty().map { it.id }
                isPlaceFavorite = placeId in placesIds
                if (isPlaceFavorite){
                    add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
                }
                else{
                    add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
                }
            }
        }

        add_to_favorite_image_view.setOnClickListener {
            if (isPlaceFavorite) {
                //delete from fav
                lifecycleScope.launch {
                   val responseMessage = placeActivityViewModel.deleteUserFavoritePlace( placeId.toString(), accessToken)
                   responseMessage?.let {
                       add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
                       isPlaceFavorite = false
                   }
                }

            } else {
                //add to fav
                lifecycleScope.launch {
                    val responseMessage = placeActivityViewModel.addPlaceToUserFavoritePlaces(VisitedPlace(pid = placeId), accessToken)
                    responseMessage?.let {
                        add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
                        isPlaceFavorite = true
                    }
                }

            }
            Toast.makeText(this, "$isPlaceFavorite", Toast.LENGTH_SHORT).show()
        }


//       Images
//        lifecycleScope.launch {
//           val placeImagesLiveData =  placeActivityViewModel.getPlaceImages(placeId.toString(), accessToken)
//           placeImagesLiveData.observe(this@PlaceActivity){placeImages ->
//               placeDetailsBinding.placeImagesRecyclerView.apply {
//                   layoutManager = LinearLayoutManager(this@PlaceActivity,LinearLayoutManager.HORIZONTAL,false)
//                   adapter = PlaceImagesAdapter(placeId.toString(),placeImages.orEmpty())
//               }
//           }
//        }
//        Images
        val placeImages = listOf(
            PlaceImage(name = image1),
            PlaceImage(name = image2),
            PlaceImage(name = image3),
            PlaceImage(name = image4),
            PlaceImage(name = image5),
            PlaceImage(name = image6)
        )

        val snapHelper: LinearSnapHelper = object : LinearSnapHelper() {
            override fun findTargetSnapPosition(
                layoutManager: RecyclerView.LayoutManager,
                velocityX: Int,
                velocityY: Int
            ): Int {
                val centerView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
                val position = layoutManager.getPosition(centerView)
                var targetPosition = -1
                if (layoutManager.canScrollHorizontally()) {
                    targetPosition = if (velocityX < 0) {
                        position - 1
                    } else {
                        position + 1
                    }
                }
                if (layoutManager.canScrollVertically()) {
                    targetPosition = if (velocityY < 0) {
                        position - 1
                    } else {
                        position + 1
                    }
                }
                val firstItem = 0
                val lastItem = layoutManager.itemCount - 1
                targetPosition = lastItem.coerceAtMost(targetPosition.coerceAtLeast(firstItem))
                return targetPosition
            }
        }

        placeDetailsBinding.placeImagesRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@PlaceActivity, LinearLayoutManager.HORIZONTAL, false)
            snapHelper.attachToRecyclerView(this)
            adapter = PlaceImagesAdapter(placeId.toString(), placeImages)
        }









//        /*
//         Comments
         lifecycleScope.launch {
                    val placeCommentsLiveData = placeActivityViewModel.getPlaceComments(
                        placeId.toString(),
                        1,
                        accessToken
                    )
                    placeCommentsLiveData.observe(this@PlaceActivity){ comments ->
                        placeDetailsBinding.commentsRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@PlaceActivity)
                            adapter = CommentsAdapter(
                                98136921416171,
                                comments.orEmpty(),
                                this@PlaceActivity
                            )
                        }
                    }
                }
//         */

//        val userListType = object : TypeToken<List<Comment?>?>() {}.type
//
//        val comments: List<Comment> = Gson().fromJson(Utils.jsonComments, userListType)
//
//
////        val comments = Gson().fromJson<List<Comment>>(Utils.jsonComments, Comment::class.java)
//
//        placeDetailsBinding.commentsRecyclerView.apply {
//            layoutManager = LinearLayoutManager(this@PlaceActivity)
//            adapter = CommentsAdapter(
//                userId = 98136921416171, comments = comments,
//                commentClickListener = this@PlaceActivity
//            )
//        }


        /*  lifecycleScope.launch {
              val map = hashMapOf(
                  "pid" to 1,
                  "comment" to "Good Place",
                  "time" to ""
              )
              val comment = PlaceComment(null, "Good Place", null)
              //val responseMessage = placeActivityViewModel.updateCommentOnPlace("1", comment, accessToken)

              val responseMessage = placeActivityViewModel.deleteCommentOnPlace("1", accessToken)

              if (responseMessage != null) {
                  Toast.makeText(this@PlaceActivity, "${responseMessage.message}", Toast.LENGTH_SHORT).show()
              }

          }
  */

//        placeDetailsBinding.addPlacesFab.setOnClickListener {
//            startActivity(Intent(this,AddPlaceActivity::class.java))
//        }

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
            val placeComment = PlaceComment(
                placeId = placeId,
                comment = placeDetailsBinding.placeCommentEditText.text.toString()
            )

            lifecycleScope.launch {
                val responseMessage = placeActivityViewModel.addCommentOnPlace(
                    placeComment,
                    accessToken
                )
                responseMessage?.let {
                    val placeCommentsLiveData = placeActivityViewModel.getPlaceComments(
                        placeId.toString(),
                        1,
                        accessToken
                    )
                    placeCommentsLiveData.observe(this@PlaceActivity) { comments ->
                        placeDetailsBinding.commentsRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@PlaceActivity)
                            adapter = CommentsAdapter(
                                98136921416171,
                                comments.orEmpty(),
                                this@PlaceActivity
                            )
                        }
                    }
                }
            }

        }

    }

    private fun setUpToolbar() {
        setSupportActionBar(placeDetailsBinding.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        val commentConfigsBottomSheet = CommentConfigurationsBottomSheet(comment, placeId)
        commentConfigsBottomSheet.show(supportFragmentManager, commentConfigsBottomSheet.tag)
    }

}