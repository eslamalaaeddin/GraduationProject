package activities

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.graduationproject.R
import com.example.graduationproject.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import fragments.FavoritesFragment
import fragments.HomeFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController

        setupBottomNavMenu(navController)

        setUpToolbar()

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            activityMainBinding.mainToolbar,
            R.string.nav_open_drawer,
            R.string.nav_close_drawer
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        activityMainBinding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.add_place -> startActivity(Intent(this@MainActivity, AddPlaceActivity::class.java))
                R.id.nav_drawer -> drawer.openDrawer(GravityCompat.START)
                R.id.search -> Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
               // R.id.homeFragment -> navigateToHomeFragment()
             //   R.id.favoritesFragment -> navigateToFavoritesFragment()
            }
            true
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(activityMainBinding.mainToolbar)
        supportActionBar?.title = ""
//        activityMainBinding.mainToolbar.title = ""
////        activityMainBinding.mainToolbar.setTitleTextColor(Color.WHITE)
//        activityMainBinding.mainToolbar.overflowIcon?.setColorFilter(
//            resources.getColor(R.color.white),
//            PorterDuff.Mode.SRC_IN
//        )
    }


    private fun setupBottomNavMenu(navController: NavController) {

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.homeFragment -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
//            R.id.favoritesFragment -> Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show()
        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun navigateToHomeFragment() {
        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.my_nav_host_fragment,homeFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToFavoritesFragment() {
        val favoritesFragment = FavoritesFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.my_nav_host_fragment,favoritesFragment)
            .addToBackStack(null)
            .commit()
    }



}