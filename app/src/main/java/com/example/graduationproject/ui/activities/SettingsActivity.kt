package com.example.graduationproject.ui.activities

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.graduationproject.R
import com.example.graduationproject.databinding.ActivitySettingsBinding
import com.example.graduationproject.helper.Constants
import com.example.graduationproject.helper.Constants.ACTION_IMAGE_UPLOADED
import com.example.graduationproject.helper.FileUtils
import com.example.graduationproject.model.user.UserName
import com.example.graduationproject.model.user.UserPassword
import com.example.graduationproject.services.ImageUploaderService
import com.example.graduationproject.viewmodel.NavigationDrawerViewModel
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.fragment_up_sign.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


private const val IMAGE_REQUEST_CODE = 123
private const val MY_IMAGE_PERMISSION_REQUEST = 100
private const val TAG = "SettingsActivity"

class SettingsActivity : AppCompatActivity() {
    private lateinit var bindingInstance: ActivitySettingsBinding
    private lateinit var accessToken: String
    private lateinit var notificationBroadcastReceiver: NotificationBroadcastReceiver
    private val navDrawerViewModel by viewModel<NavigationDrawerViewModel>()
    private var firstName = ""
    private var lastName = ""
    private var imageUrl = ""
    private var userBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingInstance = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        accessToken = SplashActivity.getAccessToken(this).toString()

        initUserInfo()

        updateUserUi()

        bindingInstance.upButtonView.setOnClickListener { finish() }

        bindingInstance.userImageView.setOnClickListener { onClickImage() }

        bindingInstance.changePasswordImageButton.setOnClickListener {
            showUpdateUserPasswordDialog()
        }

