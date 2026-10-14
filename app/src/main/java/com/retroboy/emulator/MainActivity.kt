
package com.retroboy.emulator

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var status: TextView

    private val chooseGame =
        registerForActivityResult(
            androidx.activity.result.contract
                .ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            if (uri != null) {
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {
                    // El acceso temporal puede seguir disponible.
                }

                status.text =
                    "ROM seleccionada:\n${uri.lastPathSegment}\n\n" +
                    "El motor de emulación se integrará en el siguiente paso."
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(23, 40, 32)
        window.navigationBarColor = Color.rgb(23, 40, 32)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.rgb(23, 40, 32))
        }

        val title = TextView(this).apply {
            text = "RETROBOY"
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(181, 207, 160))
        }

        val subtitle = TextView(this).apply {
            text = "GAME BOY  •  GAME BOY COLOR"
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
        }

        val screen = TextView(this).apply {
            text = "RETROBOY\n\nREADY"
            textSize = 25f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(23, 40, 32))
            setBackgroundColor(Color.rgb(181, 207, 160))
        }

        status = TextView(this).apply {
            text = "Selecciona una ROM .gb o .gbc"
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
        }

        val button = Button(this).apply {
            text = "SELECCIONAR JUEGO"
            setOnClickListener {
                chooseGame.launch(arrayOf("*/*"))
            }
        }

        root.addView(title)
        root.addView(subtitle)

        root.addView(
            screen,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                250
            ).apply {
                setMargins(0, 28, 0, 20)
            }
        )

        root.addView(
            button,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            status,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 20, 0, 0)
            }
        )

        setContentView(root)
    }
}
