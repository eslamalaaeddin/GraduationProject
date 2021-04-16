package com.example.graduationproject.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.graduationproject.R
import com.example.graduationproject.databinding.ActivityTestingBinding
import com.example.graduationproject.helper.FileUtils
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.viewmodel.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_testing.*
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


private const val IMAGE_REQUEST_CODE = 123
private const val TAG = "TestingActivity"
private const val MY_PERMISSION_REQUEST = 100
class TestingActivity : AppCompatActivity() {
    private val userProfileViewModel by viewModel<UserProfileViewModel>()
    private var clicked = false
    private lateinit var binding: ActivityTestingBinding
    private var accessToken = ""
    private var stringImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_testing)

        accessToken = SplashActivity.getAccessToken(this).orEmpty()


        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSION_REQUEST
            )
        }

        selectProfilePicture.setOnClickListener {
            //1 get image from gallery
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
            MY_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted
                } else {
                    //Not Granted
                }
            }
        }
    }

    private fun uploadImage(imageUri: Uri, accessToken: String) {


        val descriptionPart = RequestBody.create(MultipartBody.FORM, "image")
        val originalFile =  FileUtils.getFile(this, imageUri)
        val filePart = originalFile?.let {
            RequestBody.create(
                contentResolver.getType(imageUri)?.toMediaTypeOrNull(),
                it
            )
        }

        val file = filePart?.let {
            MultipartBody.Part.createFormData(
                "image",
                originalFile.name,
                it
            )
        }

        val retrofit = RetrofitInstance.api
        val call = retrofit.uploadImage(
            image = file,
            accessToken = accessToken,
            description = descriptionPart,
            oldImageName = ""
        )

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Log.i(TAG, "TTTT onResponse: Success")
                Log.i(TAG, "TTTT onResponse: $response")
                Log.i(TAG, "TTTT onResponse: $response.")

//                Picasso.get().load(response.body())
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.i(TAG, "TTTT onResponse: ${t.localizedMessage}")
            }

        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedImageURI: Uri? = data.data
            val imageStringPath = FileUtils.getPath(this, selectedImageURI)
            if (selectedImageURI != null && imageStringPath != null) {
                val imgFile = File(imageStringPath)
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageURI)
                    uploadImage(selectedImageURI, accessToken)
                    if (bitmap != null){
                        imageView.setImageBitmap(bitmap)
                    }
                } catch (ex: IOException) {
                    Log.i(TAG, "PPPP onActivityResult: ${ex.localizedMessage}", ex)
                }
            }
        }
//        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
//            val imageUri = data.data!!
//            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//                uploadImage(imageUri, accessToken)
//                if (bitmap != null){
//                    imageView.setImageBitmap(bitmap)
//                }
//            } catch (ex: IOException) {
//                Log.i(TAG, "PPPP onActivityResult: ${ex.localizedMessage}", ex)
//            }
//        }
    }

}

/*
    @Multipart
@POST("user/updateprofile")
Observable<ResponseBody> updateProfile(@Part("user_id") RequestBody id,
                                       @Part("full_name") RequestBody fullName,
                                       @Part MultipartBody.Part image,
                                       @Part("other") RequestBody other);

//pass it like this
File file = new File("/storage/emulated/0/Download/Corrections 6.jpg");
RequestBody requestFile =
        RequestBody.create(MediaType.parse("multipart/form-data"), file);

// MultipartBody.Part is used to send also the actual file name
MultipartBody.Part body =
        MultipartBody.Part.createFormData("image", file.getName(), requestFile);

// add another part within the multipart request
RequestBody fullName =
        RequestBody.create(MediaType.parse("multipart/form-data"), "Your Name");

service.updateProfile(id, fullName, body, other);
 */