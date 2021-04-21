package com.example.graduationproject.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.helpers.Constants.BASE_PRODUCT_IMAGE_URL
import com.example.graduationproject.helpers.listeners.RecommendedProductClickListener
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.ui.activities.ProductActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_product_item.view.*

private const val TAG = "RecommendedPlacesAdapte"
class RecommendedPlacesAdapter(
    private val itemLayout : Int,
    private val recommendedProductClickListener: RecommendedProductClickListener? = null
    ) :
    PagedListAdapter<Product, RecommendedPlacesAdapter.RecommendedPlacesViewHolder>(CALLBACK) {

    constructor(itemLayout: Int) : this(itemLayout, null)

        inner class RecommendedPlacesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

            init {
                itemView.setOnClickListener(this)
                itemView.add_to_favorite_image_view.setOnClickListener {
                    val place = getItem(adapterPosition)
                    place?.let {
                        recommendedProductClickListener?.onFavoriteIconClicked(place, adapterPosition)
                    }
                }
            }


            fun bind(product: Product){
                itemView.home_place_name_text_view.text = product.name
//                overallProductRate = rate?.toFloat() ?: 0F
                Log.i(TAG, "PPPP bind: $product")

                itemView.home_rating_bar.rating = product.rating ?: 0F
//                val placeImageUrl = "$BASE_IMAGE_URL${product.id}/${product.image}"
                val placeImageUrl = "$BASE_PRODUCT_IMAGE_URL/${product.image}"
                Log.i(TAG, "bind: $placeImageUrl")
//                Picasso.get().load(product.image).into(itemView.place_image_view)
                if (product.isFavorite == 1){
                    itemView.add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
                }
                else{
                    itemView.add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
                }
            }

            override fun onClick(v: View?) {
                //Temp
                val place = getItem(adapterPosition)
                val intent = Intent(itemView.context, ProductActivity::class.java)
                place?.let {
                    intent.putExtra("placeId", place.id)
                    itemView.context.startActivity(intent)
                }

            }
        }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedPlacesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(itemLayout, parent, false)
        return RecommendedPlacesViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendedPlacesViewHolder, position: Int) {
        val place = getItem(position)
        place?.let {
            holder.bind(place)
        }
    }



    companion object {
        val CALLBACK: DiffUtil.ItemCallback<Product?> = object : DiffUtil.ItemCallback<Product?>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }

}