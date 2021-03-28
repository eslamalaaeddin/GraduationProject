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
import com.example.graduationproject.helper.listeners.RecommendedPlaceClickListener
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

class HomeFragment : Fragment(), RecommendedPlaceClickListener {
    private val homeFragmentViewModel by viewModel<HomeFragmentViewModel>()
    private val placeActivityViewModel by viewModel<ProductActivityViewModel>()
    private lateinit var fragmentBinding: FragmentHomeBinding
    var recommendedPlaces = mutableListOf<Product>()
    private var accessToken = ""
    var tempClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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

//        newAdapter = RecommendedPlacesAdapter()
//        fragmentBinding.homePlacesRecyclerView.apply {
////            layoutManager = LinearLayoutManager(context)
//            layoutManager = GridLayoutManager(context,2)
//            adapter = placesAdapter
//        }

        // Toast.makeText(requireContext(), "${SplashActivity.getAccessToken(requireContext())}", Toast.LENGTH_LONG).show()

//        homeBinding.addPlacesFab.setOnClickListener {
//            startActivity(Intent(context, AddPlaceActivity::class.java))
//        }

//          lifecycleScope.launch {
//              homeFragmentViewModel.addNewPlace(Product(name = "place name", country = "Egypt", city = "Zagazig", latitude = -12.5, longitude = 10.24), accessToken).message.toString()
//          }

//        testButton.setOnClickListener {
//           startActivity(Intent(requireContext(), TestingActivity::class.java))
//        }


//
//        lifecycleScope.launch {
//            homeFragmentViewModel.searchForPlaceInCountry("beach", "egypt", accessToken).observe(viewLifecycleOwner){
//                Log.i(TAG, "ISLAM onViewCreated: $it")
//                Log.i(TAG, "ISLAM onViewCreated: ${it.size}")
//            }
//        }

//        lifecycleScope.launch {
//            homeFragmentViewModel.searchForSpecificPlace(1,accessToken).observe(viewLifecycleOwner){
//                Log.i(TAG, "onViewCreated: $it")
//            }
//        }



//        lifecycleScope.launch {
//            homeFragmentViewModel.getPlaceImages("1",accessToken).observe(viewLifecycleOwner){
//                Log.i(TAG, "onViewCreated: $it")
//                Log.i(TAG, "onViewCreated: ${it.size}")
//            }
//        }

//        lifecycleScope.launch {
//            homeFragmentViewModel.getPlaceComments("1", 1, accessToken).observe(viewLifecycleOwner){
//                Log.i(TAG, "ISLAM onViewCreated: $it")
//                Log.i(TAG, "ISLAM onViewCreated: ${it.size}")
//            }
//        }

//        lifecycleScope.launch {
//            homeFragmentViewModel.getUserVisitedPlaces(accessToken).observe(viewLifecycleOwner){
//                Log.i(TAG, "ISLAM onViewCreated: $it")
//                Log.i(TAG, "ISLAM onViewCreated: ${it.size}")
//            }
//        }

//        lifecycleScope.launch {
//            Log.i(TAG, "ISLAM onViewCreated: ${homeFragmentViewModel.addPlaceToUserVisitedPlaces(VisitedProduct(123), accessToken).message.toString()}")
//        }

//        lifecycleScope.launch {
//            Log.i(TAG, "ISLAM onViewCreated: ${homeFragmentViewModel.deleteVisitedPlace("1", accessToken).message.toString()}")
//        }

//        lifecycleScope.launch {
//            homeFragmentViewModel.getUserFavoritePlaces(accessToken).observe(viewLifecycleOwner){
//                Log.i(TAG, "ISLAM onViewCreated: $it")
//                Log.i(TAG, "ISLAM onViewCreated: ${it.size}")
//            }
//        }

//        lifecycleScope.launch {
//            //homeFragmentViewModel.addPlaceToUserFavoritePlaces(FavoriteProduct())
//            /*
//                I HAVE TO SOLVE BODY PROBLEM ==> WATCH FORM URL ENCODED
//             */
//        }

//        lifecycleScope.launch {
//            Log.i(TAG, "ISLAM onViewCreated: ${homeFragmentViewModel.deleteUserFavoritePlace("1", accessToken).message.toString()}")
//        }


    }

    override fun onStart() {
        super.onStart()
        //I moved it to onStart so that i can see the effect of adding a place to favorite or removing it from fav
        //when i make the effect from place activity
        getRecommendedPlaces()
    }

    override fun onFavoriteIconClicked(product: Product) {
        val isPlaceFavorite = product.isFavorite == 1
        if (isPlaceFavorite) {
            lifecycleScope.launch { deletePlaceFromFavorite(product) }
        } else {
            lifecycleScope.launch { addPlaceToFavorite(product) }
        }
    }

    private suspend fun addPlaceToFavorite(product: Product) {
        val responseMessage = placeActivityViewModel.addProductToFavorites(
            VisitedProduct(pid = product.id),
            accessToken
        )
        responseMessage?.let {
            add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
//                getPlaceDetails(product)
               getRecommendedPlaces()
        }
    }

    private suspend fun deletePlaceFromFavorite(product: Product) {
        val responseMessage = placeActivityViewModel.deleteProductFromFavorites(
            product.id.toString(),
            accessToken
        )
        responseMessage?.let {
            add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
//                getPlaceDetails(product)
            getRecommendedPlaces()
        }
    }

    private fun getRecommendedPlaces() {
        lifecycleScope.launch {
            homeFragmentViewModel.getRecommendedProducts(1, accessToken)
                ?.observe(viewLifecycleOwner) { recPlaces ->
                    recPlaces?.let {
                        fragmentBinding.homePlacesRecyclerView.apply {
                            layoutManager = GridLayoutManager(context, 2)
                            adapter = RecommendedPlacesAdapter(
                                it.sortedBy { it.id },
                                this@HomeFragment
                            )
                        }
                    }
                }
        }
    }

    private suspend fun getPlaceDetails(product: Product) {
        val placeDetailsLiveData =
            placeActivityViewModel.getProductDetails(product.id.toString(), accessToken)
        placeDetailsLiveData?.observe(viewLifecycleOwner) { p ->
            p?.let {
                recommendedPlaces.remove(product)
                recommendedPlaces.add(p)
                fragmentBinding.homePlacesRecyclerView.apply {
                    layoutManager = GridLayoutManager(context, 2)
                    adapter = RecommendedPlacesAdapter(
                        recommendedPlaces.sortedBy { it.id },
                        this@HomeFragment
                    )
                }
            }
        }
    }
}