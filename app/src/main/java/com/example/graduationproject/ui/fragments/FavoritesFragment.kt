package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.AbsListView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.graduationproject.R
import com.example.graduationproject.adapters.FavoriteProductsAdapter
import com.example.graduationproject.databinding.FragmentFavoritesBinding
import com.example.graduationproject.helper.Constants.TIME_OUT_SECONDS
import com.example.graduationproject.helper.listeners.FavoriteProductClickListener
import com.example.graduationproject.model.products.FavoriteProduct
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.ProductActivityViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "FavoritesFragment"

class FavoritesFragment : Fragment(), FavoriteProductClickListener {
    private lateinit var fragmentFavoritesBinding: FragmentFavoritesBinding
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private lateinit var accessToken: String
    private var favoriteProducts: MutableList<FavoriteProduct>? = mutableListOf()
    private lateinit var favoriteProductsAdapter: FavoriteProductsAdapter
    private var currentRecProducts: PagedList<FavoriteProduct>? = null
    private lateinit var gridLayoutManager: GridLayoutManager
    var userScrolled = true
    var pastVisibleItems = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentFavoritesBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_favorites,
            container,
            false
        )
        return fragmentFavoritesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accessToken = SplashActivity.getAccessToken(requireContext()).orEmpty()

        getFavoriteProducts()

        fragmentFavoritesBinding.favoriteProductsRecyclerView.addOnScrollListener(
            object : OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    // If scroll state is touch scroll then set userScrolled
                    // true
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        userScrolled = true
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // Here get the child count, item count and visibleitems
                    // from layout manager
                    visibleItemCount = gridLayoutManager.childCount
                    totalItemCount = gridLayoutManager.itemCount
                    pastVisibleItems = gridLayoutManager.findFirstVisibleItemPosition()

                    // Now check if userScrolled is true and also check if
                    // the item is end then update recycler view and set
                    // userScrolled to false
                    if (userScrolled && visibleItemCount + pastVisibleItems == totalItemCount) {
                        userScrolled = false
                        updateRecyclerView()
                    }
                }
            }
        )
    }

    private fun updateRecyclerView() {
            lifecycleScope.launch {
               fragmentFavoritesBinding.progressBar.visibility = View.VISIBLE
                val favoritePlacesLiveData = placeActivityViewModel.getFavoriteProducts(accessToken)
                favoritePlacesLiveData?.observe(viewLifecycleOwner) { favProducts ->
                    fragmentFavoritesBinding.noFavoriteProductsTextView.visibility = View.GONE
                    favoriteProducts?.addAll(favProducts.orEmpty())
                    favoriteProductsAdapter.notifyDataSetChanged()
                }
                fragmentFavoritesBinding.progressBar.visibility = View.GONE
            }
    }


    private fun getFavoriteProducts() {
        lifecycleScope.launch {
            fragmentFavoritesBinding.progressBar.visibility = View.VISIBLE
            val favoritePlacesLiveData = placeActivityViewModel.getFavoriteProducts(accessToken)

            favoritePlacesLiveData?.observe(viewLifecycleOwner) { favProducts ->
                fragmentFavoritesBinding.progressBar.visibility = View.GONE
                favoriteProducts = favProducts
                favProducts.let {

                    if (favProducts.isNullOrEmpty()) {
                        fragmentFavoritesBinding.noFavoriteProductsTextView.visibility =
                            View.VISIBLE
                    }

                    else {
                        fragmentFavoritesBinding.noFavoriteProductsTextView.visibility = View.GONE
                        favoriteProductsAdapter = FavoriteProductsAdapter(
                            favoriteProducts,
                            this@FavoritesFragment
                        )
                        gridLayoutManager = GridLayoutManager(context, 2)
                        fragmentFavoritesBinding.favoriteProductsRecyclerView.layoutManager = gridLayoutManager
                        fragmentFavoritesBinding.favoriteProductsRecyclerView.adapter = favoriteProductsAdapter
                        favoriteProductsAdapter.notifyDataSetChanged()
                    }
                }

            }
        }
    }

//    private fun getFavoriteProducts() {
//        lifecycleScope.launch {
//            fragmentFavoritesBinding.progressBar.visibility = View.VISIBLE
//
//            placeActivityViewModel.getFavoriteProductsPagedList(accessToken)
//                ?.observe(viewLifecycleOwner)
//                { recProducts ->
//                    currentRecProducts = recProducts
//                    fragmentFavoritesBinding.progressBar.visibility = View.GONE
//                    fragmentFavoritesBinding.favoriteProductsRecyclerView.apply {
//                        layoutManager = GridLayoutManager(context, 2)
//                        favoriteProductsAdapter = FavoriteProductsAdapter(this@FavoritesFragment)
//                        favoriteProductsAdapter.let {
//                            it.submitList(recProducts)
//                            adapter = it
//                        }
//                    }
//                }
//            dismissProgressAfterTimeOut()
//        }
//    }

    override fun onFavoriteIconClicked(favoriteProduct: FavoriteProduct?, productPosition: Int) {
        lifecycleScope.launch {
            removeProductOffline(productPosition)
//            fragmentFavoritesBinding.progressBar.visibility = View.VISIBLE

            val responseMessage = placeActivityViewModel.deleteProductFromFavorites(
                favoriteProduct?.id.toString(),
                accessToken
            )
            responseMessage?.let {
//                    it.dataSource.invalidate()
//                    favoriteProduct?.id = null
//                    favoriteProductsAdapter.notifyItemChanged(productPosition, null)
//                    favoriteProductsAdapter.notifyDataSetChanged()
//                     fragmentFavoritesBinding.progressBar.visibility = View.GONE
            }
        }
    }


    private fun removeProductOffline(productPosition: Int) {
            favoriteProductsAdapter.removeItem(position = productPosition)
            favoriteProductsAdapter.notifyItemRemoved(productPosition)
            favoriteProductsAdapter.notifyDataSetChanged()

    }

    private fun dismissProgressAfterTimeOut() {
        Handler().postDelayed({
            fragmentFavoritesBinding.progressBar.visibility = View.GONE
        }, TIME_OUT_SECONDS)

    }

}