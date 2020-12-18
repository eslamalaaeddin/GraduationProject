//package com.example.graduationproject.ui.activities
//
//import android.graphics.Color
//import android.os.Bundle
//import android.view.Menu
//import android.view.MenuItem
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.ActionBarDrawerToggle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.view.GravityCompat
//import androidx.databinding.DataBindingUtil
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.fragment.app.Fragment
//import com.example.graduationproject.R
//import com.example.graduationproject.databinding.ActivityTestingBinding
//import com.google.android.material.navigation.NavigationView
//
//private const val TAG = "TestingActivity"
//class TestingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
//
//    private var clicked = false
//    private lateinit var binding: ActivityTestingBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_testing)
//
////        val toolbar = findViewById<View>(R.id.main_toolbar) as Toolbar
////        setSupportActionBar(toolbar)
//
//        supportActionBar?.setDisplayShowTitleEnabled(false)
////        toolbar.setNavigationIcon(R.drawable.ic_heart)
//       // toolbar.navigationIcon?.setTint(Color.WHITE)
////        toolbar.title = ""
////        toolbar.subtitle = ""
//        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
////        val toggle = ActionBarDrawerToggle(
////            this,
////            drawer,
////            toolbar,
////            R.string.nav_open_drawer,
////            R.string.nav_close_drawer
////        )
//
////        drawer.addDrawerListener(toggle)
////        toggle.isDrawerSlideAnimationEnabled = true
////        toggle.syncState()
//        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
//        navigationView.setNavigationItemSelectedListener(this)
//        //what is the purpose of the following four lines
//        //what is the purpose of the following four lines
////        val fragment: Fragment = InboxFragment()
////        val ft = supportFragmentManager.beginTransaction()
////        ft.add(R.id.content_frame, fragment)
////        ft.commit()
//
//
//    }
//
////    override fun onNavigationItemSelected(item: MenuItem): Boolean {
////        val id = item.itemId
//////        var fragment: Fragment? = null
//////        var intent: Intent? = null
////        when (id) {
////            R.id.item1 -> Toast.makeText(this, "item 1", Toast.LENGTH_SHORT).show()
////            R.id.item2 -> Toast.makeText(this, "item 2", Toast.LENGTH_SHORT).show()
////            R.id.item3 -> Toast.makeText(this, "item 3", Toast.LENGTH_SHORT).show()
////        }
//////        if (fragment != null) {
//////            val ft = supportFragmentManager.beginTransaction()
//////            ft.replace(R.id.content_frame, fragment)
//////            ft.commit()
//////        } else {
//////            startActivity(intent)
//////        }
////        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
////        drawer.closeDrawer(GravityCompat.START)
////        return true
////    }
////
////    override fun onBackPressed() {
////        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
////        if (drawer.isDrawerOpen(GravityCompat.START)) {
////            drawer.closeDrawer(GravityCompat.START)
////        } else {
////            super.onBackPressed()
////        }
////    }
////
////    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
////        menuInflater.inflate(R.menu.bottom_nav_menu,menu)
////        return super.onCreateOptionsMenu(menu)
////    }
////
////    override fun onOptionsItemSelected(item: MenuItem): Boolean {
////        when (item.itemId) {
////            R.id.item1 -> Toast.makeText(this, "item 1", Toast.LENGTH_SHORT).show()
////            R.id.item2 -> Toast.makeText(this, "item 2", Toast.LENGTH_SHORT).show()
////            R.id.item3 -> Toast.makeText(this, "item 3", Toast.LENGTH_SHORT).show()
////        }
////        return super.onOptionsItemSelected(item)
////    }
//}