package com.example.graduationproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.models.products.ProductImage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_details_item_layout.view.*


//http://127.0.0.1:3000/images/products/product.png

class PlaceImagesAdapter(
    private var productImages: List<ProductImage>
    ) :
    RecyclerView.Adapter<PlaceImagesAdapter.PlacesImagesViewHolder>() {

    inner class PlacesImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(productImage: ProductImage) {
            if (productImage.name?.isNotEmpty() == true) {
                Picasso.get().load(productImage.name).into(itemView.detail_place_image_view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesImagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.product_details_item_layout,
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