package com.example.graduationproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.graduationproject.R

val IMAGES = arrayOf(
    R.drawable.ic_feedback_welcome,
    R.drawable.ic_chat_welcome,
    R.drawable.ic_searching_welcome
)
val HEADINGS = arrayOf("Recommendation via feedback","Comments do matter!","Search in movies by name or tag")
val DESCRIPTIONS = arrayOf("Our application recommends based on users history of interests, and collected feedback from movies that they previously interacted with.", "User comments have meaning in the recommendation decision.", "Users can reach desired movies by searching with the movie name or just using tags to get their favorite ones.")

class SliderAdapter(val context: Context) : PagerAdapter() {
    private lateinit var layoutInflater: LayoutInflater
    override fun getCount(): Int {
        return IMAGES.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.slide_layout,container,false)

        val logoImageView : ImageView = view.findViewById(R.id.logo_image_view)
        val headingTextView  : TextView = view.findViewById(R.id.heading_text_view)
        val headingDescriptionTextView  : TextView = view.findViewById(R.id.heading_description_text_view)


        logoImageView.setImageResource(IMAGES[position])
        headingTextView.text = HEADINGS[position]
        headingDescriptionTextView.text = DESCRIPTIONS[position]

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }


}