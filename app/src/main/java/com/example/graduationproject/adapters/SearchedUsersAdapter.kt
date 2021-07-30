package com.example.graduationproject.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.models.user.User
import com.example.graduationproject.ui.activities.ProductActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.searched_product_item.view.*
import kotlinx.android.synthetic.main.searched_user_item.view.*

private const val TAG = "SearchedProductsAdapter"

// TODO: 7/10/2021 To be REMOVED
class SearchedUsersAdapter :
    PagedListAdapter<User, SearchedUsersAdapter.SearchedUserViewHolder>(CALLBACK) {

    inner class SearchedUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(user: User) {
            itemView.userNameTextView.text = "${user.firstName} ${user.lastName}"
            val userImageUrl = "${Constants.BASE_USER_IMAGE_URL}${user.image}"
            Picasso.get().load(userImageUrl).into(itemView.movie_image_view)
        }

        override fun onClick(v: View?) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedUserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.searched_user_item, parent, false)
        return SearchedUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchedUserViewHolder, position: Int) {
        val user = getItem(position)
        user?.let {
            holder.bind(user)
        }
    }

    companion object {
        val CALLBACK: DiffUtil.ItemCallback<User?> = object : DiffUtil.ItemCallback<User?>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }

}