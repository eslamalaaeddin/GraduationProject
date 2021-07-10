package com.example.graduationproject.ui.bottomsheets

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.graduationproject.R
import com.example.graduationproject.adapters.SearchedProductsAdapter
import com.example.graduationproject.adapters.SearchedUsersAdapter
import com.example.graduationproject.databinding.SearchUsersBottomSheetBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.Utils
import com.example.graduationproject.helper.Utils.getAccessToken
import com.example.graduationproject.models.user.User
import com.example.graduationproject.viewmodels.SearchFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "UsersSearchBottomSheet"

class UsersSearchBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bindingInstance: SearchUsersBottomSheetBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val searchFragmentViewModel by viewModel<SearchFragmentViewModel>()
    private var searchedUsersAdapter: SearchedUsersAdapter? = null
    private var accessToken = ""
    private lateinit var loadingHandler: Handler
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
            R.layout.search_users_bottom_sheet,
            container,
            false
        )
        return bindingInstance.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accessToken = getAccessToken(requireContext()).orEmpty()
        linearLayoutManager = LinearLayoutManager(requireContext())
        loadingHandler = Handler(Looper.getMainLooper())
//        initEditTextListener()
        val dummyList = mutableListOf<User>()
        dummyList.add(User(firstName = "A", lastName = "a", image = Utils.getUserImageUrl(requireContext())))
        dummyList.add(User(firstName = "B", lastName = "b", image = Utils.getUserImageUrl(requireContext())))
        dummyList.add(User(firstName = "C", lastName = "c", image = Utils.getUserImageUrl(requireContext())))
        dummyList.add(User(firstName = "D", lastName = "d", image = Utils.getUserImageUrl(requireContext())))
        dummyList.add(User(firstName = "E", lastName = "e", image = Utils.getUserImageUrl(requireContext())))


    }

    private fun initEditTextListener() {
        bindingInstance.searchUsersEditText.requestFocus()
        bindingInstance.searchUsersEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("RestrictedApi")
            override fun afterTextChanged(q: Editable?) {
                val query = q?.trim().toString()
                if (query.isEmpty()) {
                    //UI Razala
                    lifecycleScope.launchWhenStarted {
                        delay(750)
                        bindingInstance.emptyQueryLayout.visibility = View.VISIBLE
                        searchedUsersAdapter?.let {
                            it.submitList(null)
                        }
                    }
                    bindingInstance.searchUsersEditText.hint = getString(R.string.search_by_user_name)
                } else if (query.count() < 3) {
                    searchedUsersAdapter?.let {
                        it.submitList(null)
                    }
                } else {
                    bindingInstance.emptyQueryLayout.visibility = View.GONE
                    searchProductsByName(query)
                }
            }
        })
    }

    private fun searchProductsByName(userName: String) {
        bindingInstance.progressBar.visibility = View.VISIBLE
        try {
            lifecycleScope.launch {
//                delay(1000)
                dismissProgressAfterTimeOut()
                searchFragmentViewModel.getUsersPagedListByName(userName, accessToken)
                    ?.observe(viewLifecycleOwner)
                    { users ->
                        bindingInstance.searchedUsersRecyclerView.apply {
                            layoutManager = linearLayoutManager
                            searchedUsersAdapter = SearchedUsersAdapter()
                            searchedUsersAdapter?.let {

                                bindingInstance.progressBar.visibility = View.VISIBLE
                                it.submitList(users)
                                adapter = it

                                lifecycleScope.launchWhenStarted {
                                    loadingHandler.postDelayed({
                                        bindingInstance.progressBar.visibility = View.GONE
                                    }, 750)
                                }
                            }
                        }
                    }
            }
        } catch (ex: Throwable) {
            Log.i(TAG, "getUsers: ${ex.localizedMessage}")
        } finally {
            dismissProgressAfterTimeOut()
        }

    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            bindingInstance.progressBar.visibility = View.VISIBLE
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