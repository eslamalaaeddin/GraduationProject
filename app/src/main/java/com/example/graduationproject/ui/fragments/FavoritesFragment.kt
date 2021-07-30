package com.example.graduationproject.ui.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.graduationproject.cache.CachingViewModel
import com.example.graduationproject.databinding.FragmentFavoritesBinding
import com.example.graduationproject.helper.Constants.FAVORITE_PRODUCT_REQUEST_CODE
import com.example.graduationproject.helper.Constants.TIME_OUT_MILLISECONDS
import com.example.graduationproject.helper.Utils
import com.example.graduationproject.helper.Utils.getAccessToken
import com.example.graduationproject.helper.listeners.FavoriteProductClickListener
import com.example.graduationproject.models.products.FavoriteProduct
import com.example.graduationproject.models.products.PostFavoriteProduct
import com.example.graduationproject.ui.activities.ProductActivity
import com.example.graduationproject.viewmodels.ProductActivityViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val TAG = "FavoritesFragment"

class FavoritesFragment : Fragment(), FavoriteProductClickListener {
    private lateinit var fragmentFavoritesBinding: FragmentFavoritesBinding
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private val cachingViewModel by viewModel<CachingViewModel>()
    private lateinit var accessToken: String
    private var favoriteProducts: MutableList<FavoriteProduct> = mutableListOf()
    private lateinit var favoriteProductsAdapter: FavoriteProductsAdapter
    private var currentFavoriteProduct: FavoriteProduct? = null
    private var currentFavoriteProductPosition = 0
    private lateinit var gridLayoutManager: GridLayoutManager
    var userScrolled = true
    var pastVisibleItems = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    private lateinit var loadingHandler: Handler
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
        accessToken = getAccessToken(requireContext()).orEmpty()

        loadingHandler = Handler(Looper.getMainLooper())

        if (Utils.getConnectionType(requireContext()) == 0) {
            Log.i(TAG, "00000 onViewCreated: OFFLINE")
            getFavoriteProductsOffline()
        } else {
            Log.i(TAG, "00000 onViewCreated: ONLINE")
            getFavoriteProductsOnline()
        }

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
                            if (Utils.getConnectionType(requireContext()) == 0) {
                                Log.i(TAG, "00000 onViewCreated: OFFLINE")
                                updateRecyclerViewOffline()
                            } else {
                                Log.i(TAG, "00000 onViewCreated: ONLINE")
                                updateRecyclerViewOnline()
                            }

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

    private fun getFavoriteProductsOffline() {
        try {
            lifecycleScope.launch {
                fragmentFavoritesBinding.progressBar.visibility = View.VISIBLE
                dismissProgressAfterTimeOut()
                val favoritePlacesLiveData = cachingViewModel.getFavoriteProductsFromDb()

                favoritePlacesLiveData?.observe(viewLifecycleOwner) { favProducts ->

                    favoriteProducts = favProducts
                    favProducts.let {
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
                    fragmentFavoritesBinding.progressBar.visibility = View.GONE
                }
            }
        } catch (ex: Throwable) {
            Log.i(TAG, "getFavoriteProducts: ${ex.localizedMessage}")
            fragmentFavoritesBinding.progressBar.visibility = View.GONE
        }
    }

    private fun updateRecyclerViewOffline() {
        try {
            lifecycleScope.launch {
                fragmentFavoritesBinding.progressBar.visibility = View.VISIBLE
                val favoritePlacesLiveData = cachingViewModel.getFavoriteProductsFromDb()
                favoritePlacesLiveData?.observe(viewLifecycleOwner) { favProducts ->
                    if (favProducts.isNullOrEmpty()) {
                        placeActivityViewModel.favoritePages = -1
                    } else {
                        favoriteProducts.addAll(favProducts)
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

    private fun getFavoriteProductsOnline() {
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

    private fun updateRecyclerViewOnline() {
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

    private suspend fun getProductFromServerAndAddItLocally(
        productId: String,
        accessToken: String,
        favoriteProduct: FavoriteProduct
    ) {
        placeActivityViewModel.getProductDetails(productId, accessToken)
            ?.observe(viewLifecycleOwner) {
                it?.let { product ->
                    product.favorite = 1
                    lifecycleScope.launch {
                        cachingViewModel.insertAsTransaction(favoriteProduct, product)
                        Log.i(TAG, "LLLL: INSERTED TRANSACTION FRAGMENT")
                    }
                }
            }
    }

    private suspend fun getProductFromServerAndRemoveItLocally(
        productId: String,
        accessToken: String,
        favoriteProduct: FavoriteProduct
    ) {
        placeActivityViewModel.getProductDetails(productId, accessToken)
            ?.observe(viewLifecycleOwner) {
                it?.let { product ->
                    lifecycleScope.launch {
                        cachingViewModel.deleteAsTransaction(favoriteProduct, product)
                        Log.i(TAG, "LLLL: REMOVED TRANSACTION FRAGMENT")
                    }
                }
            }
    }

    private fun addProductToFavorite(favoriteProduct: FavoriteProduct?, productPosition: Int) {
        lifecycleScope.launch {
            val responseMessage = placeActivityViewModel.addProductToFavorites(
                PostFavoriteProduct(pid = favoriteProduct?.id),
                accessToken
            )
            responseMessage?.let { _ ->
                favoriteProductsAdapter.notifyItemChanged(productPosition, favoriteProduct)
                favoriteProduct?.let {
                    it.id?.let { id ->
                        getProductFromServerAndAddItLocally(id.toString(), accessToken, it)
                    }
                }
            }
            if (responseMessage == null) {

            }
        }
    }

    private fun removeProductFromFavorite(favoriteProduct: FavoriteProduct) {
        lifecycleScope.launch {
            val responseMessage = placeActivityViewModel.deleteProductFromFavorites(
                favoriteProduct.id.toString(),
                accessToken
            )
            responseMessage?.let {
                favoriteProduct.id?.let { id ->
                    getProductFromServerAndRemoveItLocally(id.toString(), accessToken, favoriteProduct)
                }
            }
            if (responseMessage == null) {

            }
        }
    }

    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            loadingHandler.postDelayed({
                fragmentFavoritesBinding.progressBar.visibility = View.GONE
            }, TIME_OUT_MILLISECONDS)

        }
    }

    override fun onFavoriteIconClicked(favoriteProduct: FavoriteProduct?, productPosition: Int) {
        handleFavoriteState(productPosition, favoriteProduct)
        //if product is to be added
        //get [PRODUCT] from server
        //insert it into db
        //if product is to be removed
        //remove it
    }

    override fun onFavoriteProductClicked(favoriteProduct: FavoriteProduct?, position: Int) {
        currentFavoriteProduct = favoriteProduct
        currentFavoriteProductPosition = position

        val connectionState = Utils.getConnectionType(requireContext())

        val intent = Intent(requireContext(), ProductActivity::class.java)
        intent.putExtra("productId", favoriteProduct?.id)
        intent.putExtra("connectionState", connectionState)
        startActivityForResult(intent, FAVORITE_PRODUCT_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FAVORITE_PRODUCT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val isRemoved: Boolean = data.getBooleanExtra("removedProduct", false)

                    currentFavoriteProduct?.let {
                        //Is favorite
                        if (isRemoved) {
                            it.isFavorite = false
                            favoriteProductsAdapter.notifyItemChanged(
                                currentFavoriteProductPosition,
                                it
                            )
                            favoriteProductsAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingHandler.removeCallbacksAndMessages(null)
    }

}
