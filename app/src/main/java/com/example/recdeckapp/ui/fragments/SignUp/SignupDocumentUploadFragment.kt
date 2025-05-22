package com.example.recdeckapp.ui.fragments.SignUp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recdeckapp.R
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.recdeckapp.databinding.AlertDialogCustomBinding
import com.example.recdeckapp.databinding.BottomSheetFileUploadBinding
import com.example.recdeckapp.databinding.DialogPermissionBinding
import com.example.recdeckapp.databinding.FragmentSignupDocumentUploadBinding
import com.example.recdeckapp.databinding.ItemFileSignupBinding
import com.example.recdeckapp.ui.activities.SignupActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream


class SignupDocumentUploadFragment : Fragment(R.layout.fragment_signup_document_upload) {

    private var _binding: FragmentSignupDocumentUploadBinding? = null
    private val binding get() = _binding!!

    private val CAMERA_REQUEST_CODE = 100
    private val FILE_REQUEST_CODE = 101

    private var etIdPassNum = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupDocumentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvUploadDocsHeading.visibility = View.GONE

        binding.ivBackArrowAttachDocs.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.uploadLayout.setOnClickListener {
            showBottomSheet("File")
        }

        setupFieldListeners()  // Set up text listeners to update the button state

        // Initially disable the continue button
        binding.btnAttachDocsContinue.isEnabled = false
        binding.btnAttachDocsContinue.alpha = 0.5f

