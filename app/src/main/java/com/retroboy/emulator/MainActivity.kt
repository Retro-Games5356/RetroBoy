package com.retroboy.emulator

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var status: TextView

    private val selectRom =
        registerForActivityResultCompat()

    private fun registerForActivityResultCompat(): Nothing? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(0xFF202020.toInt())
        }

        val title = TextView(this).apply {
            text = "RetroBoy"
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
        }

        status = TextView(this).apply {
            text = "Game Boy / Game Boy Color"
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
        }

        val button = Button(this).apply {
            text = "Seleccionar ROM (.gb / .gbc)"
            setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
                startActivityForResult(intent, 100)
            }
        }

        layout.addView(title)
        layout.addView(
            status,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.addView(button)
        setContentView(layout)
    }

    @Deprecated("Usamos este método para mantener compatibilidad")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                status.text = "ROM seleccionada:\n$uri"
            }
        }
    }
}
