package com.example.graduationproject.dummy

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.graduationproject.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.add_to_post_bottom_sheet.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val IMAGE_REQUEST_CODE = 987
private const val VIDEO_REQUEST_CODE = 654
private const val CAMERA_REQUEST_CODE = 321
private const val MY_IMAGE_PERMISSION_REQUEST = 100
private const val MY_CAMERA_PERMISSION_REQUEST = 101
private const val TAG = "AddToPostBottomSheet"
class AddToPostBottomSheet(private val postAttachmentListener: PostAttachmentListener) : BottomSheetDialogFragment() {
    private var progressDialog: ProgressDialog? = null
    private lateinit var pAttachmentListener: PostAttachmentListener
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.add_to_post_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageOption = Option(R.drawable.ic_image, "Photo")
        val videoOption = Option(R.drawable.ic_videocam, "Video")
        val cameraOption = Option(R.drawable.ic_camera_alt_24, "Camera")

        val options = mutableListOf(imageOption, cameraOption)

        val adapter = AddToPostAdapter(requireContext(), R.layout.add_to_post_item_layout, options)

        view.postAdditionsListView.adapter = adapter

        view.postAdditionsListView.setOnItemClickListener { parent, view, position, id ->
            pAttachmentListener = postAttachmentListener
            when (position) {
                0 -> onClickImage()
                1 -> onClickCamera()
            }
        }


    }

    private fun onClickCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                MY_CAMERA_PERMISSION_REQUEST
            )
        }
        else{
             onClickCameraGranted()
        }
    }

    private fun onClickCameraGranted(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity?.packageManager!!) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }

    private fun onClickImage(){
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_IMAGE_PERMISSION_REQUEST
            )
        }

        else{
               onClickImageGranted()
        }

    }

    private fun onClickImageGranted(){
        val imageIntent = Intent(Intent.ACTION_GET_CONTENT)
        imageIntent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(imageIntent, "Choose an image"),
            IMAGE_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MY_IMAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onClickImageGranted()
                } else {
                    Toast.makeText(requireContext(), "Permission is required", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }

            MY_CAMERA_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onClickCameraGranted()
                } else {
                    Toast.makeText(requireContext(), "Permission is required", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //IMAGE
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK &&
            data != null && data.data != null
        ) {
            pAttachmentListener.onAttachmentAdded(data, "image", false)

        }
        //VIDEO
        else if (requestCode == VIDEO_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK ) {
            pAttachmentListener.onAttachmentAdded(data, "video", false)

        }
        //CAMERA
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK&&
            data != null && data.data != null ) {
            pAttachmentListener.onAttachmentAdded(data, "image", true)
        }
        //NO ATTACHMENT
        else{
            pAttachmentListener.onAttachmentAdded(null, "NULL", false)
        }
        dismiss()
    }
}