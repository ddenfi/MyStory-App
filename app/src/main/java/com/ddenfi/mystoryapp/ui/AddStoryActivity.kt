package com.ddenfi.mystoryapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ddenfi.mystoryapp.R
import com.ddenfi.mystoryapp.databinding.ActivityAddStoryBinding
import com.ddenfi.mystoryapp.utils.*
import com.ddenfi.mystoryapp.viewmodel.AddStoryViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels()
    private var getFile: File? = null
    private var result: Bitmap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var latLong: Location

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.no_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.ivPreviewImg.setImageResource((R.drawable.story_app_placeholder))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.swLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val isGranted = REQUIRED_PERMISSIONS_LOCATION.all {
                    ContextCompat.checkSelfPermission(
                        baseContext,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }
                if (!isGranted) {
                    ActivityCompat.requestPermissions(
                        this,
                        REQUIRED_PERMISSIONS_LOCATION,
                        REQUEST_CODE_PERMISSIONS
                    )
                    Toast.makeText(
                        this,
                        "You need to allow location permission to use this features!",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.swLocation.isChecked = false
                } else {
                    fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
                        override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

                        override fun isCancellationRequested() = false
                    })
                        .addOnSuccessListener { location: Location? ->
                            if (location == null) {
                                Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT)
                                    .show()
                                binding.swLocation.isChecked = false
                            }
                            else {
                                latLong = location
                                binding.btnUpload.setOnClickListener {
                                    upload(true)
                                }
                            }

                        }
                }
            } else {
                binding.btnUpload.setOnClickListener {
                    upload(false)
                }
            }
        }
    }

    private fun startCamera() {
        launcherIntentCameraX.launch(Intent(this, CameraxActivity::class.java))
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean


            result =
                rotateBitmap(
                    BitmapFactory.decodeFile(myFile.path),
                    isBackCamera
                )
        }
        getFile = result?.let { it1 -> bitmapToFile(it1, this) }
        Glide.with(this)
            .load(result)
            .placeholder(R.drawable.story_app_placeholder)
            .into(binding.ivPreviewImg)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile

            Glide.with(this)
                .load(selectedImg)
                .placeholder(R.drawable.story_app_placeholder)
                .into(binding.ivPreviewImg)
        }
    }

    private fun upload(isLocationIncluded: Boolean) {

        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description =
                binding.etUploadDescription.text.toString()
                    .toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            var lat: Float? = null
            var lon: Float? = null

            if (isLocationIncluded) {
                lat = latLong.latitude.toFloat()
                lon = latLong.longitude.toFloat()
            }

            viewModel.getToken().observe(this) { token ->
                viewModel.upload(imageMultipart, description, token, lat, lon).observe(this) {
                    when (it) {
                        is Results.Loading -> showLoading()
                        is Results.Error -> showError(it.message)
                        is Results.Success -> showSuccess()
                    }
                }
            }
        }
    }

    private fun showError(error: String?) {
        binding.pbBlock.visibility = View.GONE
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess() {
        binding.pbBlock.visibility = View.GONE
        Toast.makeText(application, "Success", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showLoading() {
        binding.pbBlock.visibility = View.VISIBLE
    }


    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        private val REQUIRED_PERMISSIONS_LOCATION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}