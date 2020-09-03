package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.graduationproject.R
import com.example.graduationproject.databinding.FragmentPasswordForgetBinding

class ForgetPasswordFragment : Fragment() {
    private lateinit var passwordBinding:FragmentPasswordForgetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        passwordBinding =
            DataBindingUtil.inflate(inflater , R.layout.fragment_password_forget , container,false)

        return passwordBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        passwordBinding.sendPasswordLinkButton.setOnClickListener {
            Toast.makeText(context, "A link has been sent to your email, please check it.", Toast.LENGTH_LONG).show()

        }
    }
}