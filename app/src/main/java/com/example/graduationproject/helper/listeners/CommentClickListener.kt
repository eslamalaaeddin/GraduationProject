package com.example.graduationproject.helper.listeners

import com.example.graduationproject.model.places.Comment

interface CommentClickListener {
    fun onMoreOnCommentClicked(comment: Comment)
}