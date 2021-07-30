package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.AbsListView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.adapters.RecommendedProductsAdapter
import com.example.graduationproject.cache.CachingViewModel
import com.example.graduationproject.databinding.FragmentHomeBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.Utils.getAccessToken
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.models.products.PostFavoriteProduct
import com.example.graduationproject.viewmodels.HomeFragmentViewModel
import com.example.graduationproject.viewmodels.ProductActivityViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(){
    private val homeFragmentViewModel by viewModel<HomeFragmentViewModel>()
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private lateinit var fragmentBinding: FragmentHomeBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private var recProductsAdapter: RecommendedProductsAdapter? = null
    private var accessToken = ""
    private lateinit var loadingHandler: Handler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        Log.i(TAG, "onCreateView: Home")
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accessToken = getAccessToken(requireContext()).orEmpty()
        gridLayoutManager = GridLayoutManager(context, 2)
        fragmentBinding.progressBar.visibility = View.VISIBLE
        loadingHandler = Handler(Looper.getMainLooper())

        Log.i(TAG, "RRRRR onViewCreated: RECREATED")

        getRecommendedPlaces()

        initArrowImageButton()
        initScrollListener()
    }


    private fun initArrowImageButton() {
        fragmentBinding.arrowUpImageButton.setOnClickListener {
            fragmentBinding.homePlacesRecyclerView.smoothScrollToPosition(0)
            fragmentBinding.arrowUpImageButton.visibility = View.GONE
        }
    }

    private fun initScrollListener() {
        fragmentBinding.homePlacesRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING && gridLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                    fragmentBinding.arrowUpImageButton.visibility = View.VISIBLE
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL && gridLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                    fragmentBinding.arrowUpImageButton.visibility = View.VISIBLE
                }


            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() < 2) {
                    fragmentBinding.arrowUpImageButton.visibility = View.GONE
                }
            }
        })
    }

    private fun getRecommendedPlaces() {
        try {
            lifecycleScope.launch {
                dismissProgressAfterTimeOut()
                homeFragmentViewModel.getRecommendedProductsPagedList(accessToken)
                    ?.observe(viewLifecycleOwner)
                    { recProducts ->

                        fragmentBinding.homePlacesRecyclerView.apply {
                            layoutManager = gridLayoutManager
                            recProductsAdapter = RecommendedProductsAdapter(R.layout.home_product_item)
                            recProductsAdapter?.let {
                                fragmentBinding.progressBar.visibility = View.VISIBLE
                                it.submitList(recProducts)
                                adapter = it
                                lifecycleScope.launchWhenStarted {
                                    loadingHandler.postDelayed({
                                        fragmentBinding.progressBar.visibility = View.GONE
                                    }, 750)
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
        lifecycleScope.launchWhenStarted {
            fragmentBinding.progressBar.visibility = View.VISIBLE
            loadingHandler.postDelayed({
                fragmentBinding.progressBar.visibility = View.GONE
            }, Constants.TIME_OUT_MILLISECONDS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingHandler.removeCallbacksAndMessages(null)
    }


}
