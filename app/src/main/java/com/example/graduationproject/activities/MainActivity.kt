package com.example.graduationproject.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.graduationproject.R
import com.example.graduationproject.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){
    private lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController

        setupBottomNavMenu(navController)

//        activityMainBinding.bottomNav.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.add_place ->{
//                    startActivity(Intent(this@MainActivity, AddPlaceActivity::class.java))
//                }
//                R.id.search -> Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
//            }
//            true
//        }
    }

    private fun setupBottomNavMenu(navController: NavController) {
            bottom_nav.setupWithNavController(navController)
    }


}