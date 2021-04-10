package com.example.graduationproject.ui.bottomsheets

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.graduationproject.R
import com.example.graduationproject.databinding.NavigationDrawerBottomSheetBinding
import com.example.graduationproject.dummy.AddToPostBottomSheet
import com.example.graduationproject.dummy.PostAttachmentListener
import com.example.graduationproject.helper.Constants.BASE_USER_IMAGE_URL
import com.example.graduationproject.helper.FileUtils
import com.example.graduationproject.model.user.User
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.ui.activities.SplashActivity
import com.example.graduationproject.viewmodel.NavigationDrawerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_testing.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

private const val TAG = "NavigationDrawerBottomS"
private const val IMAGE_REQUEST_CODE = 123
//http://127.0.0.1:3000/images/users/39131610630572.jpg
private const val MY_IMAGE_PERMISSION_REQUEST = 100

class NavigationDrawerBottomSheet : BottomSheetDialogFragment(), PostAttachmentListener {
    private lateinit var bindingInstance: NavigationDrawerBottomSheetBinding
    private val navDrawerViewModel by viewModel<NavigationDrawerViewModel>()
    private lateinit var accessToken: String
    private var user: User? = null
    private var postData: Intent? = null
    private var postDataType: String? = null
    private var bitmapFromCamera: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingInstance = DataBindingUtil.inflate(
            inflater,
            R.layout.navigation_drawer_bottom_sheet,
            container,
            false
        )
        return bindingInstance.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingInstance.settingsLayout.setOnClickListener { openSettingsBottomSheet() }
//        bindingInstance.logOutLayout.setOnClickListener {
//            SplashActivity.setLoggedOut(requireContext(), true)
//            SplashActivity.setAccessToken(requireContext(), "")
//            SplashActivity.setRefreshToken(requireContext(), "")
//            startActivity(Intent(requireContext(), RegisterActivity::class.java))
//            activity?.finish()
//        }

        bindingInstance.userImageView.setOnClickListener {
            onClickImage()
        }

