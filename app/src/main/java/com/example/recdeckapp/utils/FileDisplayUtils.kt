package com.example.recdeckapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.recdeckapp.R
import com.example.recdeckapp.databinding.ItemFileSignupBinding

object FileDisplayUtils {
    fun showSelectedFile(
        uri: Uri,
        context: Context,
        container: ViewGroup,
        onRemove: (Uri) -> Unit
    ): ItemFileSignupBinding {
        val fileName = getFileName(context, uri) ?: "Unknown"
        val fileSize = getFileSize(context, uri)
        val fileType = context.contentResolver.getType(uri) ?: "Unknown"
        val fileBinding = ItemFileSignupBinding.inflate(
            LayoutInflater.from(context),
            container,
            false
        )
        fileBinding.tvFileName.text = fileName
        fileBinding.tvFileName.isSelected = true
        fileBinding.tvFileSize.text = fileSize
        when {
            fileType.startsWith("image/") -> displayImage(context, uri, fileBinding.ivFileIcon)
            fileType == "application/pdf" -> displayPdf(context, uri, fileBinding.ivFileIcon)
            else -> fileBinding.ivFileIcon.setImageResource(R.drawable.ic_file)
        }
        fileBinding.removeLayout.setOnClickListener {
            AlertDialogUtils.showCancelDialog(
                context,
                message = "Are you sure you want to remove this file?",
                onYesClicked = {
                    container.removeView(fileBinding.root)
                    onRemove(uri)
                }
            )
        }
        container.addView(fileBinding.root)
        return fileBinding  // Return the binding
    }

    private fun displayImage(context: Context, uri: Uri, imageView: ImageView) {
        try {
            Glide.with(context)
                .load(uri)
                .error(R.drawable.ic_image)
                .into(imageView)
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.ic_image)
        }
    }

    private fun displayPdf(context: Context, uri: Uri, imageView: ImageView) {
        try {
            val fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            fileDescriptor?.let {
                val pdfRenderer = PdfRenderer(it)
                val page = pdfRenderer.openPage(0)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                imageView.setImageBitmap(bitmap)
                page.close()
                pdfRenderer.close()
            }
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.ic_pdf)
        }
    }

    fun getFileName(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
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

    fun getFileSize(context: Context, uri: Uri): String {
        val fileDescriptor =
            context.contentResolver.openFileDescriptor(uri, "r") ?: return "Unknown size"
        val size = fileDescriptor.statSize
        fileDescriptor.close()
        return if (size > 1024 * 1024) {
            String.format("%.2f MB", size / (1024.0 * 1024.0))
        } else {
            String.format("%.2f KB", size / 1024.0)
        }
    }
}