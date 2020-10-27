package com.example.graduationproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.graduationproject.R
import com.example.graduationproject.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var homeBinding: ActivityHomeBinding
//    private var placesAdapter = PlacesAdapter(emptyList())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       homeBinding = DataBindingUtil.setContentView(this,R.layout.activity_home)

//        val dummyList = mutableListOf<DummyPlace>()
//        dummyList.add(DummyPlace("Cairo",R.drawable.cairo_tower,3F))
//        dummyList.add(DummyPlace("Giza",R.drawable.pyramids,2F))
//        dummyList.add(DummyPlace("Alex",R.drawable.citadel,4F))
//        dummyList.add(DummyPlace("Aswan",R.drawable.aswan,5F))
//        dummyList.add(DummyPlace("Khalifa burg",R.drawable.burj,3F))
//        dummyList.add(DummyPlace("Mo'ai",R.drawable.moai,2F))
//        dummyList.add(DummyPlace("Eiffel tower",R.drawable.eiffel,4F))
//        dummyList.add(DummyPlace("Cairo",R.drawable.cairo_tower,3F))
//        dummyList.add(DummyPlace("Giza",R.drawable.pyramids,2F))
//        dummyList.add(DummyPlace("Alex",R.drawable.citadel,4F))
//        dummyList.add(DummyPlace("Aswan",R.drawable.aswan,5F))
//        dummyList.add(DummyPlace("Khalifa burg",R.drawable.burj,3F))
//        dummyList.add(DummyPlace("Mo'ai",R.drawable.moai,2F))
//        dummyList.add(DummyPlace("Eiffel tower",R.drawable.eiffel,4F))
//
//        placesAdapter = PlacesAdapter(dummyList)
//
//        homeBinding.homePlacesRecyclerView.apply {
//            //layoutManager = LinearLayoutManager(this@HomeActivity)
//            layoutManager = GridLayoutManager(this@HomeActivity,2)
//            adapter = placesAdapter
//        }
//
//        homeBinding.addPlacesFab.setOnClickListener {
//            startActivity(Intent(this,AddPlaceActivity::class.java))
//        }


    }

//
//    inner class PlacesAdapter(private var placesList: List<DummyPlace>) : RecyclerView.Adapter<PlacesAdapter.PlacesHolder>() {
//
//        inner class PlacesHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
//            private val placeNameTextView : TextView = itemView.findViewById(R.id.home_place_name_text_view)
//            private val placeImage: ImageView =  itemView.findViewById(R.id.place_image_view)
//            private val placeRatingBar : RatingBar = itemView.findViewById(R.id.home_rating_bar)
//
//
//            init {
//                itemView.setOnClickListener(this)
//                itemView.setOnLongClickListener(this)
//            }
//
//
//            fun bind(place: DummyPlace) {
//              placeNameTextView.text = place.name
//              placeImage.setImageResource(place.image)
//              placeRatingBar.rating = place.rating
//            }
//
//            override fun onClick(item: View?) {
//            //Temp code
//                val placeDetailsIntent = Intent(this@HomeActivity,PlaceDetailsActivity::class.java)
//                startActivity(placeDetailsIntent)
//
//            }
//
//            override fun onLongClick(item: View?): Boolean {
//                return true
//            }
//
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesHolder {
//            val view = LayoutInflater.from(parent.context).inflate(
//                R.layout.home_place_item,
//                parent,
//                false
//            ) as CardView
//
//            return PlacesHolder(view)
//        }
//
//        override fun getItemCount(): Int {
//            return placesList.size
//        }
//
//        override fun onBindViewHolder(holder: PlacesHolder, position: Int) {
//            val place = placesList[holder.adapterPosition]
//            holder.bind(place)
//        }
//    }
}