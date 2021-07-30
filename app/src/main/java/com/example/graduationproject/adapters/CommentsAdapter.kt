package com.example.graduationproject.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.graduationproject.R
import com.example.graduationproject.helper.Constants.BASE_USER_IMAGE_URL
import com.example.graduationproject.helper.listeners.CommentListener
import com.example.graduationproject.models.products.Comment
import de.hdodenhof.circleimageview.CircleImageView

private const val TAG = "CommentsAdapter"

//private const val BASE_USER_IMAGE_URL = "http://10.0.3.2:3000/images/users/"
class CommentsAdapter(
    private var comments: MutableList<Comment?>,
    private val userId: Long,
    private val commentListener: CommentListener
) : RecyclerView.Adapter<CommentsAdapter.CommentsHolder>() {

    inner class CommentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val personNameTextView: TextView =
            itemView.findViewById(R.id.comment_person_name_text_view)
        private val personImage: CircleImageView =
            itemView.findViewById(R.id.comment_person_image_view)
        private val placeRatingBar: RatingBar = itemView.findViewById(R.id.comment_place_rating_bar)
        private val commentTextView: TextView = itemView.findViewById(R.id.comment_body_text_view)
        private val moreOnCommentButton: ImageButton =
            itemView.findViewById(R.id.moreOnCommentButton)

        init {
            moreOnCommentButton.setOnClickListener {
                val comment = comments[adapterPosition]
                comment?.let {
                    commentListener.onMoreOnCommentClicked(comment, adapterPosition)
                }
            }
        }


        fun bind(comment: Comment) {
            //User data
            personNameTextView.text = comment.userName
            val userImageUrl = "$BASE_USER_IMAGE_URL${comment.userImage}"
            if (userImageUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(userImageUrl)
                    .into(personImage)
            } else {
                personImage.setImageResource(R.drawable.avatar)
            }
            Log.i(TAG, "DDDD bind: $comment")
            //Rate
            placeRatingBar.rating = comment.rate ?: 0F
            //CommentContent
            commentTextView.text = comment.commentContent
            //To show the 3 dots to each user independently
            moreOnCommentButton.visibility =
                if (userId == comment.userId) View.VISIBLE else View.GONE
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
        val comment = comments[holder.adapterPosition]
        comment?.let {
            holder.bind(comment)
        }
    }

    fun removeItem(position: Int) {
        comments.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateItem(position: Int, comment: Comment) {
        comments[position] = comment
        notifyItemChanged(position, comment)
    }


    override fun getItemCount(): Int {
        return comments.size
    }

    fun addComment(comment: Comment) {
        val tempList = mutableListOf<Comment?>()
        tempList.add(comment)
        tempList.addAll(comments)
        comments = tempList
        notifyDataSetChanged()
    }

    //As we used manual pagination, i use this function to add each page to the current list when it becomes available
    fun addCommentsPage(pagedComments: List<Comment>) {
        val temp = comments
        comments.addAll(pagedComments)
        notifyItemRangeInserted(temp.size, pagedComments.size)
        notifyDataSetChanged()
    }
}