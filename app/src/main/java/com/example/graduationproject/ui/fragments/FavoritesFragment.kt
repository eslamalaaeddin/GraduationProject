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
import com.example.graduationproject.helper.Constants.TIME_OUT_MILLISECONDS
import com.example.graduationproject.helper.listeners.FavoriteProductClickListener
import com.example.graduationproject.model.products.FavoriteProduct
import com.example.graduationproject.model.products.VisitedProduct
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.ProductActivityViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "FavoritesFragment"

class FavoritesFragment : Fragment(), FavoriteProductClickListener {
    private lateinit var fragmentFavoritesBinding: FragmentFavoritesBinding
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private lateinit var accessToken: String
    private var favoriteProducts: MutableList<FavoriteProduct> = mutableListOf()
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

        //getFavoriteProducts()

        fragmentFavoritesBinding.arrowUpImageButton.setOnClickListener {
            fragmentFavoritesBinding.favoriteProductsRecyclerView.smoothScrollToPosition(0)
            fragmentFavoritesBinding.arrowUpImageButton.visibility = View.GONE
        }

        fragmentFavoritesBinding.favoriteProductsRecyclerView.addOnScrollListener(
            object : OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (favoriteProducts.isNotEmpty()) {
                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING && gridLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                            fragmentFavoritesBinding.arrowUpImageButton.visibility = View.VISIBLE
                        } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL && gridLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                            fragmentFavoritesBinding.arrowUpImageButton.visibility = View.VISIBLE
                        }
                    }

                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        userScrolled = true
                        Log.i(TAG, "onScrollStateChanged: SCROLLING")
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
                        if (placeActivityViewModel.favoritePages != -1) {
                            updateRecyclerView()
                        }

                    }

                    if (dy < 0) {
                        fragmentFavoritesBinding.arrowUpImageButton.visibility = View.GONE
                    }

                    if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() < 2) {
                        fragmentFavoritesBinding.arrowUpImageButton.visibility = View.GONE
                    }
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        getFavoriteProducts()
    }

    override fun onStop() {
        super.onStop()
        placeActivityViewModel.favoritePages = 1
    }

    private fun updateRecyclerView() {
        try {
            lifecycleScope.launch {
                fragmentFavoritesBinding.progressBar.visibility = View.VISIBLE
                val favoritePlacesLiveData = placeActivityViewModel.getFavoriteProducts(accessToken)
                favoritePlacesLiveData?.observe(viewLifecycleOwner) { favProducts ->
                    if (favProducts.isNullOrEmpty()) {
                        placeActivityViewModel.favoritePages = -1
                    } else {
                        favoriteProducts?.addAll(favProducts.orEmpty())
                        favoriteProductsAdapter.notifyDataSetChanged()
                    }
                    fragmentFavoritesBinding.noFavoriteProductsTextView.visibility = View.GONE
                    fragmentFavoritesBinding.progressBar.visibility = View.GONE
                }

            }
        } catch (ex: Throwable) {
            Log.i(TAG, "updateRecyclerView: ${ex.localizedMessage}")
            fragmentFavoritesBinding.progressBar.visibility = View.GONE
        } finally {
            dismissProgressAfterTimeOut()
        }

    }

    private fun getFavoriteProducts() {
        try {
            lifecycleScope.launch {
                fragmentFavoritesBinding.progressBar.visibility = View.VISIBLE
                dismissProgressAfterTimeOut()
                val favoritePlacesLiveData = placeActivityViewModel.getFavoriteProducts(accessToken)

                favoritePlacesLiveData?.observe(viewLifecycleOwner) { favProducts ->

                    if (favProducts != null) {
                        favoriteProducts = favProducts
                    }
                    favProducts?.let {

                        if (favProducts.isEmpty()) {
                            fragmentFavoritesBinding.noFavoriteProductsTextView.visibility =
                                View.VISIBLE
                        } else {
                            fragmentFavoritesBinding.noFavoriteProductsTextView.visibility =
                                View.GONE
                            favoriteProductsAdapter = FavoriteProductsAdapter(
                                favoriteProducts,
                                this@FavoritesFragment
                            )
                            gridLayoutManager = GridLayoutManager(context, 2)
                            fragmentFavoritesBinding.favoriteProductsRecyclerView.layoutManager =
                                gridLayoutManager
                            fragmentFavoritesBinding.favoriteProductsRecyclerView.adapter =
                                favoriteProductsAdapter
                            favoriteProductsAdapter.notifyDataSetChanged()
                        }
                    }
                    if (favProducts == null) {
                        fragmentFavoritesBinding.progressBar.visibility = View.GONE
                    }
                    fragmentFavoritesBinding.progressBar.visibility = View.GONE
                }
            }
        } catch (ex: Throwable) {
            Log.i(TAG, "getFavoriteProducts: ${ex.localizedMessage}")
            fragmentFavoritesBinding.progressBar.visibility = View.GONE
        }

    }

    private fun handleFavoriteState(productPosition: Int, favoriteProduct: FavoriteProduct?) {
        val isFavorite = favoriteProduct?.isFavorite
        //Is favorite
        if (isFavorite == true) {
            favoriteProduct.isFavorite = false
            favoriteProductsAdapter.notifyItemChanged(productPosition, favoriteProduct)
            removeProductFromFavorite(favoriteProduct)
        }
        //Not favorite
        else {
            addProductToFavorite(favoriteProduct, productPosition)
            favoriteProduct?.isFavorite = true
            favoriteProductsAdapter.notifyItemChanged(productPosition, favoriteProduct)

        }
    }

    private fun addProductToFavorite(product: FavoriteProduct?, productPosition: Int) {
        lifecycleScope.launch {
            val responseMessage = placeActivityViewModel.addProductToFavorites(
                VisitedProduct(pid = product?.id),
                accessToken
            )
            responseMessage?.let {
                favoriteProductsAdapter.notifyItemChanged(productPosition, product)
            }
            if (responseMessage == null) {

            }
        }
    }

    private fun removeProductFromFavorite(favoriteProduct: FavoriteProduct){
        lifecycleScope.launch {
            val responseMessage = placeActivityViewModel.deleteProductFromFavorites(
                favoriteProduct.id.toString(),
                accessToken
            )
            responseMessage?.let {
                //Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
            if (responseMessage == null) {

            }
        }
    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            Handler().postDelayed({
                fragmentFavoritesBinding.progressBar.visibility = View.GONE
            }, TIME_OUT_MILLISECONDS)

        }
    }

    override fun onFavoriteIconClicked(favoriteProduct: FavoriteProduct?, productPosition: Int) {
        handleFavoriteState(productPosition, favoriteProduct)
    }

}