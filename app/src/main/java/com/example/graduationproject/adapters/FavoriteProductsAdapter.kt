package com.example.graduationproject.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.helpers.listeners.FavoriteProductClickListener
import com.example.graduationproject.models.products.FavoriteProduct
import com.example.graduationproject.ui.activities.ProductActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.favorite_product_item.view.*
import kotlinx.android.synthetic.main.home_product_item.view.add_to_favorite_image_view
import kotlinx.android.synthetic.main.home_product_item.view.home_place_name_text_view
import kotlinx.android.synthetic.main.home_product_item.view.home_rating_bar

//private const val BASE_PRODUCT_IMAGE_URL = "http://10.0.3.2:3000/images/products/"
private const val TAG = "FavoritePlacesAdapte"

class FavoriteProductsAdapter(
    private val favoriteProducts: MutableList<FavoriteProduct>?,
    private val favoriteProductClickListener: FavoriteProductClickListener
) : RecyclerView.Adapter<FavoriteProductsAdapter.FavoritePlacesViewHolder>() {

    inner class FavoritePlacesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
            itemView.addToFavoriteFrameLayout.setOnClickListener {
                val place = favoriteProducts?.get(adapterPosition)
                favoriteProductClickListener.onFavoriteIconClicked(place, adapterPosition)
            }
        }

        fun bind(product: FavoriteProduct) {
            if (product.isFavorite == true){
                itemView.add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
            }
            else{
                itemView.add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
            }
            itemView.home_rating_bar.rating = product.rating ?: 0F
            itemView.home_place_name_text_view.text = product.name
            Picasso.get().load(product.image).into(itemView.place_image_view)
        }

        override fun onClick(v: View?) {
            //Temp
            val favoriteProduct = favoriteProducts?.get(adapterPosition)
            val intent = Intent(itemView.context, ProductActivity::class.java)
            intent.putExtra("placeId", favoriteProduct?.id)
//            itemView.context.startActivity(intent)
            favoriteProductClickListener.onFavoriteProductClicked(favoriteProduct, adapterPosition)
//            itemView.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritePlacesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.favorite_product_item, parent, false)
        return FavoritePlacesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritePlacesViewHolder, position: Int) {
        val place = favoriteProducts?.get(position)
        Log.i(TAG, "SSS onBindViewHolder: CALLED")

        place.let {
            if (it != null) {
                holder.bind(it)
            }
        }
    }


    fun removeItem(position: Int) {
        favoriteProducts?.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }



    override fun getItemCount(): Int {
        if (favoriteProducts != null) {
            return favoriteProducts.size
        }
        return 0
    }
}