package activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.graduationproject.R
import com.example.graduationproject.databinding.ActivityRegisterBinding
import fragments.SignInFragment
import fragments.SignUpFragment

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerBinding = DataBindingUtil.setContentView(this,R.layout.activity_register)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val signUpFragment = SignUpFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, signUpFragment)
                .commit()
        }



    }

}