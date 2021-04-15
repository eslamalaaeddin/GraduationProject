package com.example.graduationproject.ui.bottomsheets

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.databinding.UpdateCommentBottomSheetBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.listeners.CommentListener
import com.example.graduationproject.model.comments.ProductComment
import com.example.graduationproject.model.products.Comment
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.ProductActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpdateCommentBottomSheet(
    private val commentListener: CommentListener,
    private val comment: Comment,
    private val placeId: Long,
    private val position: Int
    ) : BottomSheetDialogFragment() {
    private lateinit var bindingInstance: UpdateCommentBottomSheetBinding
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private lateinit var accessToken: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingInstance = DataBindingUtil.inflate(
            inflater,
            R.layout.update_comment_bottom_sheet,
            container,
            false
        )
        return bindingInstance.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accessToken = SplashActivity.getAccessToken(requireContext()).orEmpty()

        bindingInstance.commentEditText.setText(comment.commentContent.orEmpty())
        bindingInstance.commentEditText.setSelection(bindingInstance.commentEditText.text.length)

        bindingInstance.addCommentFab.setOnClickListener {
            val commentContent = bindingInstance.commentEditText.text.toString().trim()
            if (commentContent.isNotEmpty()) {
                dismissProgressAfterTimeOut()
                val placeComment = ProductComment(placeId, commentContent)
                lifecycleScope.launch {
                    val responseMessage = placeActivityViewModel.updateCommentOnProduct(
                        comment.commentId.toString(),
                        placeComment,
                        accessToken
                    )
                    responseMessage?.let {
                        bindingInstance.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Comment updated", Toast.LENGTH_SHORT)
                            .show()
                        //call an interface to get tell Product activity that comments has been updated
                        comment.commentContent = commentContent
                        commentListener.onCommentModified(comment, position)
                        dismiss()
                    }
                    if (responseMessage == null){
                        bindingInstance.progressBar.visibility = View.GONE
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Enter a comment first", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            Handler().postDelayed({
                bindingInstance.progressBar.visibility = View.GONE
            }, Constants.TIME_OUT_MILLISECONDS)

        }
    }

}