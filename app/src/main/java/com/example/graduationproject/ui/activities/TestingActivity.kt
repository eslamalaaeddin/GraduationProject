package com.example.graduationproject.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.graduationproject.R
import com.example.graduationproject.databinding.ActivityTestingBinding
import com.example.graduationproject.network.Api
import com.example.graduationproject.network.RetrofitInstance
import com.example.graduationproject.viewmodel.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_testing.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

private const val IMAGE_REQUEST_CODE = 123
private const val TAG = "TestingActivity"

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

//        uploadImage(accessToken)

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

    private fun uploadImage(imageUri: Uri, accessToken: String) {

        val file: File = File(imageUri.path)
        val retrofit: Retrofit = RetrofitInstance.retrofit
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val parts = MultipartBody.Part.createFormData("newimage", file.name, requestBody)
//        val someData = RequestBody.create(MediaType.parse("text/plain"), "This is a new Image")
        val uploadApis: Api = retrofit.create(Api::class.java)
        val call: Call<RequestBody?>? = uploadApis.uploadImage(parts, "1", accessToken)
        call?.enqueue(object : Callback<RequestBody?> {
            override fun onResponse(call: Call<RequestBody?>, response: Response<RequestBody?>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@TestingActivity, "Succsessful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@TestingActivity, "UnSuccessful", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RequestBody?>, t: Throwable) {
                Toast.makeText(this@TestingActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            val imagePath = data.data!!

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
                stringImage = imageToString(bitmap)
                uploadImage(imagePath, accessToken)
                //imageView.setImageBitmap(bitmap)
            } catch (ex: IOException) {
                Log.e(TAG, "onActivityResult: ${ex.message}", ex)
            }
        }
    }

    private fun imageToString(bitmap: Bitmap): String {
        val byteArrayOS = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS)
        val byteImage = byteArrayOS.toByteArray()
        return Base64.encodeToString(byteImage, Base64.DEFAULT)


    }
}