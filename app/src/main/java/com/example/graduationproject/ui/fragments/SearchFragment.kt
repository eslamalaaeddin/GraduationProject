package com.example.graduationproject.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.adapters.SearchedProductsAdapter
import com.example.graduationproject.databinding.SearchFragmentBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.Utils.getAccessToken
import com.example.graduationproject.helper.listeners.TagClickListener
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodels.SearchFragmentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val TAG = "SearchFragment"
class SearchFragment : Fragment() , TagClickListener{
    private lateinit var bindingInstance: SearchFragmentBinding
    private val searchFragmentViewModel by viewModel<SearchFragmentViewModel>()
    private lateinit var linearLayoutManager : LinearLayoutManager
    private var searchedProductsAdapter: SearchedProductsAdapter? = null
    private var accessToken = ""
    private var tagSearch = false
    private var wasTagging = false
    private var theLastQuery = ""
    private lateinit var loadingHandler: Handler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingInstance = DataBindingUtil.inflate(
            inflater,
            R.layout.search_fragment,
            container,
            false
        )
        return bindingInstance.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accessToken = getAccessToken(requireContext()).orEmpty()
        linearLayoutManager = LinearLayoutManager(requireContext())
//        bindingInsta nce.progressBar.visibility = View.VISIBLE

        loadingHandler = Handler(Looper.getMainLooper())

        initArrowImageButton()

        initScrollListener()

        initEditTextListener()

        bindingInstance.filterSearchMethodImageButton.setOnClickListener {
            showTagsListDialog(this)
        }

    }

    private fun initEditTextListener() {
        bindingInstance.searchProductsEditText.requestFocus()
        bindingInstance.searchProductsEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("RestrictedApi")
            override fun afterTextChanged(q: Editable?) {
                val query = q?.trim().toString()
                theLastQuery = query

                 if ((query.isEmpty() || query.isBlank()) && !tagSearch) {
                        lifecycleScope.launchWhenStarted {
                            delay(750)
                            bindingInstance.emptyQueryLayout.visibility = View.VISIBLE
                            searchedProductsAdapter?.let {
                                it.submitList(null)
                                bindingInstance.arrowUpImageButton.visibility = View.GONE
                            }
                        }
                        bindingInstance.searchProductsEditText.hint =
                            getString(R.string.search_by_name_hint)

                }
                //As they might be movies with one char
//                else if (query.count() < 3) {
//                    searchedProductsAdapter?.let {
//                        it.submitList(null)
//                        bindingInstance.arrowUpImageButton.visibility = View.GONE
//                    }
//                }

                else {

                    lifecycleScope.launchWhenStarted {
                        //for fast typing, immediate search would lead to bad performance, so better to wait
                        delay(1000)
                        if (query.trim().isNotBlank() || query.trim().isNotEmpty()) {
                            Log.i(TAG, "88888 afterTextChanged: Empty but i will search")
                            searchProductsByName(query)
                        }
                    }
                }
            }
        })
    }

    private fun showTagsListDialog(tagClickListener: TagClickListener) {
        // setup the alert builder
        val builder = AlertDialog.Builder(requireContext(), R.style.MyDialogTheme)
        builder.setTitle(getString(R.string.tags))

        val tags = arrayOf(
            "Music",
            "Crime",
            "Mystery",
            "Western",
            "Fantasy",
            "Thriller",
            "Animation",
            "Reality-Tv",
            "Adventure",
            "Action",
            "Sport",
            "Drama",
            "Adult",
            "Sci-Fi",
            "Family",
            "Documentary",
            "History",
            "Musical", "Romance", "Biography", "Film-Noir", "News", "Comedy", "Horror", "War"
        ).sortedArray()
        builder.setItems(tags) { dialog, which ->
           tagClickListener.onTagClicked(tags[which])
        }

// create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun initArrowImageButton(){
        bindingInstance.arrowUpImageButton.setOnClickListener {
            bindingInstance.searchedProductsRecyclerView.smoothScrollToPosition(0)
            bindingInstance.arrowUpImageButton.visibility = View.GONE
        }
    }

    private fun initScrollListener(){
        bindingInstance.searchedProductsRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING &&
                    linearLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                    bindingInstance.arrowUpImageButton.visibility = View.VISIBLE
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL &&
                    linearLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                    bindingInstance.arrowUpImageButton.visibility = View.VISIBLE
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() < 2 ) {
                    bindingInstance.arrowUpImageButton.visibility = View.GONE
                }
            }
        })
    }


    override fun onTagClicked(tag: String) {
        tagSearch = true
        bindingInstance.searchProductsEditText.text.clear()
        bindingInstance.searchProductsEditText.hint = "#$tag"
        searchProductsByTag(tag)
    }

    private fun searchProductsByName(productName: String) {
        bindingInstance.progressBar.visibility = View.VISIBLE
        bindingInstance.emptyQueryLayout.visibility = View.GONE
        try {
            lifecycleScope.launch {
                dismissProgressAfterTimeOut()
                searchFragmentViewModel.getProductsPagedListByName(productName, accessToken)
                    ?.observe(viewLifecycleOwner)
                    { products ->
                        bindingInstance.searchedProductsRecyclerView.apply {
                            layoutManager = linearLayoutManager
                            searchedProductsAdapter = SearchedProductsAdapter()
                            searchedProductsAdapter?.let {

                                bindingInstance.progressBar.visibility = View.VISIBLE
                                if (theLastQuery.isEmpty() || theLastQuery.isBlank()){
                                    it.submitList(null)
                                    bindingInstance.arrowUpImageButton.visibility = View.GONE
                                    bindingInstance.emptyQueryLayout.visibility = View.VISIBLE
                                    adapter = it
                                }

                                else{
                                    it.submitList(products)
                                    adapter = it
                                }

                                lifecycleScope.launchWhenStarted {
                                    loadingHandler.postDelayed({
                                        bindingInstance.progressBar.visibility = View.GONE
                                    },750)
                                }
                            }
                        }
                    }
            }
        } catch (ex: Throwable) {
            Log.i(TAG, "getRecommendedPlaces: ${ex.localizedMessage}")
        } finally {
            dismissProgressAfterTimeOut()
        }

    }

