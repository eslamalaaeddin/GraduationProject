package com.example.graduationproject.helper.listeners

import com.example.graduationproject.models.products.Comment

interface CommentListener {
    fun onMoreOnCommentClicked(comment: Comment, position: Int)
    fun onCommentModified(comment: Comment? = null, position: Int)
//    fun onProductRated()
}