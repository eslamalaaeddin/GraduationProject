package com.example.graduationproject.ui.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.example.graduationproject.R
import com.example.graduationproject.ui.bottomsheets.SearchBottomSheet
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
//        setupBottomNavMenu(navController = navController)

        //check if it is not there add it else do not do anything
        navigateToHomeFragment()

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navigateToHomeFragment()
                }
                R.id.favoritesFragment -> {
                    navigateToFavoritesFragment()
                }
                R.id.addPlaces -> {
                    navigateToAddPlacesActivity()
                }
                R.id.searchFragment -> {
//                    navigateToSearchFragment()
                    navigateToSearchBottomSheet()
                }
            }
            true
        }

    }

    private fun setupBottomNavMenu(navController: NavController) {
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun navigateToHomeFragment() {
        val homeFragment = HomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.my_nav_host_fragment, homeFragment)
//            .addToBackStack(null)
            .commit()
    }

    private fun navigateToFavoritesFragment() {
        val favoriteFragment = FavoritesFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.my_nav_host_fragment, favoriteFragment)
//            .addToBackStack(null)
            .commit()
    }

    private fun navigateToSearchFragment() {
        val searchFragment = SearchFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.my_nav_host_fragment, searchFragment)
//            .addToBackStack(null)
            .commit()
    }

    private fun navigateToSearchBottomSheet(){
        val searchBottomSheet = SearchBottomSheet()
        searchBottomSheet.show(supportFragmentManager, searchBottomSheet.tag)
    }

    private fun navigateToAddPlacesActivity() {
        val intent = Intent(this, AddPlaceActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
       showExitApplicationDialog()
    }

    private fun showExitApplicationDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.exit_application_dialog)

        val cancelButton = dialog.findViewById(R.id.cancelExitButton) as Button
        val exitButton = dialog.findViewById(R.id.confirmExitButton) as Button
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        exitButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()

    }


}