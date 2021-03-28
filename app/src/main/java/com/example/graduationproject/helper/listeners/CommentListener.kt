package com.example.graduationproject.helper.listeners

import com.example.graduationproject.model.products.Comment

interface CommentListener {
    fun onMoreOnCommentClicked(comment: Comment)
    fun onCommentModified()
    fun onProductRated()
}