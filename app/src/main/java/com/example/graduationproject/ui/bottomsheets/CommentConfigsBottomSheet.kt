package com.example.graduationproject.ui.bottomsheets

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.graduationproject.R
import com.example.graduationproject.databinding.CommentConfigurationsBottomSheetBinding
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

class CommentConfigsBottomSheet(
    private val commentListener: CommentListener,
    private val comment: Comment,
    private val productId: Long,
    private val position: Int
) :
    BottomSheetDialogFragment() {
    private lateinit var bindingInstance: CommentConfigurationsBottomSheetBinding
    private val productActivityViewModel by viewModel<ProductActivityViewModel>()
    private lateinit var accessToken: String
    private lateinit var loadingHandler: Handler

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
        accessToken = getAccessToken(requireContext()).orEmpty()
        loadingHandler = Handler(Looper.getMainLooper())
        bindingInstance.updateCommentLayout.setOnClickListener {
            //get current comment
            //show it in a dialog box
            //showCommentUpdateDialog(comment)
            val updateCommentBottomSheet = UpdateCommentBottomSheet(commentListener, comment, productId, position)
            updateCommentBottomSheet.show(requireActivity().supportFragmentManager, updateCommentBottomSheet.tag)
            dismiss()
        }

        bindingInstance.deleteCommentLayout.setOnClickListener {
            showCommentDeleteDialog(comment)
        }

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
                loadingHandler.postDelayed({
                    progressBar.visibility = View.GONE
                }, Constants.TIME_OUT_MILLISECONDS)
            }

            lifecycleScope.launch {
                val responseMessage = productActivityViewModel.deleteCommentFromProduct(
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

    override fun onDestroy() {
        super.onDestroy()
        loadingHandler.removeCallbacksAndMessages(null)
    }




}