        bindingInstance.upButtonView.setOnClickListener { dismiss() }
    }

    private fun onClickImage(){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_IMAGE_PERMISSION_REQUEST
            )
        }

        else{
            val imageIntent = Intent(Intent.ACTION_GET_CONTENT)
            imageIntent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(imageIntent, "Choose an image"),
                IMAGE_REQUEST_CODE
            )
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MY_IMAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val imageIntent = Intent(Intent.ACTION_GET_CONTENT)
                    imageIntent.type = "image/*"
                    startActivityForResult(
                        Intent.createChooser(imageIntent, "Choose an image"),
                        IMAGE_REQUEST_CODE
                    )
                } else {
                    Toast.makeText(requireContext(), "Permission is required", Toast.LENGTH_SHORT)
                        .show()
                    dismiss()
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            val selectedImageURI: Uri? = data.data
            var imageStringPath : String? = null
            try {
                imageStringPath = FileUtils.getPath(requireContext(), selectedImageURI)
//                imageStringPath = selectedImageURI?.path
            }
            catch (ex: Throwable){
                Toast.makeText(requireContext(), "Please choose an image from images folder ", Toast.LENGTH_LONG).show()
                Log.i(TAG, "MMMM onActivityResultERROR : ${ex.localizedMessage}", ex)
            }
            if (selectedImageURI != null && imageStringPath != null) {
                val imgFile = File(imageStringPath)
                Log.i(TAG, "MMMM onActivityResult STRING PATH: $imageStringPath")
                try {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            selectedImageURI
                        )
                    uploadImage(selectedImageURI, accessToken)
                    if (bitmap != null) {
                        bindingInstance.userImageView.setImageBitmap(bitmap)
                    }
                } catch (ex: Throwable) {
                    Log.i(TAG, "MMMM onActivityResultERROR : ${ex.localizedMessage}", ex)
                }
            }
        }
    }


    private fun openSettingsBottomSheet() {
        user?.let {
            val settingsBottomSheet = SettingsBottomSheet(it)
            settingsBottomSheet.show(activity?.supportFragmentManager!!, settingsBottomSheet.tag)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        accessToken = SplashActivity.getAccessToken(requireContext()).toString()
//        getUser()
        val userFromViewModelScopeLiveData =
            navDrawerViewModel.getUserWithViewModelScope(accessToken)
        userFromViewModelScopeLiveData.observe(viewLifecycleOwner) {
            it?.let { currentUser ->
                user = currentUser
                bindingInstance.userNameTextView.text = "${currentUser.firstName} ${currentUser.lastName}"
                bindingInstance.userEmailTextView.text = currentUser.email
                val userImageUrl = "${BASE_USER_IMAGE_URL}${currentUser.image}"
                Log.i(TAG, "TTTT getUser: $userImageUrl")
                Log.i(TAG, "TTTT getUser: $currentUser")

                if (userImageUrl.isNotEmpty()) {
                    Glide.with(requireContext())
                        .load(userImageUrl)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(bindingInstance.userImageView)

                } else {
                    bindingInstance.userImageView.setImageResource(R.drawable.avatar)
                }

            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getUser() {
        lifecycleScope.launch {
            val currentUser = navDrawerViewModel.getUser(accessToken)
            currentUser?.let { user ->
                this@NavigationDrawerBottomSheet.user = user
                Log.i(TAG, "TTTT getUser: $user")

                bindingInstance.userNameTextView.text = "${user.firstName} ${user.lastName}"
                bindingInstance.userEmailTextView.text = user.email
                val userImageUrl = "${BASE_USER_IMAGE_URL}${user.image}"
                Log.i(TAG, "TTTT getUser: $userImageUrl")

//                if (userImageUrl.isNotEmpty()) {
//                    Picasso.get().load(userImageUrl).into(bindingInstance.userImageView)
//
//                } else {
//                    bindingInstance.userImageView.setImageResource(R.drawable.avatar)
//                }
            }
        }
    }

    private fun showPostAttachmentBottomSheet() {
        val addToPostBottomSheet = AddToPostBottomSheet(this)
        addToPostBottomSheet.show(activity?.supportFragmentManager!!, addToPostBottomSheet.tag)
    }

    override fun onAttachmentAdded(data: Intent?, dataType: String, fromCamera: Boolean) {
        if (data != null) {
            postData = data
            postDataType = dataType
            bitmapFromCamera = fromCamera

            if (postDataType == "image") {
                val imageUri = data.data!!
                val bitmap: Bitmap? = if (bitmapFromCamera) {
                    postData?.extras?.get("data") as Bitmap
                } else {
                    MediaStore.Images.Media.getBitmap(
                        activity?.contentResolver,
                        postData!!.data
                    )
                }
                if (bitmap != null) {
                    bindingInstance.userImageView.setImageBitmap(bitmap)
                    try {
//                        uploadImage(imageUri, accessToken)

                    } catch (ex: IOException) {
                        Log.i(TAG, "PPPP onActivityResult: ${ex.localizedMessage}", ex)
                    }
                }
            }
        }

    }

    private fun uploadImage(imageUri: Uri, accessToken: String) {

        val descriptionPart = RequestBody.create(MultipartBody.FORM, "image")
//        val theRequiredFile = FiloUtils.getFileFromUri(requireContext(), imageUri)
        val theRequiredFile = FileUtils.getFile(requireContext(), imageUri)
//        val theRequiredFile = File(imageUri.encodedPath)
        val filePart = theRequiredFile.let {
            RequestBody.create(
                requireActivity().contentResolver.getType(imageUri)?.toMediaTypeOrNull(),
                it
            )
        }

        val file = filePart.let {
            MultipartBody.Part.createFormData(
                "image",
                theRequiredFile.name,
                it
            )
        }

        val retrofit = RetrofitInstance.api

        val call = retrofit.uploadImage(
            image = file,
            accessToken = accessToken,
            description = descriptionPart
        )

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Log.i(TAG, "TTTT onResponse: $response")
                if (response.isSuccessful) {

                    onStart()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.i(TAG, "TTTT onResponse: ${t.localizedMessage}")
            }

        })

    }


}