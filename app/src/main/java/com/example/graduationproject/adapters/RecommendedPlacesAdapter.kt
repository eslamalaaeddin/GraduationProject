package com.example.graduationproject.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.helper.listeners.RecommendedPlaceClickListener
import com.example.graduationproject.model.places.Place
import com.example.graduationproject.ui.activities.PlaceActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_place_item.view.*
private const val BASE_IMAGE_URL = "http://10.0.3.2:3000/images/places/"
private const val TAG = "RecommendedPlacesAdapte"
const val imageLocation = "https://st2.depositphotos.com/3974537/10978/v/600/depositphotos_109787082-stock-video-pyramids-at-night-with-moon.jpg"
class RecommendedPlacesAdapter(
    private val recommendedPlaces: List<Place>,
    private val recommendedPlaceClickListener: RecommendedPlaceClickListener
    ) :
    RecyclerView.Adapter<RecommendedPlacesAdapter.RecommendedPlacesViewHolder>() {

        inner class RecommendedPlacesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

            init {
                itemView.setOnClickListener(this)
                itemView.add_to_favorite_image_view.setOnClickListener {
                    val place = recommendedPlaces[adapterPosition]
                    recommendedPlaceClickListener.onFavoriteIconClicked(place)
                }
            }

            fun bind(place: Place){
                itemView.home_place_name_text_view.text = place.name
                itemView.home_rating_bar.rating = place.rating?.toFloat() ?: 0F
//                val placeImageUrl = "$BASE_IMAGE_URL${place.id}/${place.image}"
                val placeImageUrl = "$BASE_IMAGE_URL${place.id}/${place.image}"
                Log.i(TAG, "bind: $placeImageUrl")
                Picasso.get().load(placeImageUrl).into(itemView.place_image_view)
                if (place.isFavorite == 1){
                    itemView.add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
                }
                else{
                    itemView.add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
                }
            }

            override fun onClick(v: View?) {
                //Temp
                val place = recommendedPlaces[adapterPosition]
                val intent = Intent(itemView.context, PlaceActivity::class.java)
                intent.putExtra("placeId", place.id)
                itemView.context.startActivity(intent)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedPlacesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.home_place_item, parent, false)
        return RecommendedPlacesViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendedPlacesViewHolder, position: Int) {
        val place = recommendedPlaces[holder.adapterPosition]
        holder.bind(place)
    }

    override fun getItemCount(): Int = recommendedPlaces.size
}