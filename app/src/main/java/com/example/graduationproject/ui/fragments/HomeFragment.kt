package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.example.graduationproject.R
import com.example.graduationproject.adapters.RecommendedPlacesAdapter
import com.example.graduationproject.databinding.FragmentHomeBinding
import com.example.graduationproject.helper.listeners.RecommendedProductClickListener
import com.example.graduationproject.model.products.Product
import com.example.graduationproject.model.products.VisitedProduct
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.HomeFragmentViewModel
import com.example.graduationproject.viewmodel.ProductActivityViewModel
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(), RecommendedProductClickListener {
    private val homeFragmentViewModel by viewModel<HomeFragmentViewModel>()
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private lateinit var fragmentBinding: FragmentHomeBinding
    var recommendedPlaces = mutableListOf<Product>()
    private var recProductsAdapter : RecommendedPlacesAdapter? = null
    private var accessToken = ""
    var tempClicked = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        // (activity as AppCompatActivity?)?.setSupportActionBar(fragmentBinding.mainToolbar)
        Log.i(TAG, "onCreateView: Home")
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accessToken = SplashActivity.getAccessToken(requireContext()).orEmpty()

//        android:nestedScrollingEnabled="false"

    }

    override fun onStart() {
        super.onStart()
        //I moved it to onStart so that i can see the effect of adding a place to favorite or removing it from fav
        //when i make the effect from place activity
        getRecommendedPlaces()
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

//    private fun getRecommendedPlaces() {
//        lifecycleScope.launch {
//            homeFragmentViewModel.getRecommendedProducts(1, accessToken)
//                ?.observe(viewLifecycleOwner) { recPlaces ->
//                    recPlaces?.let {
//                        fragmentBinding.homePlacesRecyclerView.apply {
//                            layoutManager = GridLayoutManager(context, 2)
//                            adapter = RecommendedPlacesAdapter(
//                                it.sortedBy { it.id },
//                                this@HomeFragment
//                            )
//                        }
//                    }
//                }
//        }

        private fun getRecommendedPlaces() {
            lifecycleScope.launch {
                homeFragmentViewModel.getRecommendedProductsPagedList( accessToken)
                    ?.observe(viewLifecycleOwner) {recProducts ->
                        fragmentBinding.homePlacesRecyclerView.apply {
                            layoutManager = GridLayoutManager(context, 2)
                            recProductsAdapter = RecommendedPlacesAdapter(this@HomeFragment)
                            recProductsAdapter?.let {
                                it.submitList(recProducts)
                                adapter = it
                            }

                        }
                    }
            }
        }
    }
