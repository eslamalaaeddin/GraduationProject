package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.graduationproject.R
import com.example.graduationproject.databinding.FragmentVerificationBinding

class VerificationFragment : Fragment() {
    private lateinit var verificationBinding:FragmentVerificationBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        verificationBinding =
            DataBindingUtil.inflate(inflater , R.layout.fragment_verification , container,false)

        return verificationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verificationBinding.noVerificationSendTextView.setOnClickListener {
            Toast.makeText(context, "Verification code has been sent,please check your phone", Toast.LENGTH_LONG).show()
        }

        verificationBinding.haveAccountTextView.setOnClickListener {

            val signInFragment = SignInFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, signInFragment)
                ?.commit()

        }

        verificationBinding.verifyButton.setOnClickListener {
            Toast.makeText(context, "To main Activity", Toast.LENGTH_LONG).show()
        }

    }
}