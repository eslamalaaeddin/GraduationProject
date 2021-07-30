package com.example.graduationproject.ui.activities

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.graduationproject.R
import com.example.graduationproject.helper.listeners.SearchListener
import com.example.graduationproject.databinding.ActivityMainBinding
import com.example.graduationproject.ui.fragments.UserInfoFragment
import com.example.graduationproject.ui.fragments.FavoritesFragment
import com.example.graduationproject.ui.fragments.HomeFragment
import com.example.graduationproject.ui.fragments.SearchFragment

class MainActivity : AppCompatActivity(), SearchListener {
    private lateinit var bindingInstance: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingInstance = DataBindingUtil.setContentView(this, R.layout.activity_main)

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
                R.id.moreNavigationDrawer -> {
                    navigateToUserInfoFragment()
                }
            }
            true
        }


    }

    private fun navigateToHomeFragment() {
        val homeFragment = HomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.my_nav_host_fragment, homeFragment)
            .commit()
    }

    private fun navigateToFavoritesFragment() {
        val favoriteFragment = FavoritesFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.my_nav_host_fragment, favoriteFragment)
            .commit()
    }

    private fun navigateToSearchFragment() {
        val searchFragment = SearchFragment(this)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.my_nav_host_fragment, searchFragment)
            .commit()
    }


    private fun navigateToUserInfoFragment(){
        val userInfoFragment = UserInfoFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.my_nav_host_fragment, userInfoFragment)
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

    //to show up the bottom navigation after searching in case it is not visible
    override fun onSearchEvent() {
        bindingInstance.bottomNavigationView.clearAnimation();
        bindingInstance.bottomNavigationView.animate().translationY(0F).duration = 200
    }


}