package com.example.graduationproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.graduationproject.R
import com.example.graduationproject.helper.Constants.BASE_USER_IMAGE_URL
import com.example.graduationproject.helper.listeners.CommentListener
import com.example.graduationproject.model.products.Comment
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.comment_item_layout.view.*

private const val TAG = "CommentsAdapter"
//private const val BASE_USER_IMAGE_URL = "http://10.0.3.2:3000/images/users/"
class CommentsAdapter(
    private val userId: Long,
    private val commentListener: CommentListener
    ) : PagedListAdapter<Comment, CommentsAdapter.CommentsHolder>(CALLBACK) {

    inner class CommentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        private val personNameTextView : TextView = itemView.findViewById(R.id.comment_person_name_text_view)
        private val personImage: CircleImageView =  itemView.findViewById(R.id.comment_person_image_view)
        private val placeRatingBar : RatingBar = itemView.findViewById(R.id.comment_place_rating_bar)
        private val commentTextView : TextView = itemView.findViewById(R.id.comment_body_text_view)
        private val moreOnCommentButton: ImageButton = itemView.findViewById(R.id.moreOnCommentButton)

        init {
            moreOnCommentButton.setOnClickListener {
                val comment = getItem(position)
                comment?.let{
                    commentListener.onMoreOnCommentClicked(comment)
                }
            }
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }


        fun bind(comment: Comment) {
            personNameTextView.text = comment.userName
                val userImageUrl = "$BASE_USER_IMAGE_URL${comment.userImage}"
                if (userImageUrl.isNotEmpty()){
                    Glide.with(itemView.context)
                        .load(userImageUrl)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(personImage)

//                    Picasso.get().load(userImageUrl).into(personImage)
                }
                else{
                    personImage.setImageResource(R.drawable.avatar)
                }
            //Rate
            if (comment.rate == null){
                placeRatingBar.visibility = View.GONE
            }
            else{
                placeRatingBar.rating = comment.rate ?: 0F
            }
            //CommentContent
            commentTextView.text = comment.commentContent

            moreOnCommentButton.visibility = if (userId == comment.userId) View.VISIBLE else View.GONE

        }

        override fun onClick(item: View?) {
            //Temp code
//            Toast.makeText(itemView.context, "$userId \n ${comments[adapterPosition].userId}", Toast.LENGTH_SHORT).show()

        }

        override fun onLongClick(item: View?): Boolean {
            return true
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.comment_item_layout,
            parent,
            false
        )

        return CommentsHolder(view)
    }


    override fun onBindViewHolder(holder: CommentsHolder, position: Int) {
        val comment = getItem(position)
        comment?.let {
            holder.bind(comment)
        }
    }

//    fun removeItem(position: Int) {
//        favoriteProducts.removeAt(position)
//        notifyItemRemoved(position)
//        notifyItemRangeChanged(position, favoriteProducts.size)
//    }

    companion object {
        val CALLBACK: DiffUtil.ItemCallback<Comment?> = object : DiffUtil.ItemCallback<Comment?>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.commentId === newItem.commentId
            }

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }

        }
    }
}