//    private fun updateView(itemCount: Int) {
//        if (itemCount > 0){
//            Toast.makeText(requireContext(), "NOT EMPTY", Toast.LENGTH_SHORT).show()
//        }
//        else{
//            Toast.makeText(requireContext(), "EMPTY", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun searchProductsByTag(tag: String) {
        bindingInstance.progressBar.visibility = View.VISIBLE
        bindingInstance.emptyQueryLayout.visibility = View.GONE
        try {
            lifecycleScope.launch {
                dismissProgressAfterTimeOut()
                searchFragmentViewModel.getProductsPagedListByTag(tag, accessToken)
                    ?.observe(viewLifecycleOwner)
                    { products ->
                        bindingInstance.searchedProductsRecyclerView.apply {
                            layoutManager = linearLayoutManager
                            searchedProductsAdapter = SearchedProductsAdapter()
                            searchedProductsAdapter?.let {

                                bindingInstance.progressBar.visibility = View.VISIBLE
                                it.submitList(products)
                                adapter = it
                                tagSearch = false
                                lifecycleScope.launchWhenStarted {
                                    loadingHandler.postDelayed({
                                        bindingInstance.progressBar.visibility = View.GONE
                                    },750)
                                }
                            }
                        }
                    }
            }
        } catch (ex: Throwable) {
            Log.i(TAG, "getRecommendedPlaces: ${ex.localizedMessage}")
        } finally {
            dismissProgressAfterTimeOut()
        }

    }

    private fun dismissProgressAfterTimeOut() {
        bindingInstance.progressBar.visibility = View.VISIBLE
        lifecycleScope.launchWhenStarted {
            bindingInstance.progressBar.visibility = View.VISIBLE
            loadingHandler.postDelayed({
                bindingInstance.progressBar.visibility = View.GONE
            }, Constants.TIME_OUT_MILLISECONDS)
        }
    }


}