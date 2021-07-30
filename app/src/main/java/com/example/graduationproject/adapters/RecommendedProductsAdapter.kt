package com.example.graduationproject.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.helper.Constants.BASE_PRODUCT_IMAGE_URL
import com.example.graduationproject.helper.Utils
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.ui.activities.ProductActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_product_item.view.*

private const val TAG = "RecommendedPlacesAdapte"
class RecommendedProductsAdapter(private val itemLayout : Int) :
    PagedListAdapter<Product, RecommendedProductsAdapter.RecommendedPlacesViewHolder>(CALLBACK) {

        inner class RecommendedPlacesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

            init {
                itemView.setOnClickListener(this)
            }

            fun bind(product: Product){
                itemView.home_place_name_text_view.text = product.name
                Picasso.get().load(product.image).into(itemView.place_image_view)
            }

            override fun onClick(v: View?) {
                val product = getItem(adapterPosition)
                val intent = Intent(itemView.context, ProductActivity::class.java)
                val connectionState = Utils.getConnectionType(itemView.context)
                product?.let {
                    intent.putExtra("productId", product.id)
                    intent.putExtra("connectionState", connectionState)
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