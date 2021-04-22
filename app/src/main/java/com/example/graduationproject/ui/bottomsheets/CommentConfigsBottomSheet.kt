package com.example.graduationproject.ui.bottomsheets

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.databinding.CommentConfigurationsBottomSheetBinding
import com.example.graduationproject.helpers.Constants
import com.example.graduationproject.helpers.listeners.CommentListener
import com.example.graduationproject.models.comments.ProductComment
import com.example.graduationproject.models.products.Comment
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodels.ProductActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CommentConfigsBottomSheet(
    private val commentListener: CommentListener,
    private val comment: Comment,
    private val placeId: Long,
    private val position: Int
) :
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
            //showCommentUpdateDialog(comment)
            val updateCommentBottomSheet = UpdateCommentBottomSheet(commentListener, comment, placeId, position)
            updateCommentBottomSheet.show(requireActivity().supportFragmentManager, updateCommentBottomSheet.tag)
            dismiss()
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
                        Toast.makeText(requireContext(), "Comment updated", Toast.LENGTH_SHORT)
                            .show()
                        //call an interface to get tell Product activity that comments has been updated
                        comment.commentContent = commentContent
                        commentListener.onCommentModified(comment, position)
                        dialog.dismiss()
                        dismiss()
                    }
                }
            } else {
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
        val progressBar = dialog.findViewById(R.id.progressBar) as ProgressBar


        deleteButton.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                progressBar.visibility = View.VISIBLE
                Handler().postDelayed({
                    progressBar.visibility = View.GONE
                }, Constants.TIME_OUT_MILLISECONDS)
            }

            lifecycleScope.launch {
                val responseMessage = placeActivityViewModel.deleteCommentFromProduct(
                    comment.commentId.toString(),
                    accessToken
                )
                responseMessage?.let {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.comment_deleted), Toast.LENGTH_SHORT).show()
                    commentListener.onCommentModified(position = position)
                    dialog.dismiss()
                    dismiss()
                }
                if (responseMessage == null){
                    progressBar.visibility = View.GONE
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




}