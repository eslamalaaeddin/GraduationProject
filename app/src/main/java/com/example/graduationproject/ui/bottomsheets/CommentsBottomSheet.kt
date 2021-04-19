package com.example.graduationproject.ui.bottomsheets

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.adapters.CommentsAdapter
import com.example.graduationproject.databinding.CommentsBottomSheetBinding
import com.example.graduationproject.helpers.Constants
import com.example.graduationproject.helpers.listeners.CommentListener
import com.example.graduationproject.models.comments.ProductComment
import com.example.graduationproject.models.products.Comment
import com.example.graduationproject.viewmodels.ProductActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "CommentsBottomSheet"

class CommentsBottomSheet(
    private val accessToken: String,
    private val placeId: Long,
    private val userId: Long,
    private val userName: String,
    private val userImageUrl: String

) : BottomSheetDialogFragment(),
    CommentListener {
    private lateinit var bindingInstance: CommentsBottomSheetBinding
    private val productActivityViewModel by viewModel<ProductActivityViewModel>()
    var isPlaceFavorite = false
    private var commentsAdapter: CommentsAdapter = CommentsAdapter(mutableListOf(), userId, this)
    private lateinit var layoutManager: LinearLayoutManager
    private var comments: MutableList<Comment?> = mutableListOf()
    var userScrolled = true
    var pastVisibleItems = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    private var userRate: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingInstance = DataBindingUtil.inflate(
            inflater,
            R.layout.comments_bottom_sheet,
            container,
            false
        )
        return bindingInstance.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            getComments()
            //to get always the updated rate
            getUserSpecificRate()
        }
       // initEditTextListener()
        initUpButton()
        initUpArrowImageButton()
        initScrollListener()
        initFab()
    }

    private fun initScrollListener() {
        bindingInstance.commentsRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (comments.isNotEmpty()){
                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING
                            && layoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
//                            bindingInstance.arrowUpImageButton.visibility = View.VISIBLE
                        } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                            && layoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
//                            bindingInstance.arrowUpImageButton.visibility = View.VISIBLE
                        }
                    }

                    // If scroll state is touch scroll then set userScrolled
                    // true
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        userScrolled = true
                    }

                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // Here get the child count, item count and visibleitems
                    // from layout manager
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                    if (layoutManager.findFirstCompletelyVisibleItemPosition() < 2) {
//                        bindingInstance.arrowUpImageButton.visibility = View.GONE
                    }

                    if (dy < 0) {
//                        bindingInstance.arrowUpImageButton.visibility = View.GONE
                    }
                    if (userScrolled && visibleItemCount + pastVisibleItems == totalItemCount) {
                        userScrolled = false
                        if (productActivityViewModel.commentPages != -1){
                            updateRecyclerView()
                        }
                    }

                    if (layoutManager.findFirstCompletelyVisibleItemPosition() < 0) {
//                        bindingInstance.arrowUpImageButton.visibility = View.GONE
                    }
                }
            }
        )
    }

    private fun initUpButton() {
        bindingInstance.upButtonView.setOnClickListener { dismiss() }
    }

    private fun initFab() {
        bindingInstance.addCommentFab.setOnClickListener {
            addCommentAndUpdateComments()
        }
    }

    private fun initUpArrowImageButton() {
        bindingInstance.arrowUpImageButton.setOnClickListener {
            bindingInstance.commentsRecyclerView.smoothScrollToPosition(0)
            bindingInstance.arrowUpImageButton.visibility = View.GONE
        }
    }

    private fun updateRecyclerView() {
        lifecycleScope.launch {
            dismissProgressAfterTimeOut()
            val commentsLiveData =
                productActivityViewModel.getProductComments(placeId.toString(), accessToken)
            commentsLiveData?.observe(viewLifecycleOwner) { it ->
                bindingInstance.progressBar.visibility = View.GONE
                if (!it.isNullOrEmpty()) {
                    Log.i(TAG, "ccc $it")
                    bindingInstance.emptyCommentsLayout.visibility = View.GONE
                    commentsAdapter.addCommentsPage(it.orEmpty())
                    commentsAdapter.notifyDataSetChanged()
                }
                else{
                    productActivityViewModel.commentPages = -1
                }

            }
            bindingInstance.progressBar.visibility = View.GONE
        }
    }

    private fun addCommentAndUpdateComments() {
//        dismissProgressAfterTimeOut()
        val commentString = bindingInstance.commentEditText.text.trim().toString()
        if (commentString.isEmpty()){
            Toast.makeText(requireContext(), "Enter a comment first.", Toast.LENGTH_SHORT).show()
        }
        else{
           dismissProgressAfterTimeOut()
            bindingInstance.addCommentFab.isEnabled = false

            val productComment = ProductComment(
                placeId = placeId,
                comment = bindingInstance.commentEditText.text.toString()
            )
            lifecycleScope.launch {
                val returnedComment = productActivityViewModel.addCommentOnProduct(
                    productComment,
                    accessToken
                )
                returnedComment?.let { retComment ->
                    Log.i(TAG, "555 : $retComment")
                    val commentToBeAdded = Comment(
                        commentId = retComment.commentId,
                        commentContent = productComment.comment,
                        userId = userId,
                        rate = userRate,
                        userName = userName,
                        userImage = userImageUrl,
                        time = retComment.time
                    )

                    Log.i(TAG, "commentTo: $commentToBeAdded")
                    commentsAdapter.addComment(commentToBeAdded)

                    if (comments.isEmpty()){
                        layoutManager = LinearLayoutManager(requireContext())
                        bindingInstance.commentsRecyclerView.layoutManager = layoutManager
                        bindingInstance.commentsRecyclerView.adapter = commentsAdapter
                        commentsAdapter.notifyItemInserted(0)
                        commentsAdapter.notifyDataSetChanged()
                    }

                    bindingInstance.addCommentFab.isEnabled = true
                    bindingInstance.progressBar.visibility = View.GONE
                    bindingInstance.commentEditText.text.clear()
                    bindingInstance.emptyCommentsLayout.visibility = View.GONE
                }
                if (returnedComment == null){
                    bindingInstance.addCommentFab.isEnabled = true
                    bindingInstance.progressBar.visibility = View.GONE
                }
            }
        }

       
    }

    private suspend fun getComments() {
        dismissProgressAfterTimeOut()
        val productCommentsLiveData = productActivityViewModel.getProductComments(
            placeId.toString(),
            accessToken
        )
        productCommentsLiveData?.observe(viewLifecycleOwner) { it ->
            bindingInstance.progressBar.visibility = View.GONE
            if (it.isNullOrEmpty()) {
                bindingInstance.emptyCommentsLayout.visibility = View.VISIBLE
            } else {
                bindingInstance.emptyCommentsLayout.visibility = View.GONE
                comments = it.toMutableList()
                comments?.let { cmmnts ->
                    Log.i(TAG, "22222 getComments: ${cmmnts.size}")
                    commentsAdapter = CommentsAdapter(
                        cmmnts.toMutableList(),
                        userId,
                        this
                    )
                    layoutManager = LinearLayoutManager(requireContext())
                    bindingInstance.commentsRecyclerView.layoutManager = layoutManager
                    bindingInstance.commentsRecyclerView.adapter = commentsAdapter
                    commentsAdapter.notifyDataSetChanged()
                }

            }
        }
    }

    private suspend fun getUserSpecificRate() {
        val rate = productActivityViewModel.getUserSpecificRate(
            placeId.toString(),
            accessToken
        )
        rate?.let {
            Log.i(TAG, "llll USER RATE BEFORE INITIALIZING : $userRate")
            userRate = it.rate ?: 0F
            Log.i(TAG, "llll USER RATE AFTER INITIALIZING: $userRate")
        }
    }


    override fun onMoreOnCommentClicked(comment: Comment, position: Int) {
        val commentConfigsBottomSheet = CommentConfigsBottomSheet(this, comment, placeId, position)
        commentConfigsBottomSheet.show(
            requireActivity().supportFragmentManager,
            commentConfigsBottomSheet.tag
        )
    }

    //when user delete or add comment it will take effect immediately
    override fun onCommentModified(comment: Comment?, position: Int) {
        //comment deleted
        if (comment == null) {
            commentsAdapter.removeItem(position)
            commentsAdapter.notifyItemRemoved(position)
            commentsAdapter.notifyDataSetChanged()
            if (position == 0 && commentsAdapter.itemCount == 0){
                bindingInstance.emptyCommentsLayout.visibility = View.VISIBLE
            }
        }
        //comment updated
        else {
            commentsAdapter.updateItem(position, comment)
            commentsAdapter.notifyItemChanged(position, comment)
            commentsAdapter.notifyDataSetChanged()
        }
    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            bindingInstance.progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                bindingInstance.progressBar.visibility = View.GONE
                bindingInstance.addCommentFab.isEnabled = true
            }, Constants.TIME_OUT_MILLISECONDS)
        }
    }
}