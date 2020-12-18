package com.example.graduationproject.ui.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.DummyComment
import com.example.graduationproject.DummyPlace
import com.example.graduationproject.R
import com.example.graduationproject.databinding.ActivityPlaceDetailsBinding

class PlaceDetailsActivity : AppCompatActivity() {
    private lateinit var placeDetailsBinding:ActivityPlaceDetailsBinding
    private var placesAdapter = PlacesAdapter(emptyList())
    private var commentsAdapter = CommentsAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        placeDetailsBinding = DataBindingUtil.setContentView(this,R.layout.activity_place_details)

        setUpToolbar()
        val dummyList = mutableListOf<DummyPlace>()
        dummyList.add(DummyPlace("Cairo",R.drawable.cairo_tower,3F))
        dummyList.add(DummyPlace("Giza",R.drawable.pyramids,2F))
        dummyList.add(DummyPlace("Alex",R.drawable.citadel,4F))
        dummyList.add(DummyPlace("Aswan",R.drawable.aswan,5F))
        dummyList.add(DummyPlace("Khalifa burg",R.drawable.burj,3F))
        dummyList.add(DummyPlace("Mo'ai",R.drawable.moai,2F))
        dummyList.add(DummyPlace("Eiffel tower",R.drawable.eiffel,4F))
        dummyList.add(DummyPlace("Cairo",R.drawable.cairo_tower,3F))
        dummyList.add(DummyPlace("Giza",R.drawable.pyramids,2F))
        dummyList.add(DummyPlace("Alex",R.drawable.citadel,4F))
        dummyList.add(DummyPlace("Aswan",R.drawable.aswan,5F))
        dummyList.add(DummyPlace("Khalifa burg",R.drawable.burj,3F))
        dummyList.add(DummyPlace("Mo'ai",R.drawable.moai,2F))
        dummyList.add(DummyPlace("Eiffel tower",R.drawable.eiffel,4F))
        placesAdapter = PlacesAdapter(dummyList)

        placeDetailsBinding.placeImagesRecyclerView.apply {
            //layoutManager = LinearLayoutManager(this@HomeActivity)
            layoutManager = LinearLayoutManager(this@PlaceDetailsActivity,LinearLayoutManager.HORIZONTAL,false)
            adapter = placesAdapter
        }

        val dummyComments = mutableListOf<DummyComment>()
        dummyComments.add(DummyComment("islam",R.drawable.ic_person_24,4F,"Good place"))
        dummyComments.add(DummyComment("Ahmed",R.drawable.ic_person_24,3F,"Nice place"))
        dummyComments.add(DummyComment("Sultan",R.drawable.ic_person_24,3F,"Great place"))
        dummyComments.add(DummyComment("Hesham",R.drawable.ic_person_24,5F,"Fabulous!"))
        dummyComments.add(DummyComment("John",R.drawable.ic_person_24,5F,"عاش يرجولة"))
        dummyComments.add(DummyComment("Frank",R.drawable.ic_person_24,5F,"شغل عالي"))
        dummyComments.add(DummyComment("Kevin",R.drawable.ic_person_24,5F,"تسلم إيديكو"))


        commentsAdapter = CommentsAdapter(dummyComments)

        placeDetailsBinding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PlaceDetailsActivity)
            adapter = commentsAdapter
        }


//        placeDetailsBinding.addPlacesFab.setOnClickListener {
//            startActivity(Intent(this,AddPlaceActivity::class.java))
//        }

        placeDetailsBinding.placeCommentEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("RestrictedApi")
            override fun afterTextChanged(text: Editable?) {
               if (text.toString().isEmpty()){
                   placeDetailsBinding.submitComment.visibility = View.GONE
               }
                else{
                   placeDetailsBinding.submitComment.visibility = View.VISIBLE
               }
            }
        })

    }

    private fun setUpToolbar(){
        setSupportActionBar(placeDetailsBinding.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        placeDetailsBinding.mainToolbar.setTitleTextColor(Color.WHITE)
        placeDetailsBinding.mainToolbar.setSubtitleTextColor(Color.WHITE)
        placeDetailsBinding.mainToolbar.overflowIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)
        placeDetailsBinding.mainToolbar.navigationIcon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)
    }


    inner class PlacesAdapter(private var placesList: List<DummyPlace>) : RecyclerView.Adapter<PlacesAdapter.PlacesHolder>() {

        inner class PlacesHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
           // private val placeNameTextView : TextView = itemView.findViewById(R.id.details_place_name_text_view)
            private val placeImage: ImageView =  itemView.findViewById(R.id.detail_place_image_view)
          //  private val placeRatingBar : RatingBar = itemView.findViewById(R.id.details_place_rating_bar)


            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }


            fun bind(place: DummyPlace) {
               // placeNameTextView.text = place.name
                placeImage.setImageResource(place.image)
               // placeRatingBar.rating = place.rating
            }

            override fun onClick(item: View?) {
                //Temp code
                Toast.makeText(this@PlaceDetailsActivity, "Clicked", Toast.LENGTH_SHORT).show()

            }

            override fun onLongClick(item: View?): Boolean {
                return true
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.place_details_item_layout,
                parent,
                false
            ) as CardView

            return PlacesHolder(view)
        }

        override fun getItemCount(): Int {
            return placesList.size
        }

        override fun onBindViewHolder(holder: PlacesHolder, position: Int) {
            val place = placesList[holder.adapterPosition]
            holder.bind(place)
        }
    }

    inner class CommentsAdapter(private var commentsList: List<DummyComment>) : RecyclerView.Adapter<CommentsAdapter.CommentsHolder>() {

        inner class CommentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
            private val personNameTextView : TextView = itemView.findViewById(R.id.comment_person_name_text_view)
            private val personImage: ImageView =  itemView.findViewById(R.id.comment_person_image_view)
            private val placeRatingBar : RatingBar = itemView.findViewById(R.id.comment_place_rating_bar)
            private val commentTextView : TextView = itemView.findViewById(R.id.comment_body_text_view)


            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }


            fun bind(comment: DummyComment) {
                personNameTextView.text = comment.userName
                personImage.setImageResource(R.drawable.ic_person_24)
                placeRatingBar.rating = comment.rating
                commentTextView.text = comment.comment
            }

            override fun onClick(item: View?) {
                //Temp code
                Toast.makeText(this@PlaceDetailsActivity, "Clicked", Toast.LENGTH_SHORT).show()

            }

            override fun onLongClick(item: View?): Boolean {
                return true
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.comment_item_layout,
                parent,
                false
            )

            return CommentsHolder(view)
        }

        override fun getItemCount(): Int {
            return commentsList.size
        }

        override fun onBindViewHolder(holder: CommentsHolder, position: Int) {
            val comment = commentsList[holder.adapterPosition]
            holder.bind(comment)
        }
    }
}