package com.example.graduationproject.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.graduationproject.R
import com.example.graduationproject.ui.fragments.VerificationFragment


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val responseCodeFromAuthRepo = intent.getIntExtra("unVerified", 0)
        if (responseCodeFromAuthRepo == 403){
            val verificationFragment = VerificationFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.registerHostFragment, verificationFragment)
                .commit()
        }
    }

}