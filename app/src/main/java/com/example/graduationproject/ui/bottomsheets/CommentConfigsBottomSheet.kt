package com.example.graduationproject.ui.bottomsheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.databinding.CommentConfigurationsBottomSheetBinding
import com.example.graduationproject.helper.listeners.CommentListener
import com.example.graduationproject.model.comments.ProductComment
import com.example.graduationproject.model.products.Comment
import com.example.graduationproject.model.rating.Rate
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.ProductActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CommentConfigsBottomSheet(private val commentListener: CommentListener, private val comment: Comment, private val placeId: Long) :
    BottomSheetDialogFragment() {
    private lateinit var bindingInstance: CommentConfigurationsBottomSheetBinding
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private lateinit var accessToken: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingInstance = DataBindingUtil.inflate(
            inflater,
            R.layout.comment_configurations_bottom_sheet,
            container,
            false
        )


        return bindingInstance.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accessToken = SplashActivity.getAccessToken(requireContext()).orEmpty()

        bindingInstance.updateCommentLayout.setOnClickListener {
            //get current comment
            //show it in a dialog box
            showCommentUpdateDialog(comment)
        }

        bindingInstance.deleteCommentLayout.setOnClickListener {
            showCommentDeleteDialog(comment)
        }

//        bindingInstance.ratePlaceLayout.setOnClickListener {
//            showRateProductDialog(comment)
//        }
    }

    private fun showCommentUpdateDialog(comment: Comment) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.update_comment_dialog)

        val updateButton = dialog.findViewById(R.id.updateCommentButton) as Button
        val cancelButton = dialog.findViewById(R.id.cancelUpdateCommentButton) as Button
        val updateCommentEditText = dialog.findViewById(R.id.updateCommentEditText) as EditText

        updateCommentEditText.setText(comment.commentContent.orEmpty())
        updateCommentEditText.setSelection(updateCommentEditText.text.length)

        updateButton.setOnClickListener {
            val commentContent = updateCommentEditText.text.toString().trim()
            if (commentContent.isNotEmpty()) {
                val placeComment = ProductComment(placeId, commentContent)
                lifecycleScope.launch {
                    val responseMessage = placeActivityViewModel.updateCommentOnProduct(
                        comment.commentId.toString(),
                        placeComment,
                        accessToken
                    )
                    responseMessage?.let {
                        Toast.makeText(requireContext(), "Comment updated", Toast.LENGTH_SHORT).show()
                        //call an interface to get tell Product activity that comments has been updated
                        commentListener.onCommentModified(commentContent)
                        dialog.dismiss()
                        dismiss()
                    }
                }
            }
            else{
                Toast.makeText(requireContext(), "Enter a comment first", Toast.LENGTH_SHORT).show()
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun showCommentDeleteDialog(comment: Comment) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.delete_comment_dialog)

        val deleteButton = dialog.findViewById(R.id.deleteCommentButton) as Button
        val cancelButton = dialog.findViewById(R.id.cancelDeleteCommentButton) as Button


        deleteButton.setOnClickListener {
            lifecycleScope.launch {
                val responseMessage = placeActivityViewModel.deleteCommentFromProduct(
                    comment.commentId.toString(),
                    accessToken
                )
                responseMessage?.let {
                    Toast.makeText(requireContext(), "Comment deleted", Toast.LENGTH_SHORT).show()
                    commentListener.onCommentModified()
                    dialog.dismiss()
                    dismiss()
                }
            }

        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

//    private fun showRateProductDialog(comment: Comment) {
//        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(true)
//        dialog.setContentView(R.layout.rate_place_dialog)
//
//        val submitButton = dialog.findViewById(R.id.submitRateButton) as Button
//        val cancelButton = dialog.findViewById(R.id.cancelRateButton) as Button
//        val ratingBar = dialog.findViewById(R.id.addRatingView) as RatingBar
//
//        comment.rate?.let { ratingBar.rating = it }
//        Toast.makeText(requireContext(), "${ratingBar.rating}", Toast.LENGTH_SHORT).show()
//
//        submitButton.setOnClickListener {
//            val rate = Rate(ratingBar.rating.toInt())
//            //Post or Put
//            if (comment.rate == null) {
//                lifecycleScope.launch {
//                    val responseMessage =
//                        placeActivityViewModel.addRatingToProduct(rate, placeId.toString(), accessToken)
//                    responseMessage?.let {
//                        Toast.makeText(requireContext(), "Rate added", Toast.LENGTH_SHORT).show()
//                        commentListener.onProductRated()
//                        dialog.dismiss()
//                        dismiss()
//                    }
//                }
//            }
//            else{
//                lifecycleScope.launch {
//                    val responseMessage =
//                        placeActivityViewModel.updateRatingToProduct(rate, placeId.toString(), accessToken)
//                    responseMessage?.let {
//                        Toast.makeText(requireContext(), "Rate updated", Toast.LENGTH_SHORT).show()
//                        commentListener.onProductRated()
//                        dialog.dismiss()
//                        dismiss()
//                    }
//                }
//            }
//
//        }
//        cancelButton.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
//
//    }

}