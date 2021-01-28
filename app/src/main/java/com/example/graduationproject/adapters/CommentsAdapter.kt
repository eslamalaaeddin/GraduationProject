package com.example.graduationproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.helper.listeners.CommentClickListener
import com.example.graduationproject.model.places.Comment
import de.hdodenhof.circleimageview.CircleImageView

private const val TAG = "CommentsAdapter"
private const val BASE_IMAGE_URL = "http://10.0.2.2:3000/images/users/"
class CommentsAdapter(
    private val userId: Long,
    private val comments: List<Comment>,
    private val commentClickListener: CommentClickListener
    ) : RecyclerView.Adapter<CommentsAdapter.CommentsHolder>() {
    inner class CommentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        private val personNameTextView : TextView = itemView.findViewById(R.id.comment_person_name_text_view)
        private val personImage: CircleImageView =  itemView.findViewById(R.id.comment_person_image_view)
        private val placeRatingBar : RatingBar = itemView.findViewById(R.id.comment_place_rating_bar)
        private val commentTextView : TextView = itemView.findViewById(R.id.comment_body_text_view)
        private val moreOnCommentButton: ImageButton = itemView.findViewById(R.id.moreOnCommentButton)



        init {
            moreOnCommentButton.setOnClickListener {
                val comment = comments[adapterPosition]
                commentClickListener.onMoreOnCommentClicked(comment)
            }
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }


        fun bind(comment: Comment) {
            personNameTextView.text = comment.userName
            //UserImage
//            if (comment.userImage.isNullOrEmpty()){
//                personImage.setImageResource(R.drawable.ic_person_24)
//            }
//            else{
//                val userImageUrl = "$BASE_IMAGE_URL${comment.userImage}"
//                Log.i(TAG, "OOOO bind: $userImageUrl")
//                Picasso.get().load(comment.userImage).into(itemView.comment_person_image_view)
//            }
            personImage.setImageResource(R.drawable.avatar)
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
            Toast.makeText(itemView.context, "CommentClicked", Toast.LENGTH_SHORT).show()

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

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentsHolder, position: Int) {
        val comment = comments[holder.adapterPosition]
        holder.bind(comment)
    }
}