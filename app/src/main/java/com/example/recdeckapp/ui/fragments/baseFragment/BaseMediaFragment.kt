package com.example.recdeckapp.ui.fragments.baseFragment

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.example.recdeckapp.databinding.BottomSheetFileUploadBinding
import com.example.recdeckapp.ui.activities.BaseActivity
import com.example.recdeckapp.utils.MediaPickerUtils
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class BaseMediaFragment : Fragment() {
    protected fun showMediaPickerBottomSheet(
        showCamera: Boolean = true,
        showGallery: Boolean = true,
        showFilePicker: Boolean = false,
    ) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = BottomSheetFileUploadBinding.inflate(LayoutInflater.from(context))
        bottomSheetBinding.ivClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.llCamera.visibility = if (showCamera) View.VISIBLE else View.GONE
        bottomSheetBinding.llGallery.visibility = if (showGallery) View.VISIBLE else View.GONE
        bottomSheetBinding.llfile.visibility = if (showFilePicker) View.VISIBLE else View.GONE
        bottomSheetBinding.imgCameraBottomDialog.setOnClickListener {
            MediaPickerUtils.checkCameraPermission(requireActivity(), this)
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.imgGalleryBottomDialog.setOnClickListener {
            MediaPickerUtils.checkGalleryPermission(requireActivity(), this)
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.imgFileBottomDialog.setOnClickListener {
            MediaPickerUtils.checkFilePermission(requireActivity(), this)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MediaPickerUtils.CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MediaPickerUtils.openCamera(this)
                } else {
                    (activity as? BaseActivity)?.showToast("Camera permission denied")
                }
            }

            MediaPickerUtils.GALLERY_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MediaPickerUtils.openGallery(this)
                } else {
                    (activity as? BaseActivity)?.showToast("Gallery permission denied")
                }
            }

            MediaPickerUtils.FILE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MediaPickerUtils.pickFile(this)
                } else {
                    (activity as? BaseActivity)?.showToast("File permission denied")
                }
            }
        }
    }
}