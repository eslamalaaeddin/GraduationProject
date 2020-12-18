package com.example.graduationproject.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.graduationproject.R
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment(R.layout.fragment_search) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        upButton.setOnClickListener {
            //findNavController().navigateUp()
            activity?.supportFragmentManager?.popBackStack()
        }
    }
}