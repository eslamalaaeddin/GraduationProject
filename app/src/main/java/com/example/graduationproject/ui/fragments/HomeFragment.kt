package com.example.graduationproject.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import com.example.graduationproject.adapters.RecommendedPlacesAdapter
import com.example.graduationproject.databinding.FragmentHomeBinding
import com.example.graduationproject.helpers.Constants
import com.example.graduationproject.helpers.listeners.RecommendedProductClickListener
import com.example.graduationproject.models.products.Product
import com.example.graduationproject.models.products.VisitedProduct
import com.example.graduationproject.ui.activities.ProductActivity
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodels.HomeFragmentViewModel
import com.example.graduationproject.viewmodels.ProductActivityViewModel
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(), RecommendedProductClickListener {
    private val homeFragmentViewModel by viewModel<HomeFragmentViewModel>()
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private lateinit var fragmentBinding: FragmentHomeBinding
    private lateinit var gridLayoutManager : GridLayoutManager
    private var recProductsAdapter: RecommendedPlacesAdapter? = null
    private var accessToken = ""


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
        accessToken = SplashActivity.getAccessToken(requireContext()).orEmpty()
        gridLayoutManager = GridLayoutManager(context, 2)
        fragmentBinding.progressBar.visibility = View.VISIBLE

        Log.i(TAG, "RRRRR onViewCreated: RECREATED")

        getRecommendedPlaces()

        initArrowImageButton()
        initScrollListener()
    }

    private fun initArrowImageButton(){
        fragmentBinding.arrowUpImageButton.setOnClickListener {
            fragmentBinding.homePlacesRecyclerView.smoothScrollToPosition(0)
            fragmentBinding.arrowUpImageButton.visibility = View.GONE
        }
    }

    private fun initScrollListener(){
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
                if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() < 2 ) {
                    fragmentBinding.arrowUpImageButton.visibility = View.GONE
                }
            }
        })
    }


    override fun onFavoriteIconClicked(product: Product, productPosition: Int) {
        val isPlaceFavorite = product.isFavorite == 1
        if (isPlaceFavorite) {
            lifecycleScope.launch { deletePlaceFromFavorite(product, productPosition) }
        } else {
            lifecycleScope.launch { addPlaceToFavorite(product, productPosition) }
        }
    }



    private suspend fun addPlaceToFavorite(product: Product, productPosition: Int) {
        val responseMessage = placeActivityViewModel.addProductToFavorites(
            VisitedProduct(pid = product.id),
            accessToken
        )
        responseMessage?.let {
            //this line has no effect as you can't modify a state of items of a recycler view
//            add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
            //the following two lines are meant to only change the state of a specific item
            product.isFavorite = if (product.isFavorite == 0) 1 else 0
            recProductsAdapter?.notifyItemChanged(productPosition, product)
            // getRecommendedPlaces()
        }
    }

    private suspend fun deletePlaceFromFavorite(product: Product, productPosition: Int) {
        val responseMessage = placeActivityViewModel.deleteProductFromFavorites(
            product.id.toString(),
            accessToken
        )
        responseMessage?.let {
//            add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
            // getRecommendedPlaces()
            product.isFavorite = if (product.isFavorite == 0) 1 else 0
            recProductsAdapter?.notifyItemChanged(productPosition, product)

        }
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
                            recProductsAdapter = RecommendedPlacesAdapter(R.layout.home_product_item, this@HomeFragment)
                            recProductsAdapter?.let {
                                fragmentBinding.progressBar.visibility = View.VISIBLE
                                it.submitList(recProducts)
                                adapter = it
                                lifecycleScope.launchWhenStarted {
                                    Handler().postDelayed({
                                        fragmentBinding.progressBar.visibility = View.GONE
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
        lifecycleScope.launchWhenStarted {
            fragmentBinding.progressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                fragmentBinding.progressBar.visibility = View.GONE
            }, Constants.TIME_OUT_MILLISECONDS)
        }
    }


}
