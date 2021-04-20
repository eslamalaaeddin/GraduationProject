package com.example.graduationproject.ui.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.example.graduationproject.R
import com.example.graduationproject.databinding.ActivityMainBinding
import com.example.graduationproject.ui.fragments.UserInfoFragment
import com.example.graduationproject.ui.fragments.FavoritesFragment
import com.example.graduationproject.ui.fragments.HomeFragment
import com.example.graduationproject.ui.fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var bindingInstance: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingInstance = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        setContentView(R.layout.activity_main)

//        val host: NavHostFragment = supportFragmentManager
//            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return
//
//        val navController = host.navController
//
//        setupBottomNavMenu(navController = navController)

        //check if it is not there add it else do not do anything

        bindingInstance.testingButton.setOnClickListener {
            startActivity(Intent(this, TestingActivity::class.java))
        }

        if (savedInstanceState == null) {
            navigateToHomeFragment()
        }


        bindingInstance.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navigateToHomeFragment()
                }
                R.id.favoritesFragment -> {
                    navigateToFavoritesFragment()
                }
                R.id.searchFragment -> {
                    navigateToSearchFragment()
                }
//                R.id.logOutIcon -> {
////                    navigateToSearchFragment()
////                    navigateToSearchBottomSheet()
////                    updateStateAndLogOut()
//                    showLogoutDialog()
//                }
                R.id.moreNavigationDrawer -> {
                    navigateToUserInfoFragment()
                }
            }
            true
        }


    }

    private fun showLogoutDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.logout_dialog)

        val cancelButton = dialog.findViewById(R.id.cancelLogoutButton) as Button
        val logoutButton = dialog.findViewById(R.id.confirmLogoutButton) as Button
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        logoutButton.setOnClickListener {
            updateStateAndLogOut()
        }
        dialog.show()

    }

    private fun updateStateAndLogOut() {
        SplashActivity.setLoggedOut(this, true)
        SplashActivity.setAccessToken(this, "")
        SplashActivity.setRefreshToken(this, "")
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
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




    private fun navigateToUserInfoFragment(){
        val userInfoFragment = UserInfoFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.my_nav_host_fragment, userInfoFragment)
//            .addToBackStack(null)
            .commit()
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