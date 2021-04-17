package com.example.graduationproject.ui.bottomsheets

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.databinding.UpdateCommentBottomSheetBinding
import com.example.graduationproject.helpers.Constants
import com.example.graduationproject.helpers.listeners.CommentListener
import com.example.graduationproject.models.comments.ProductComment
import com.example.graduationproject.models.products.Comment
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodels.ProductActivityViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

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
            val commentContent = bindingInstance.commentEditText.text.trim().toString()
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