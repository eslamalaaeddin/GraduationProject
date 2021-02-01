package com.example.graduationproject.ui.fragments

import com.example.graduationproject.ui.activities.PlaceActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationproject.dummy.DummyPlace
import com.example.graduationproject.R
import com.example.graduationproject.adapters.FavoritePlacesAdapter
import com.example.graduationproject.adapters.RecommendedPlacesAdapter
import com.example.graduationproject.databinding.FragmentFavoritesBinding
import com.example.graduationproject.helper.listeners.FavoritePlaceClickListener
import com.example.graduationproject.helper.listeners.RecommendedPlaceClickListener
import com.example.graduationproject.model.places.FavoritePlace
import com.example.graduationproject.model.places.Place
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.PlaceActivityViewModel
import kotlinx.android.synthetic.main.activity_place.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "FavoritesFragment"
class FavoritesFragment: Fragment(), FavoritePlaceClickListener {
    private lateinit var fragmentFavoritesBinding: FragmentFavoritesBinding
    private val placeActivityViewModel by viewModel<PlaceActivityViewModel>()
    private lateinit var accessToken: String
    private var favoritePlaces = mutableListOf<FavoritePlace>()
    private lateinit var favoritePlacesAdapter: FavoritePlacesAdapter
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

            lifecycleScope.launch {
            val favoritePlacesLiveData = placeActivityViewModel.getUserFavoritePlaces(accessToken)
            favoritePlacesLiveData?.observe(viewLifecycleOwner){favPlaces ->
                favPlaces?.let {
                    fragmentFavoritesBinding.homePlacesRecyclerView.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        favoritePlacesAdapter = FavoritePlacesAdapter(
                            favPlaces.sortedBy { it.id },
                            this@FavoritesFragment
                        )
                        adapter = favoritePlacesAdapter
                    }
                }

            }
        }

    }

    override fun onFavoriteIconClicked(favoritePlace: FavoritePlace) {
        favoritePlaces.remove(favoritePlace)
        favoritePlacesAdapter.notifyDataSetChanged()
    }


}