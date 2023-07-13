package com.example.apublic

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.ImageDecoder.Source
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.btn_pick_img)
        imageView = findViewById(R.id.img_view)

        button.setOnClickListener {
            launchBaseDirectoryPicker()
        }

    }


    private var baseDocumentTreeUri: Uri? = null

    private fun launchBaseDirectoryPicker() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            val context: Context = applicationContext

            Log.d("intent data", "${data?.data}")

            val file: File? = data?.data?.path?.let { File(it) }
            val path: Uri? = data?.data
            println("FILE NAME ${getMimeType(file!!)}")
            //val source: Source = ImageDecoder.createSource(file)
            //val bitmap: Bitmap = ImageDecoder.decodeBitmap(source)
            //println("IMAGE BITMAP ${source}")

            saveFileUsingMediaStore(context, file.path, file.name)
        }
    }

    private fun getFileSize(file: File): Long {
        val fileSizeInKb: Long = file.length()

        Log.d("fileSizeInMb", "${fileSizeInKb / 1024}")

        return fileSizeInKb / 1024
    }

    private fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }

        Log.d("fileMimeType and fileName", "type: ${type}, name: ${file.name}")

        return type
    }

    private fun saveFileUsingMediaStore(context: Context, url: String, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(File(url)))
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            URL(url).openStream().use { input ->
                resolver.openOutputStream(uri).use { output ->
                    input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                }
            }
        }
    }
}