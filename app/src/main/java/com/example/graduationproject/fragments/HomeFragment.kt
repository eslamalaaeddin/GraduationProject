package com.example.graduationproject.fragments

import com.example.graduationproject.activities.PlaceDetailsActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.DummyPlace
import com.example.graduationproject.R
import com.example.graduationproject.databinding.FragmentHomeBinding

private const val TAG = "HomeFragment"
class HomeFragment : Fragment() {
    private lateinit var fragmentBinding: FragmentHomeBinding
        private var placesAdapter = PlacesAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,container,false)
       // (activity as AppCompatActivity?)?.setSupportActionBar(fragmentBinding.mainToolbar)
        Log.i(TAG, "onCreateView: Home")
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        fragmentBinding.homePlacesRecyclerView.apply {
            //layoutManager = LinearLayoutManager(this@HomeActivity)
            layoutManager = GridLayoutManager(context,2)
            adapter = placesAdapter
        }

//        homeBinding.addPlacesFab.setOnClickListener {
//            startActivity(Intent(context, AddPlaceActivity::class.java))
//        }


    }



    inner class PlacesAdapter(private var placesList: List<DummyPlace>) : RecyclerView.Adapter<PlacesAdapter.PlacesHolder>() {

        inner class PlacesHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
            private val placeNameTextView : TextView = itemView.findViewById(R.id.home_place_name_text_view)
            private val placeImage: ImageView =  itemView.findViewById(R.id.place_image_view)
            private val placeRatingBar : RatingBar = itemView.findViewById(R.id.home_rating_bar)


            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }


            fun bind(place: DummyPlace) {
              placeNameTextView.text = place.name
              placeImage.setImageResource(place.image)
              placeRatingBar.rating = place.rating
            }

            override fun onClick(item: View?) {
            //Temp code
                val placeDetailsIntent = Intent(context, PlaceDetailsActivity::class.java)
                startActivity(placeDetailsIntent)

            }

            override fun onLongClick(item: View?): Boolean {
                return true
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.home_place_item,
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
}