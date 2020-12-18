package com.example.graduationproject.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.example.graduationproject.R
import com.example.graduationproject.ui.fragments.FavoritesFragment
import com.example.graduationproject.ui.fragments.HomeFragment
import com.example.graduationproject.ui.fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val stack = Stack<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val host: NavHostFragment = supportFragmentManager
//            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return
//
//        val navController = host.navController
//
//        setupBottomNavMenu

        stack.push(0)
        bottomNavigationView.menu.getItem(0).setIcon(R.drawable.ic_tent_home)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    val i = 0
                    navigateToHomeFragment()
                    changeColorNext(i)
                    if (!stack.contains(i)) {
                        stack.push(i)
                    }
                }
                R.id.favoritesFragment -> {
                    val i = 1
                    navigateToFavoritesFragment()
                    changeColorNext(i)
                    if (!stack.contains(i)) {
                        stack.push(i)
                    }
                }
                R.id.addPlaces -> {
                    val i = 2
                    navigateToAddPlacesActivity()
                    changeColorNext(i)
                    if (!stack.contains(i)) {
                        stack.push(i)
                    }
                }
                R.id.searchFragment -> {
                    val i = 3
                    navigateToSearchFragment()
                    changeColorNext(i)
                    if (!stack.contains(i)) {
                        stack.push(i)
                    }
                }
            }
            true
        }

    }

    private fun setupBottomNavMenu(navController: NavController) {
        //  bottom_nav.setupWithNavController(navController)
    }

    private fun navigateToHomeFragment() {
        val homeFragment = HomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.my_nav_host_fragment, homeFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToFavoritesFragment() {
        val favoriteFragment = FavoritesFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.my_nav_host_fragment, favoriteFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToSearchFragment() {
        val searchFragment = SearchFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.my_nav_host_fragment, searchFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToAddPlacesActivity() {
        val intent = Intent(this, AddPlaceActivity::class.java)
        startActivity(intent)
    }

//    override fun onBackPressed() {
//        if (!stack.empty()) {
//            changeColorPrevious(stack.peek())
//            stack.pop()
//            changeColorNext(stack.peek())
//            supportFragmentManager.popBackStack()
//        }
//    }

    private fun changeColorNext(i: Int) {
        //have to be tented
        when (i) {
            0 -> bottomNavigationView.menu.getItem(i).setIcon(R.drawable.ic_tent_home)
            1 -> bottomNavigationView.menu.getItem(i).setIcon(R.drawable.ic_tent_heart)
            2 -> bottomNavigationView.menu.getItem(i).setIcon(R.drawable.ic_tent_add_places)
            3 -> bottomNavigationView.menu.getItem(i).setIcon(R.drawable.ic_tent_search)
        }
    }

    private fun changeColorPrevious(i: Int) {
        when (i) {
            0 -> bottomNavigationView.menu.getItem(i).setIcon(R.drawable.ic_home)
            1 -> bottomNavigationView.menu.getItem(i).setIcon(R.drawable.ic_heart)
            2 -> bottomNavigationView.menu.getItem(i).setIcon(R.drawable.ic_placeholder)
            3 -> bottomNavigationView.menu.getItem(i).setIcon(R.drawable.ic_search)
        }
    }


}