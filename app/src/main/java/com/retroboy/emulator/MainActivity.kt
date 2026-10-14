package com.retroboy.emulator

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "RetroBoy"
            textSize = 32f
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Emulador Game Boy y Game Boy Color"
            textSize = 16f
            gravity = Gravity.CENTER
        }

        val selectButton = Button(this).apply {
            text = "Seleccionar ROM"
            setOnClickListener {
                val message = TextView(this@MainActivity).apply {
                    text = "El selector de juegos se añadirá en el siguiente paso."
                    textSize = 16f
                    gravity = Gravity.CENTER
                }

                layout.addView(
                    message,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }

        layout.addView(
            title,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.addView(
            subtitle,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.addView(
            selectButton,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        setContentView(layout)
    }
}
