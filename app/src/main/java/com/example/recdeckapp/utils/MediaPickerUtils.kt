package com.example.recdeckapp.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.DialogPermissionBinding
import java.io.File
import java.io.FileOutputStream

object MediaPickerUtils {
    // Request codes
    const val CAMERA_REQUEST_CODE = 100
    const val GALLERY_REQUEST_CODE = 101
    const val FILE_REQUEST_CODE = 102

    // Temporary storage for camera image
    private var cameraImageUri: Uri? = null
    private var pendingCameraCallback: ((Uri) -> Unit)? = null

    // Permission check methods
    fun checkCameraPermission(
        activity: FragmentActivity,
        fragment: Fragment,
        onGranted: (Uri) -> Unit = {}
    ) {
        pendingCameraCallback = onGranted
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera(fragment)
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            )
        ) {
            showPermissionRationaleDialog(activity, fragment)
        } else {
            fragment.requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    fun checkGalleryPermission(
        activity: FragmentActivity,
        fragment: Fragment,
        onGranted: (Uri) -> Unit = {}
    ) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery(fragment)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                // Show explanation dialog
                showPermissionRationaleDialog(activity, fragment, permission)
            }

            else -> {
                // First time OR "Don't ask again" clicked
                fragment.requestPermissions(arrayOf(permission), GALLERY_REQUEST_CODE)
            }
        }
    }

    fun checkFilePermission(
        activity: FragmentActivity,
        fragment: Fragment,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                pickFile(fragment)
            } else {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:" + activity.packageName)
                    fragment.startActivityForResult(intent, FILE_REQUEST_CODE)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    fragment.startActivityForResult(intent, FILE_REQUEST_CODE)
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                pickFile(fragment)
            } else {
                fragment.requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    FILE_REQUEST_CODE
                )
            }
        }
    }

    private fun showPermissionRationaleDialog(
        activity: FragmentActivity,
        fragment: Fragment,
        permission: String? = null
    ) {
        val dialogBinding = DialogPermissionBinding.inflate(LayoutInflater.from(activity))

        val alertDialog = AlertDialog.Builder(activity)
            .setView(dialogBinding.root)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.tvMessage.text =
            if (permission == Manifest.permission.READ_MEDIA_IMAGES || permission == Manifest.permission.READ_EXTERNAL_STORAGE) {
                activity.getString(R.string.stringPermissionInstructionMessage)
            } else {
                activity.getString(R.string.stringPermissionInstructionMessage)
            }
        dialogBinding.tvGoToSetting.text = activity.getString(R.string.stringGoToSettings)
        dialogBinding.tvCancel.text = activity.getString(R.string.stringCancel)

        dialogBinding.tvGoToSetting.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivity(intent)
            alertDialog.dismiss()
        }

        dialogBinding.tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    // Action methods
    fun openCamera(
        fragment: Fragment,
    ) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fragment.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    fun openGallery(fragment: Fragment) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        fragment.startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    fun pickFile(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/pdf", "text/*"))
        }
        fragment.startActivityForResult(
            Intent.createChooser(intent, "Select Files"),
            FILE_REQUEST_CODE
        )
    }

    // Handle activity result
    fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        context: Context,
        onImageSelected: (Uri) -> Unit,
        onFileSelected: (Uri) -> Unit
    ) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        val uri = saveImageToGallery(context, it)
                        onImageSelected(uri)
                    }
                }

                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        onImageSelected(uri)
                    }
                }

                FILE_REQUEST_CODE -> {
                    data?.let { intent ->
                        if (intent.clipData != null) {
                            for (i in 0 until intent.clipData!!.itemCount) {
                                val uri = intent.clipData!!.getItemAt(i).uri
                                onFileSelected(uri)
                            }
                        } else {
                            intent.data?.let { uri ->
                                onFileSelected(uri)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveImageToGallery(context: Context, bitmap: Bitmap): Uri {
        val imagesFolder = File(context.cacheDir, "images")
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "${System.currentTimeMillis()}.jpg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}