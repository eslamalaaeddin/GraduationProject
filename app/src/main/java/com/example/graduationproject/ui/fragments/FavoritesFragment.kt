package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.graduationproject.R
import com.example.graduationproject.adapters.FavoriteProductsAdapter
import com.example.graduationproject.databinding.FragmentFavoritesBinding
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
    private var favoriteProducts = mutableListOf<FavoriteProduct>()
    private lateinit var favoriteProductsAdapter: FavoriteProductsAdapter
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
    }

    override fun onStart() {
        super.onStart()
        getFavoriteProducts()
    }

    private fun getFavoriteProducts() {
        lifecycleScope.launch {
            val favoritePlacesLiveData = placeActivityViewModel.getFavoriteProducts(accessToken)
            favoritePlacesLiveData?.observe(viewLifecycleOwner) {favPlaces ->
                favPlaces.let {
                    if (favPlaces.isNullOrEmpty()) {
                        fragmentFavoritesBinding.noFavoriteProductsTextView.visibility =
                            View.VISIBLE
                    } else {
                        fragmentFavoritesBinding.noFavoriteProductsTextView.visibility = View.GONE
                        fragmentFavoritesBinding.favoriteProductsRecyclerView.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            Log.i(TAG, "OOOO getFavoriteProducts: $favPlaces")
                            favoriteProductsAdapter = FavoriteProductsAdapter(
                                favPlaces.sortedBy { it.id }.toMutableList(),
                                this@FavoritesFragment
                            )
                            adapter = favoriteProductsAdapter
                        }
                    }
                }

            }
        }
    }

    override fun onFavoriteIconClicked(favoriteProduct: FavoriteProduct, productPosition: Int) {
        lifecycleScope.launch {
            val responseMessage = placeActivityViewModel.deleteProductFromFavorites(
                favoriteProduct.id.toString(),
                accessToken
            )
            responseMessage?.let {
                favoriteProductsAdapter.removeItem(position = productPosition)
                favoriteProductsAdapter.notifyItemRemoved(productPosition)
                favoriteProductsAdapter.notifyDataSetChanged()
            }
        }

    }


}