package com.example.graduationproject.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.ui.activities.ProductActivity
import com.squareup.picasso.Picasso
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
            //comedy,fantasy,drama ==> Comedy, Fantasy, Drama
            val tags = product.tags.orEmpty().splitToSequence(",").toList().joinToString(", ") { item -> item.capitalize() }
            itemView.productTagsTextView.text = tags
            Picasso.get().load(product.image).into(itemView.movie_image_view)
        }

        override fun onClick(v: View?) {
            val product = getItem(adapterPosition)
            val intent = Intent(itemView.context, ProductActivity::class.java)
            product?.let {
                intent.putExtra("productId", product.id)
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
        val product = getItem(position)
        product?.let {
            holder.bind(product)
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