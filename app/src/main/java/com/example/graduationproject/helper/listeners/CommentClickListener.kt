package com.example.graduationproject.helper.listeners

import com.example.graduationproject.model.products.Comment

interface CommentClickListener {
    fun onMoreOnCommentClicked(comment: Comment)
}