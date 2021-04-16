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
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.model.products.Product
import com.example.graduationproject.ui.activities.ProductActivity
import kotlinx.android.synthetic.main.home_product_item.view.*
import kotlinx.android.synthetic.main.searched_product_item.view.*

private const val TAG = "SearchedProductsAdapter"
class SearchedProductsAdapter :
    PagedListAdapter<Product, SearchedProductsAdapter.SearchedProductsViewHolder>(CALLBACK) {

    inner class SearchedProductsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(product: Product){
            itemView.productNameTextView.text = product.name
            itemView.homeRatingBar.rating = product.rating ?: 0F
            val tags = product.tags.orEmpty().replace(",", ", ")
            itemView.productTagsTextView.text = tags
//            Picasso.get().load(product.image).into(itemView.place_image_view)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedProductsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.searched_product_item, parent, false)
        return SearchedProductsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchedProductsViewHolder, position: Int) {
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