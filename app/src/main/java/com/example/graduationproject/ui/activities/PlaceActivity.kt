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
import com.example.graduationproject.R
import com.example.graduationproject.adapters.CommentsAdapter
import com.example.graduationproject.adapters.PlaceImagesAdapter
import com.example.graduationproject.databinding.ActivityPlaceBinding
import com.example.graduationproject.helper.Utils
import com.example.graduationproject.helper.listeners.CommentClickListener
import com.example.graduationproject.model.comments.PlaceComment
import com.example.graduationproject.model.places.Comment
import com.example.graduationproject.model.places.PlaceImage
import com.example.graduationproject.model.user.User
import com.example.graduationproject.ui.bottomsheets.CommentConfigurationsBottomSheet
import com.example.graduationproject.viewmodel.PlaceActivityViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
const val image5 =
    "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxMSEhUSEhIVFRUXFxUXFRUVFRcVFxUVFRcWFxUVFRUYHSggGBolHRUVITEhJSkrLi4uFx8zODMtNygtLisBCgoKDg0OFxAQFy0eHR0rLS0tLSstLS0tKy0tLS0tLSstLS0tLS0tKy0tLS0tLS0tKys3LS0rKystKystNysyN//AABEIAMIBAwMBIgACEQEDEQH/xAAcAAACAwEBAQEAAAAAAAAAAAABAgADBQQGBwj/xABCEAABAwIEAwQGBgcIAwAAAAABAAIRAyEEEjFBBVFhBiJxgRNUkZOh0TJCUrHS8CMkNENTweEHFBUzYnKSlBaC8f/EABkBAAMBAQEAAAAAAAAAAAAAAAABAgMEBf/EACMRAQEBAQACAgEEAwAAAAAAAAABEQIDIRIxQQQiUXETMmH/2gAMAwEAAhEDEQA/APHhSEAUWr245kUhQBNlVEQhSE8I5UAgUKbKhCYKCVCUYQhMIFEVITwwRUUhBFlREhANQWigExCCmmIURSEoLRlCUSEIQlJKEolBIwJQc5EBKUlQhchKiik0zJCUyUqbQEpXFEhVuUgPNRRRGm1Q1RMAoGraJBqaFAoSmBCICXMoHoAkIFEoJgCEIVgCMJwKwhCcBEhUSuExCBCYhPDIUIRITQgEUITKFqigoUITBqMJFSpQE6MITSHwSQryEpYgtUwgQriFU5TVKnBDyVhSqarSFKrEjgoMhCQhWFAhIKC0oq1RGhphFAIrYhSkJoRhMEypYV0IZUxpRKYBHKjCAEIgKBGFUgLCkJiiArkKkyqQrcqORac8aNUZFHBXliXKi8YWqA1GFblSlqyvI0pCBTQgApsIqtptSgKynqiRn1XVTwJcJgwFy1qcLc4dxg0qbmACHCDI+5ZGJeCVfUmObxd+S92WenCVW8K9ypIWFdkVoEKwhKSoq5SFI8K1I4KaaooFOQlcFJq1EYUSNpJghCcBdESICKIarGNVArWJsqubTVgpKpE2ubKgWrpdSSPYnhOcBAqwhK1qZ6UJwgAnaFfIpmtXTSoSqWLX4HjG0qrKjm5g0yQd13eL/W2TaTPrYcjULlcxel7UcUZiKpqMYGCAI/mvOvR378c66mW/gVSQlcFYQkK4evsEyqQmlKsrQACISzdGUtTYfMkJQLkspXopAJVZCdAhQrFe6kJ4QhSashIVa5qUhTVqYSOVxCQhTTVKJsqCkNMBWBqhCLF0kYBWsCRoV4CuJWUmLTwWAL7ASs+ivS9neKCg8OifFbT6uTaisjGYEssRCzajV6btDxIVnl8ROw8F5yqi/U31TjjcoVc5qQNUKwgCKYtUhVKECsD0kIhb8eXPoYZ1QpCilKffltIhCUhOUq5eqrCwlhOQgVnSqqEYTQlhKkVyU2TuChUkrhREhBI0SEpoQhScISlcU5SvClSspSncFW5TTKUEZUSDXTAKQma1dKTsCsCDQiFUJYwq5r1zhM0q50MXuqSqSoCjCd60sVlqXIriolqpFBaoArSEEzxVCuo0Z8E9OlNzp960sNh7eP3bLHyeb4+ovnjWVWoEeHNc5C9G+hM6cj4LIxeELLjT7uhS8fn+XqjrjPpwlRM4ILS1IEJCnQUkrSqwpXKaWFSlOlyqbRhXJYTEIJaeEhQpkpSGFSuCZCEjVlI8KxyRymhVCiMKKdpthqtAVbWq0LpiKdqdI0qwFUAUTBOGynp6rDVc1qOVGE4CliGRWAKBqqwKixdGFwhd3jOUamCfgPFPhcNndGg3PIH+a0K7WiGiYGgJ0J1PVYeXyZ6i+efyTDUKTiAS906ZGtHKxLiIWy7hbSO7UbOwc0t8twubgWBmXlv+0mI3mB+d1tPomNCPzsvP779/bp5jFxPD3tGbKDeJb3o8YuPNZ+WdRPNehgtktJHhmGn3oOqF30mh3+4SfI/nRTO6ePEY7DBroExtII8RfWOa5i1evxOEYQA5rYAjQCJ118vavPY/AOpnS02PzXb4vPOpl+2HfFjOypXNV5alc1bMnPCBarsianhy4wPz4pW4HPlUIWo/DgDJaQZdInnEbRuq8Vhie9YHeC0ydzA0M/fosP8ANLV/BluahlV72wq4WiVLggrXNVSRFKBRKQlLQVyrcrCVW4pGrURUSJuNYnITAKLpIsJgUFZQpFxgCT+deim3Dw9JkmAtfDYXIAZ7xIm3e0LjlBaZttzEK/h+D9GYiTrynSNjAkH29F3NpN0DjGlyCZB7oIiOnO4XF5vP8vU+m/Hj/NcuOwoe3MSS4AgZWEQWyYc0mQIBhY76RBggg8j1XpWU7EXA60xG14bE+wyuLHUBH0T0IAABvIgE90xIIT/T/qPj+2/R+Tx/mMgMVtDDOeYA8TsJ5rpoYIudlHmdgOa0HPYxoYLc7am4knyXd35MnpjOf5cNKvlPo8rYF5k942gnmd1M2Z4aAASbkDQHlPTmqa9O5OYG+sHruVv9lMMJL++XRECdN3E6RFolcnluT5NOf4aFHhdVgAZWJA0DmNIiTqWgHzCZ9HEiYdSPjTePufbZar2hsuZLt3MGYEjm1thm6boNxLHWBba8SLDTnI+C4L1W8jCqOxH2aM+LhN4MSLfFclLGOqZmhtMVG6g1GkWmdBM6axqvSYmhDS7KTAmBBkROhMHSdVzVaLXGTTEtGsCcrgJBOsaXk6FHzn8HjExGGxGzqYEd5voswjfvTJG646jK5Dm1KLH2sWuyyeUO389lv1KABMWFrZZiRAd0F7+BXLWFpO2Uy0HcAiIvy9qJ2Ly8VUw72zmY5sGDOxvaRbZc+ReyxVEGRzE6G4cRfKeVtOfWV51+AOaGyRz1tqu/xfqJ16rn78eOGnQLjA9uwXa1gaLC/wDPaV2HDwMrR4zuevwQp0CXAbE3mRbcE7f1Ud+T5f0JzillCL+cW8NdtEBTdJ3Bi0i8CNfCNitmph7RBB8o5WnfT2+S4XUCNoi4JyzaJBcCdrjlB8FzfPW3xY+Lwu8jx6c4CzXMXp8ThtoMeOkid9N+qysVhrmZnxB081v4/Nn2y78bIKQq6pTjZUuC6N1jityrJTPVRQMAlVOcncVS5RaeAXKISoloehbUTZlTlXThaDnuDWiT7IjUk7Bb/LJ7LD4Wg6o4NbqfYBuSdgvQCg2gyNZjMSIki8CdrKyhh20WlrYJsXONieYHSys4RSFapmJGRgbqYnkTsdp8ua4vN5t/p0cePGlgcIYBLbuBO9hcgGRYi+nMrrey0S02jLll3TUztpGq76OHBFnAxcRMEG0zqfEFK4w0EwY1aXAHQWvadvJcV6byMj0Ooy3vYNjQ2IAABXDU+tDpIlpBy23cI1A1N9I0WnigD3CRdtrkEGdQRPSxWTjH5nE9025+Jc089Cr4pWKcLj8liZaRGl2x3TprtPine/MJt5czuPgsysT3jaJkD6V4vBgkXJuEmHx+UBpcS0m2vdm4tEkfcu3xd59sOo78PSL3tY0QSY5RtJOw115r6FwvDMpNawSLEgOcZmZdE7SdQvKdlcCXVDUI7omNYLrabH+q9tTp6SHHfYc+Sn9R1tyDmD6QFsgG21pFtrrlxOCp1GyWNMi0iTldqF3QDcWPXn1VVIQYGlzBdp7dtfguWqlZVLDOpk5DIAgtdIzaAOBI5NOsquljQXZHU3McZiWywtn7Q2MjwJWq8mCfrdDqATHwvfnC4MRRIzOBI5kukNsTvoL/AAHRTWkqvEs2g6PhwiREEWOp6T9WFyv1EgNzEBuoDgWZrRpuIMXC6HPqAg7FumYRmAMwY6aTELnxEZSzKLQ5gcRYgyBrbfzHgpUy6jZEtIbkmbSGkat8NTHIdFzYqg7PcNBF8wmAJGcESCLRYzcrsxGD/SOdTPeytabw6RBbmGhJBcL/AGRpKXiBpmoSSWuLHscPpD0YY1wzfZ0Bk8yPC5SrBGMzSRa7rWnumNtRYrT4Dhi9xf35GkBwabXki06W+a4+KYGHQ0ua9rnvcC0FhYTBAePokgtsdyTsvSdkH0alPKwObUE52vs53NwH2b7feteu/wBiJParE4V+9+kOA6gwfn8s2vQcJmMp6mdxZ0Ty8F6zEUIgEG5iwJGhPegSNFi48Zc2YmMnfiTLfti1yPbrK551WmPPHMG5ZmLT3ZIGlo1Aj2nkuSsHGTvN9I1nlpZbVWkLOBDmuAJIJuDbNoDqTfYEKh1Igwd5a60je4MaSD4GFfzKx5yvRkXgW8bSdDGizK9Mt/ovUYqhAFvZtlIzDn+Qs/EUA4mQTrrI8It4rbx+XGffDzzgqHrsxNEtJsY2PP8AP8lyvXX8tjnsxVCqerXBUuSIiKiiA9Lh6Be4NaJJXqcFSp4emSBncfpEg3OkC30dwsrB8Yw2HYXNPpZMFzIMn22bZw9nNZXEe1/pHkMswNiMsknVzidvkubvyXr+nRJI2cTxEVctKkAXuOUa3PmLbmRbVe34HgRRY1jSTFyREFxuXAFtvOYXiOyjadKg7H4lwZTiGF94bJEtAkuLrAQL+a8j237e1MW70dBz6eHAjLOV1U/aqRtybPjdYXerk+lyyTX2nC8fwtWt6GnXbUqhhcWsLXy0GD3rgkGO7KONqQC4B7gAbDJJmJBkC48YX5lw2KfTcHsc5jm3a5pLXA9CNF9c7Ddtab8KKeJxIFdrnSarpL2k5muBOtjEA7aaKe/HnuHx3K3eKcQDbZXBwEjuGCZkCQCAdR/7LMxfEmuBcATchwIcDaxgEXI7110cQ4gA5rQQ9rgdXFpk8gd4MRbReVq8fYHvo1AQXPcGuDpkOsHgmA1u9zOtk+Z/xVsdlbFCPC89BF53svOY7tEcway4Gpdvb4bri4/xkOcWMJygm/Mze8/R0t0C86asmV0cseq+zdi+2bacNdekSC4aupnSRzHRfWsNXbUYHsIc0iQRoV+TuHY91M5gY+IPQr652B7YCmA1xmk7Ualh3LROkxKq877id19WfFpESbHqkqtBMkaG09RcD4JGcQouaHCqyDBBzDe41VdfidHQ1aUbzUb81lTiutSh2UCz5JHO0aeEW+SSq4xlMGdNpgRHslVVuM0Gx+mpmSB/mNsC0316FCrj2GCHsc0kiQ9pOawAaJg+HTqsrK0iqkyGmWmZkXsSQCAOUlzre1c5e1zWuMG5a4O2DQNp0kN/5dUtXiLWwKjmicoJzNAnKc4E3Jki3KLrMx3EmtGU1GAncubBdN7TfQHwss9q8cTqzm1WtEh4ZUGb6RcxpGRztZcHME3ggpuKYlhe58Nc1zRTIAhwzxYzaAdid52vU/ibHOLmuEg5YDmyAIzG3+k2851WLWxgDnRlh47zS68tBAEXAsG+I8FrNvsrkaeMrNAhx7wDWVHfbBG56H4Sd5WZhsW6i5pZUILHF0mDu51wdGkudYc+i4X4uDzjuyT9IRmmPtS0CdO8b8uCrihAvcZgTMEtMgAT0AH/AMVTlN6fXezvHqeNpkhuWo21Snmu3k5u5adQfJHE0ZP1vbJB/CV8fwXHn0K3pqLw1wm0y17b9wjcaeyV9Z7N9pKGOYXNd6N7YD2Pc0EE7tO7TCz75sPnqOTGYTI0kMcRuxkTAs4tG6j8OS0XzAwc1hmDhIcB1kHcXXfxTj9Cg/0Tg6o7LLiwNLQHbEk+B8wsfGdo6NClLcNVNJoiQWnI0m/OBBIuY00R/j7zcHzmuXEYIuluV0yCPEWtuJaSPEeCyMVQNgdwRMxeCRPk6Z6Rzinjv9oOHeR/djXpw2HfoqRBdrIl0jU9F5TFdqKzwSK8a2cxjTpsBKfPHX5K9xuYtjdCDBMa8zvPQa9Fh4mnlMQfP7is+r2gqvHeeDcHRu350XQ3jAcCHuGbK7QWNjA6HRb828sus6PKrdT3VJrNGhkfceRCZmI21W86jOwhRXe3DkicnwUT+ULHh24hwBaHENNy0EwTzhdFHiVRrSwO7psbC4vMmJ3PtXEE7Auds2OLdpcTiKdKjVeDTpf5bWtDQIGUExqQLLJLkj1JSkwGzItPVJKGZMlrr/1QSByEoCxFJKLSmFjUA4g2JHUGE4VJKdKOmnj6o0q1B4PcP5rpp8dxLRDcRVAIgjOTI1vOqy8yjnJaG3Q7UYpthVHnTpO5blmlgtXBf2iYqm0NNHCVI+u/DtD/ADcwi/WF49pUlKq2vScU7WvrkOdQpNdcy3NqdYBJhY1bGl2o9i5QUCjJpa6W4gAyC5vh/RMzFEfW9q4yUJRpNMY/lCX+++HwWdKgKehpHGnogMZ4LPJQzI08a44i8aPItsT8bphxatBaKz4cMrhndBbyIm4WKoloaRxDuYKj6p6G3xWbKYmyNDtLuiQlc7KsaifOEM6NDpz+KnpiNC4eBhcubqhnPNGljtONqH67/wDk75orhzHmggYJTNdZRpHMe0IgjmEjLCCskcx7UluY9qYQlSES4cwhA5j2oAKJrbkI25hALKakVMo5pmEJwjgqqqrrRqq6hBToimVJRIUPkpMAiSgAogIjKEKQgklSVFIQEUUChQaIIhRIIVEYQhAQKI5ShCAiikKQgIgioAgIogigP2d/hVD+BS9235JXcJw/8Cl7tnyUUUIA8Hw/q9H3TPkp/guG9Xo+6Z8lFEAruBYX1Wh7pnySHgOF9Voe5p/JRREFA8AwnquH9zT+SR3Z/CeqYf3NP8KiiqBSez2D9Uw/uaf4VW7s5g/U8P7in+FRRWIrPZvB+p4b3FP8KrHZrBep4b3FP8KiiVVCHszgrfqeG9xT/Cq6nZjA+pYb3FLkf9KiimhUey2Bn9hwv/XpfhSHsrgb/qOF/wCvS/CookSmr2WwPqWF/wCvS/CqanZjAx+xYbX+BS/CoonCVu7M4L1LDbfuKX4VH9mcFf8AUsNv+4pfhUUVQnJV7N4P1PDe4p8h/pXF/wCP4S36ph9W/uafL/agoniiHgGEgfqtD3NP5LjZwPC+rUNT+6Zy8FFEYCN4Jho/ZqGn8JnPwXDU4Ph4/Z6Pu2fJRRGHF2G4Nhpqfq9GzXR+iZ06LAqcOo2/Q09R9RvyRUQUctfAUrfoqeh+o35Kp2Cpfw2f8G9eiCiRqH4OnH+Wzf6o69FnGi37I1OwUUQasUm/ZHsCiiiQf//Z"
