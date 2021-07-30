package com.example.graduationproject.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.example.graduationproject.R
import com.example.graduationproject.adapters.SliderAdapter
import com.example.graduationproject.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var activityWelcomeBinding: ActivityWelcomeBinding
    private lateinit var sliderAdapter : SliderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityWelcomeBinding = DataBindingUtil.setContentView(this,R.layout.activity_welcome)

        sliderAdapter = SliderAdapter(this)

        activityWelcomeBinding.sliderImagesViewPager.adapter = sliderAdapter

        addDotsIndicator(0)

        activityWelcomeBinding.sliderImagesViewPager.addOnPageChangeListener(viewListener)

        activityWelcomeBinding.skipButton.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }
    }

    //to show the 3 dots
    private fun addDotsIndicator(position:Int) {
        val dots : Array<TextView> = arrayOf(TextView(this),TextView(this),TextView(this))
       activityWelcomeBinding.dotsLayout.removeAllViews()
        for ((i,item) in dots.withIndex()) {
            dots[i] = TextView(this)
            dots[i].apply {
                text = (Html.fromHtml("&#8226;"))
                textSize = 36F
                setTextColor(resources.getColor(R.color.white_font_text))
            }
            activityWelcomeBinding.dotsLayout.addView(dots[i])

        }

        if (dots.isNotEmpty()) {
            dots[position]. setTextColor(resources.getColor(R.color.black))
        }

    }

    //to listen to changing the page
    private val viewListener : ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener{
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
           addDotsIndicator(position)
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }
}