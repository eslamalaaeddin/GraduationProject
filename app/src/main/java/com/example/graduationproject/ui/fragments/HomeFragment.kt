package com.example.graduationproject.ui.fragments

import com.example.graduationproject.ui.activities.PlaceActivity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.R
import com.example.graduationproject.adapters.RecommendedPlacesAdapter
import com.example.graduationproject.databinding.FragmentHomeBinding
import com.example.graduationproject.helper.listeners.RecommendedPlaceClickListener
import com.example.graduationproject.model.places.Place
import com.example.graduationproject.model.places.VisitedPlace
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.HomeFragmentViewModel
import com.example.graduationproject.viewmodel.PlaceActivityViewModel
import kotlinx.android.synthetic.main.activity_place.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(), RecommendedPlaceClickListener {
    private val homeFragmentViewModel by viewModel<HomeFragmentViewModel>()
    private val placeActivityViewModel by viewModel<PlaceActivityViewModel>()
    private lateinit var fragmentBinding: FragmentHomeBinding
    var recommendedPlaces = mutableListOf<Place>()
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
//              homeFragmentViewModel.addNewPlace(Place(name = "place name", country = "Egypt", city = "Zagazig", latitude = -12.5, longitude = 10.24), accessToken).message.toString()
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

            getRecommendedPlaces()

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
//            Log.i(TAG, "ISLAM onViewCreated: ${homeFragmentViewModel.addPlaceToUserVisitedPlaces(VisitedPlace(123), accessToken).message.toString()}")
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
//            //homeFragmentViewModel.addPlaceToUserFavoritePlaces(FavoritePlace())
//            /*
//                I HAVE TO SOLVE BODY PROBLEM ==> WATCH FORM URL ENCODED
//             */
//        }

//        lifecycleScope.launch {
//            Log.i(TAG, "ISLAM onViewCreated: ${homeFragmentViewModel.deleteUserFavoritePlace("1", accessToken).message.toString()}")
//        }


    }

    override fun onFavoriteIconClicked(place: Place) {
        val isPlaceFavorite = place.isFavorite == 1
        if (isPlaceFavorite) {
            lifecycleScope.launch { deletePlaceFromFavorite(place) }
        } else {
            lifecycleScope.launch { addPlaceToFavorite(place) }
        }
    }

    private suspend fun addPlaceToFavorite(place: Place) {
        val responseMessage = placeActivityViewModel.addPlaceToUserFavoritePlaces(
            VisitedPlace(pid = place.id),
            accessToken
        )
        responseMessage?.let {
            add_to_favorite_image_view.setImageResource(R.drawable.ic_heart_filled)
//                getPlaceDetails(place)
               getRecommendedPlaces()
        }
    }

    private suspend fun deletePlaceFromFavorite(place: Place) {
        val responseMessage = placeActivityViewModel.deleteUserFavoritePlace(
            place.id.toString(),
            accessToken
        )
        responseMessage?.let {
            add_to_favorite_image_view.setImageResource(R.drawable.ic_heart)
//                getPlaceDetails(place)
            getRecommendedPlaces()
        }
    }

    private fun getRecommendedPlaces() {
        lifecycleScope.launch {
            homeFragmentViewModel.getRecommendedPlaces(1, accessToken)
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

    private suspend fun getPlaceDetails(place: Place) {
        val placeDetailsLiveData =
            placeActivityViewModel.getPlaceDetails(place.id.toString(), accessToken)
        placeDetailsLiveData?.observe(viewLifecycleOwner) { p ->
            p?.let {
                recommendedPlaces.remove(place)
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