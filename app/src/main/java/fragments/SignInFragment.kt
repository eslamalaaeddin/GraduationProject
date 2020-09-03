package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.graduationproject.R
import com.example.graduationproject.databinding.FragmentInSignBinding


class SignInFragment : Fragment() {
    private lateinit var signInBinding: FragmentInSignBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signInBinding =
            DataBindingUtil.inflate(inflater , R.layout.fragment_in_sign , container,false)

        return signInBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInBinding.doNotHaveAccountTextView.setOnClickListener {

            val signUpFragment = SignUpFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, signUpFragment)
                ?.commit()

        }

        signInBinding.forgetPasswordTextView.setOnClickListener {

        }

    }
}