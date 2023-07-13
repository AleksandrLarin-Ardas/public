package com.example.apublic

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.btn_pick_img)
        imageView = findViewById(R.id.img_view)

        button.setOnClickListener {
            saveFileUsingMediaStore(it.context)
        }
    }

    private fun saveFileUsingMediaStore(context: Context) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Hey, this is a copy")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val file = File(context.filesDir, "304_attachment_1.pdf")
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            file.toURI().toURL()?.openStream().use { input ->
                resolver.openOutputStream(uri).use { output ->
                    input?.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                }
            }
        }
    }
}