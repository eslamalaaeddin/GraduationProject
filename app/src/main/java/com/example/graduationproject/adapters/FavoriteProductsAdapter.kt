package com.example.graduationproject.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.helper.listeners.FavoritePlaceClickListener
import com.example.graduationproject.model.products.FavoriteProduct
import com.example.graduationproject.ui.activities.PlaceActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_place_item.view.*
private const val BASE_IMAGE_URL = "http://10.0.3.2:3000/images/places/"
private const val TAG = "FavoritePlacesAdapte"
class FavoritePlacesAdapter(
    private val favoriteProducts: List<FavoriteProduct>,
    private val favoritePlaceClickListener: FavoritePlaceClickListener
) :
    RecyclerView.Adapter<FavoritePlacesAdapter.FavoritePlacesViewHolder>() {

    inner class FavoritePlacesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

        init {
            itemView.setOnClickListener(this)
            itemView.add_to_favorite_image_view.setOnClickListener {
                val place = favoriteProducts[adapterPosition]
                favoritePlaceClickListener.onFavoriteIconClicked(place)
            }
        }

        fun bind(product: FavoriteProduct){
            itemView.add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
            itemView.home_place_name_text_view.text = product.name
            val placeImageUrl = "$BASE_IMAGE_URL${product.id}/${product.image}"
            Picasso.get().load(placeImageUrl).into(itemView.place_image_view)
        }

        override fun onClick(v: View?) {
            //Temp
            val place = favoriteProducts[adapterPosition]
            val intent = Intent(itemView.context, PlaceActivity::class.java)
            intent.putExtra("placeId", place.id)
            itemView.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritePlacesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.home_place_item, parent, false)
        return FavoritePlacesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritePlacesViewHolder, position: Int) {
        val place = favoriteProducts[holder.adapterPosition]
        holder.bind(place)
    }

    override fun getItemCount(): Int = favoriteProducts.size
}