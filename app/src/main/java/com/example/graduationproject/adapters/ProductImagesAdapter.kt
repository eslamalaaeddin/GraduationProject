package com.example.graduationproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.model.products.ProductImage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.place_details_item_layout.view.*

//const val BASE_PLACE_IMAGE_URL = "http://10.0.0.2:3000/images/places/"
const val BASE_PLACE_IMAGE_URL = "http://10.0.3.2:3000/images/products/"

//http://127.0.0.1:3000/images/products/product.png

class PlaceImagesAdapter(
    private val placeId: String,
    private var productImages: List<ProductImage>
    ) :
    RecyclerView.Adapter<PlaceImagesAdapter.PlacesImagesViewHolder>() {

    inner class PlacesImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {
        // private val placeNameTextView : TextView = itemView.findViewById(R.id.details_place_name_text_view)
        private val placeImage: ImageView = itemView.findViewById(R.id.detail_place_image_view)
        //  private val placeRatingBar : RatingBar = itemView.findViewById(R.id.details_place_rating_bar)


        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }


        fun bind(productImage: ProductImage) {
            if (productImage.name?.isNotEmpty() == true) {
//                Picasso.get().load("${BASE_PLACE_IMAGE_URL}$placeId/${productImage.name}").into(itemView.detail_place_image_view)
                Picasso.get().load("${BASE_PLACE_IMAGE_URL}${productImage.name}").into(itemView.detail_place_image_view)
//                Picasso.get().load(productImage.name).into(itemView.detail_place_image_view)
            }

        }

        override fun onClick(item: View?) {
            //Temp code
            Toast.makeText(itemView.context, "ImageClicked", Toast.LENGTH_SHORT).show()

        }

        override fun onLongClick(item: View?): Boolean {
            return true
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesImagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.place_details_item_layout,
            parent,
            false
        ) as CardView

        return PlacesImagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productImages.size
    }

    override fun onBindViewHolder(holder: PlacesImagesViewHolder, position: Int) {
        val place = productImages[holder.adapterPosition]
        holder.bind(place)
    }
}