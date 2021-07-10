package com.example.graduationproject.ui.bottomsheets

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.databinding.UpdateCommentBottomSheetBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.Utils.getAccessToken
import com.example.graduationproject.helper.listeners.CommentListener
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
    private lateinit var loadingHandler: Handler

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
        accessToken = getAccessToken(requireContext()).orEmpty()
        loadingHandler = Handler(Looper.getMainLooper())

        bindingInstance.commentEditText.setText(comment.commentContent.orEmpty())
        bindingInstance.commentEditText.setSelection(bindingInstance.commentEditText.text.length)

        bindingInstance.addCommentFab.setOnClickListener {
            val commentContent = bindingInstance.commentEditText.text.trim().toString()
            if (commentContent.isNotEmpty()) {
                if (commentContent == comment.commentContent){
                    Toast.makeText(requireContext(), getString(R.string.comment_no_change), Toast.LENGTH_SHORT).show()
                }
                else{
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
                            Toast.makeText(requireContext(), getString(R.string.comment_updated), Toast.LENGTH_SHORT)
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
                }

            } else {
                Toast.makeText(requireContext(), getString(R.string.enter_comment_first), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            loadingHandler.postDelayed({
                bindingInstance.progressBar.visibility = View.GONE
            }, Constants.TIME_OUT_MILLISECONDS)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingHandler.removeCallbacksAndMessages(null)
    }

}