const val image6 =
    "https://t3.ftcdn.net/jpg/02/66/45/50/360_F_266455083_yASCBTitC7vtyI7dBL9kzk1SDXLS3m6s.jpg"


class PlaceActivity : AppCompatActivity(), CommentClickListener {
    private lateinit var placeDetailsBinding: ActivityPlaceBinding
    private lateinit var commentsAdapter: CommentsAdapter
    private val placeActivityViewModel by viewModel<PlaceActivityViewModel>()
    private var placeId: Long = 0
    var tempClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accessToken = SplashActivity.getAccessToken(this).orEmpty()
        placeDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_place)

        setUpToolbar()
        placeId = intent.getLongExtra("placeId", 0)


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

        add_to_favorite_image_view.setOnClickListener {
            if (tempClicked) {
                add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
                tempClicked = false
            } else {
                add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
                tempClicked = true
            }
            Toast.makeText(this, "$tempClicked", Toast.LENGTH_SHORT).show()
        }


        /*
        Images
        lifecycleScope.launch {
           val placeImagesLiveData =  placeActivityViewModel.getPlaceImages(placeId, accessToken)
           placeImagesLiveData.observe(this@PlaceActivity){placeImages ->
               placeDetailsBinding.placeImagesRecyclerView.apply {
                   layoutManager = LinearLayoutManager(this@PlaceActivity,LinearLayoutManager.HORIZONTAL,false)
                   adapter = PlaceImagesAdapter(placeImages.orEmpty())
               }
           }
        }
        */
        //Images
        val placeImages = listOf(
            PlaceImage(name = image1),
            PlaceImage(name = image2),
            PlaceImage(name = image3),
            PlaceImage(name = image4),
            PlaceImage(name = image5),
            PlaceImage(name = image6)
        )
        placeDetailsBinding.placeImagesRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@PlaceActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = PlaceImagesAdapter(placeImages)
        }

//        /*
//         Comments
//         lifecycleScope.launch {
//                    val placeCommentsLiveData = placeActivityViewModel.getPlaceComments(placeId.toString(), 1, accessToken)
//                    placeCommentsLiveData.observe(this@PlaceActivity){comments ->
//                        placeDetailsBinding.commentsRecyclerView.apply {
//                            layoutManager = LinearLayoutManager(this@PlaceActivity)
//                            adapter = CommentsAdapter(comments.orEmpty())
//                        }
//                    }
//                }
//         */

        val userListType = object : TypeToken<List<Comment?>?>() {}.type

        val comments: List<Comment> = Gson().fromJson(Utils.jsonComments, userListType)


//        val comments = Gson().fromJson<List<Comment>>(Utils.jsonComments, Comment::class.java)

        placeDetailsBinding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PlaceActivity)
            adapter = CommentsAdapter(
                userId = 98136921416171, comments = comments,
                commentClickListener = this@PlaceActivity
            )
        }


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
        val commentConfigsBottomSheet = CommentConfigurationsBottomSheet(comment)
        commentConfigsBottomSheet.show(supportFragmentManager, commentConfigsBottomSheet.tag)
    }

}