        bindingInstance.updateInfoButton.setOnClickListener {
            val firstName = bindingInstance.firstNameEditText.text.toString()
            val lastName = bindingInstance.lastNameEditText.text.toString()
            if (firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
                val toast = Toast.makeText(
                    applicationContext,
                    "Enter all information first.", Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 96)
                toast.show()

            } else if (this.firstName == firstName && this.lastName == lastName) {
                val toast = Toast.makeText(
                    applicationContext,
                    "First and last name didn't change.", Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 96)
                toast.show()
            } else {
                updateUserInfo(firstName, lastName)
            }
        }

    }

    private fun initUserInfo() {
        firstName = intent.getStringExtra("userFirstName").toString()
        lastName = intent.getStringExtra("userLastName").toString()
        imageUrl = intent.getStringExtra("userImageUrl").toString()
    }

    private fun showUpdateUserPasswordDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.change_password_dialog)

        val oldPasswordEditText = dialog.findViewById(R.id.oldPasswordEditText) as EditText
        val newPasswordEditText = dialog.findViewById(R.id.newPasswordEditText) as EditText

        val cancelButton = dialog.findViewById(R.id.cancelChangePasswordButton) as Button
        val submitButton = dialog.findViewById(R.id.SubmitChangePasswordButton) as Button
        submitButton.setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()

            if (oldPassword.trim().isEmpty() || newPassword.trim().isEmpty()) {
                val toast = Toast.makeText(
                    applicationContext,
                    "Enter all information first.", Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 96)
                toast.show()

            } else {
                val userPassword = UserPassword(oldPassword, newPassword)
                lifecycleScope.launch {
                    bindingInstance.progressBar.visibility = View.VISIBLE
                    submitButton.isEnabled = false
                    val responseMessage = navDrawerViewModel.changeUserPassword(
                        userPassword,
                        accessToken
                    )
                    responseMessage?.let {
                        bindingInstance.progressBar.visibility = View.GONE
                        submitButton.isEnabled = true
                        val toast = Toast.makeText(
                            applicationContext,
                            it.message, Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.TOP, 0, 96)
                        toast.show()
                        dialog.dismiss()
                    }
                    if (responseMessage == null) {
                        bindingInstance.progressBar.visibility = View.GONE
                        submitButton.isEnabled = true
                    }
                }
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onStart() {
        super.onStart()
        notificationBroadcastReceiver = NotificationBroadcastReceiver()
        val intentFilter = IntentFilter(ACTION_IMAGE_UPLOADED)
        registerReceiver(notificationBroadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
//        bindingInstance.progressBar.visibility = View.GONE
//        bindingInstance.updateInfoButton.isEnabled = true
        unregisterReceiver(notificationBroadcastReceiver)
    }

    private fun updateUserInfo(firstName: String, lastName: String) {
        //check to not make a dummy request with the same data
//        if (firstName != user.firstName && lastName != user.lastName) {
        val userName = UserName(firstName, lastName)
        dismissProgressAfterTimeOut()
        bindingInstance.updateInfoButton.isEnabled = false
        lifecycleScope.launch {
            val responseMessage = navDrawerViewModel.updateUserName(userName, accessToken)
            responseMessage?.let {
                bindingInstance.updateInfoButton.isEnabled = true
                bindingInstance.progressBar.visibility = View.GONE
                val toast = Toast.makeText(
                    applicationContext,
                    "Updated successfully", Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.TOP, 0, 96)
                toast.show()

            }
            if (responseMessage == null) {
                bindingInstance.progressBar.visibility = View.GONE
                bindingInstance.updateInfoButton.isEnabled = true
            }
        }
//        }
    }

    private fun updateUserUi() {
        dismissProgressAfterTimeOut()
        val userImageUrl = "${Constants.BASE_USER_IMAGE_URL}${imageUrl}"
        if (userImageUrl.isNotEmpty()) {
            Glide.with(this)
                .asBitmap()
                .load(userImageUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bindingInstance.userImageView.setImageBitmap(resource)
                        bindingInstance.progressBar.visibility = View.GONE
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        bindingInstance.progressBar.visibility = View.GONE
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })
//                .into(bindingInstance.userImageView)

        }
        bindingInstance.firstNameEditText.setText(firstName)
        bindingInstance.firstNameEditText.setSelection(bindingInstance.firstNameEditText.text.length)

        bindingInstance.lastNameEditText.setText(lastName)
        bindingInstance.lastNameEditText.setSelection(bindingInstance.lastNameEditText.text.length)

    }

    private fun onClickImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                MY_IMAGE_PERMISSION_REQUEST
            )
        } else {
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
        when (requestCode) {
            MY_IMAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    val imageIntent = Intent(Intent.ACTION_GET_CONTENT)
                    imageIntent.type = "image/*"
                    startActivityForResult(
                        Intent.createChooser(imageIntent, "Choose an image"),
                        IMAGE_REQUEST_CODE
                    )
                } else {
                    val toast = Toast.makeText(
                        applicationContext,
                        "Permissions are required.", Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP, 0, 96)
                    toast.show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedImageURI: Uri? = data.data
            var imageStringPath: String? = null
            try {
                imageStringPath = FileUtils.getPath(this, selectedImageURI)
//                imageStringPath = selectedImageURI?.path
            } catch (ex: Throwable) {
                Toast.makeText(
                    this,
                    "Please choose an image from images folder ",
                    Toast.LENGTH_LONG
                ).show()
                Log.i(TAG, "MMMM onActivityResultERROR : ${ex.localizedMessage}", ex)
            }

            if (selectedImageURI != null && imageStringPath != null) {
                try {
                    val theRequiredFile = FileUtils.getFile(this, selectedImageURI)
                    if (theRequiredFile.length() <= 1000000) {
//                        dismissProgressAfterTimeOut()
                        var path = ""
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            selectedImageURI
                        )

                        if (bitmap != null) {
                            val intent =
                                Intent(this@SettingsActivity, ImageUploaderService::class.java)
                            ContextCompat.startForegroundService(this@SettingsActivity, intent)
                            CoroutineScope(Dispatchers.IO).launch {
                                Log.i(TAG, "GGG onActivityResult: 1")
                                val deferredBitmap: Deferred<Bitmap?> =
                                    CoroutineScope(Dispatchers.IO).async { compressImage(bitmap) }
                                Log.i(TAG, "GGG onActivityResult: 2")
                                userBitmap = deferredBitmap.await()
                                Log.i(TAG, "GGG onActivityResult: 3")
                                path = MediaStore.Images.Media.insertImage(
                                    contentResolver,
                                    userBitmap,
                                    "Title",
                                    null
                                )
                                //1// Start the service
                                intent.data = Uri.parse(path)
                                intent.putExtra("accessToken", accessToken)
                                startService(intent)
//                                ContextCompat.startForegroundService(this@SettingsActivity, intent)
                            }
                        }

                    } else {
                        bindingInstance.progressBar.visibility = View.GONE

                        val toast = Toast.makeText(
                            applicationContext,
                            "Image size can't be more than 1 MB.", Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.TOP, 0, 96)
                        toast.show()
                    }

                }
                catch (ex: Throwable) {
                  //  bindingInstance.progressBar.visibility = View.GONE
                } finally {
                    //bindingInstance.progressBar.visibility = View.GONE
                }
            }
        }
    }


    inner class NotificationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action == ACTION_IMAGE_UPLOADED) {
                bindingInstance.progressBar.visibility = View.GONE
                bindingInstance.userImageView.setImageBitmap(userBitmap)
            }
        }
    }

    private fun compressImage(bitmap: Bitmap): Bitmap? {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
        val bitmapData = bos.toByteArray()
        val inputStream = ByteArrayInputStream(bitmapData)

        val original = BitmapFactory.decodeStream(inputStream)
        val out = ByteArrayOutputStream()
        original.compress(Bitmap.CompressFormat.PNG, 100, out)
        val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))

        //Toast.makeText(this, "${original.width.toString() + " " + original.height}\n${decoded.width.toString() + " " + decoded.height}", Toast.LENGTH_LONG).show()
        Log.i("Original   TTT", original.width.toString() + " " + original.height)
        Log.i("Compressed TTT", decoded.width.toString() + " " + decoded.height)

        return decoded
    }


    private fun dismissProgressAfterTimeOut() {
        lifecycleScope.launchWhenStarted {
            bindingInstance.progressBar.visibility = View.VISIBLE
            //  bindingInstance.updateInfoButton.isEnabled = false
            Handler().postDelayed({
                bindingInstance.progressBar.visibility = View.GONE
                // bindingInstance.updateInfoButton.isEnabled = true
            }, Constants.TIME_OUT_MILLISECONDS)
        }

    }

}