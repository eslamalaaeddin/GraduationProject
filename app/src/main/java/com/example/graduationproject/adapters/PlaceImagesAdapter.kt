package com.example.graduationproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.model.places.PlaceImage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.place_details_item_layout.view.*


class PlaceImagesAdapter(private var placeImages: List<PlaceImage>) : RecyclerView.Adapter<PlaceImagesAdapter.PlacesImagesViewHolder>() {

    inner class PlacesImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        // private val placeNameTextView : TextView = itemView.findViewById(R.id.details_place_name_text_view)
        private val placeImage: ImageView =  itemView.findViewById(R.id.detail_place_image_view)
        //  private val placeRatingBar : RatingBar = itemView.findViewById(R.id.details_place_rating_bar)


        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }


        fun bind(place: PlaceImage) {
            if (place.name?.isNotEmpty() == true){
                Picasso.get().load(place.name).into(itemView.detail_place_image_view)
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
        return placeImages.size
    }

    override fun onBindViewHolder(holder: PlacesImagesViewHolder, position: Int) {
        val place = placeImages[holder.adapterPosition]
        holder.bind(place)
    }
}