        binding.btnAttachDocsContinue.setOnClickListener {
            (activity as SignupActivity).switchFragment(SignupSuccessFragment())
        }
    }

    private fun showBottomSheet(title: String,) {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = BottomSheetFileUploadBinding.inflate(layoutInflater)

        sheetBinding.imgCameraBottomDialog.setOnClickListener {
            checkCameraPermission()
            dialog.dismiss()
        }

        sheetBinding.imgGalleryBottomDialog.setOnClickListener {
            checkFilePermission()
            dialog.dismiss()
        }

        sheetBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        sheetBinding.tvGalleryBottomDialog.text = title
        //sheetBinding.imgGalleryBottomDialog.setImageResource(imageResId)

        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }

    private fun areAllFieldsFilled(): Boolean {
        val PassNum = binding.etIdPassNum.text.toString().trim()

        // Check if any field is empty
        return PassNum.isNotEmpty()
    }

    private fun setupFieldListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check fields and update button state
                updateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etIdPassNum.addTextChangedListener(textWatcher)

    }
    private fun updateButtonState() {
        if (areAllFieldsFilled()) {
            binding.btnAttachDocsContinue.isEnabled = true
            binding.btnAttachDocsContinue.alpha = 1.0f
        } else {
            binding.btnAttachDocsContinue.isEnabled = false
            binding.btnAttachDocsContinue.alpha = 0.5f
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            // Show a dialog explaining why the permission is needed
            showPermissionRationaleDialog()
        } else {
            // Request permission for the first time
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }


    private fun showPermissionRationaleDialog() {
        val dialogBinding = DialogPermissionBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.tvMessage.text = "Camera permission is required. Please enable it from settings."
        dialogBinding.tvGoToSetting.text = "Go to Settings"
        dialogBinding.tvCancel.text = "Cancel"

        dialogBinding.tvGoToSetting.setOnClickListener {
            // Open app settings to enable permission manually
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
            alertDialog.dismiss()
        }

        dialogBinding.tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    private fun checkFilePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // For Android 11+ (API 30+)
            if (Environment.isExternalStorageManager()) {
                pickFile()
            } else {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:" + requireContext().packageName)
                    startActivityForResult(intent, FILE_REQUEST_CODE)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivityForResult(intent, FILE_REQUEST_CODE)
                }
            }
        } else {
            // For Android 10 and below
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickFile()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    FILE_REQUEST_CODE
                )
            }
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    // Check if the user has denied the permission permanently
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                        showPermissionRationaleDialog()
                    } else {
                        Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }




    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/pdf", "text/*"))
        }
        startActivityForResult(Intent.createChooser(intent, "Select Files"), FILE_REQUEST)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    try {
                        // Get the Bitmap from camera result
                        val imageBitmap = data?.extras?.get("data") as? Bitmap
                        imageBitmap?.let {
                            val imageUri = saveImageToGallery(it)  // Save and get URI
                            showSelectedFile(imageUri)

                            // Debug log to confirm URI
                            Log.d("ImageDebug", "Camera image URI: $imageUri")
                        } ?: run {
                            Toast.makeText(requireContext(), "Camera capture failed", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Error capturing image", Toast.LENGTH_SHORT).show()
                    }
                }

                FILE_REQUEST -> {
                    data?.let {
                        if (it.clipData != null) {
                            for (i in 0 until it.clipData!!.itemCount) {
                                showSelectedFile(it.clipData!!.getItemAt(i).uri)
                            }
                        } else if (it.data != null) {
                            showSelectedFile(it.data!!)
                        }
                    }
                }
            }
        }
    }

    // Modified function to use FileProvider
    private fun saveImageToGallery(bitmap: Bitmap): Uri {
        val imagesFolder = File(requireContext().cacheDir, "images")
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "${System.currentTimeMillis()}.jpg")
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // Use FileProvider to create content:// URI (instead of file://)
            return FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e // Rethrow to handle in caller
        }
    }


    private fun showSelectedFile(uri: Uri) {
        val fileName = getFileName(uri) ?: "Unknown"
        val fileSize = getFileSize(uri)
        val fileType = requireContext().contentResolver.getType(uri) ?: "Unknown"

        val fileBinding = ItemFileSignupBinding.inflate(LayoutInflater.from(requireContext()), binding.fileListContainer, false)
        fileBinding.tvFileName.text = fileName
        fileBinding.tvFileSize.text = fileSize

        // Show image or PDF preview
        if (fileType.startsWith("image/")) {
            try {
                // Debug
                Log.d("ImageDebug", "Trying to load image from URI: $uri")

                // Load with Glide for better handling of different URI types
                Glide.with(requireContext())
                    .load(uri)
                    .error(R.drawable.ic_image) // Fallback image
                    .into(fileBinding.ivFileIcon)

            } catch (e: Exception) {
                e.printStackTrace()
                fileBinding.ivFileIcon.setImageResource(R.drawable.ic_image) // Fallback image
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        } else if (fileType == "application/pdf") {
            try {
                val fileDescriptor = requireContext().contentResolver.openFileDescriptor(uri, "r")
                fileDescriptor?.let {
                    val pdfRenderer = PdfRenderer(it)
                    val page = pdfRenderer.openPage(0)
                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    fileBinding.ivFileIcon.setImageBitmap(bitmap)
                    page.close()
                    pdfRenderer.close()
                }
            } catch (e: Exception) {
                fileBinding.ivFileIcon.setImageResource(R.drawable.ic_pdf)
                e.printStackTrace()
            }
        } else {
            fileBinding.ivFileIcon.setImageResource(R.drawable.ic_file)
        }

        fileBinding.removeLayout.setOnClickListener {
            // Inflate the custom layout using ViewBinding
            val dialogBinding = AlertDialogCustomBinding.inflate(layoutInflater)

            // Build the AlertDialog with ViewBinding
            val alertDialog = AlertDialog.Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

            // Customize dialog window
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Set click listeners for buttons using ViewBinding
            dialogBinding.tvYes.setOnClickListener {
                binding.fileListContainer.removeView(fileBinding.root)
                alertDialog.dismiss()

                // Check the child count and update visibility
                if (binding.fileListContainer.childCount > 0) {
                    binding.tvUploadDocsHeading.visibility = View.VISIBLE
                } else {
                    binding.tvUploadDocsHeading.visibility = View.GONE
                }


            }

            dialogBinding.tvNo.setOnClickListener {
                alertDialog.dismiss()
            }

            // Show the dialog
            alertDialog.show()
        }

        binding.fileListContainer.addView(fileBinding.root)

        // Show the heading if there are files, else hide it
        if (binding.fileListContainer.childCount > 0) {
            binding.tvUploadDocsHeading.visibility = View.VISIBLE
        } else {
            binding.tvUploadDocsHeading.visibility = View.GONE
        }

    }

    private fun getFileName(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex("_display_name")
                if (index != -1) {
                    return it.getString(index)
                }
            }
        }
        return uri.lastPathSegment
    }

    private fun getFileSize(uri: Uri): String {
        val fileDescriptor = requireContext().contentResolver.openFileDescriptor(uri, "r") ?: return "Unknown size"
        val size = fileDescriptor.statSize
        fileDescriptor.close()
        return if (size > 1024 * 1024) {
            String.format("%.2f MB", size / (1024.0 * 1024.0))
        } else {
            String.format("%.2f KB", size / 1024.0)
        }
    }

    companion object {
        const val CAMERA_REQUEST = 1001
        const val FILE_REQUEST = 1002
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
