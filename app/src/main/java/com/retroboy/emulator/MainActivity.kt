
package com.retroboy.emulator

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.graphics.Color
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.database.Cursor
import android.content.ContentUris
import android.widget.Toast
import android.provider.DocumentsContract.Document
import android.content.ContentResolver

class MainActivity : Activity() {

    private lateinit var layout: LinearLayout
    private lateinit var status: TextView
    private lateinit var gamesList: LinearLayout

    companion object {
        private const val PICK_FOLDER = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 32, 24, 24)
            setBackgroundColor(Color.rgb(32, 32, 32))
        }

        val title = TextView(this).apply {
            text = "RetroBoy"
            textSize = 30f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
        }

        status = TextView(this).apply {
            text = "Selecciona tu carpeta de juegos"
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(Color.LTGRAY)
            setPadding(0, 16, 0, 16)
        }

        val chooseButton = Button(this).apply {
            text = "Elegir carpeta de ROMs"
            setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                startActivityForResult(intent, PICK_FOLDER)
            }
        }

        gamesList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scroll = ScrollView(this).apply {
            addView(
                gamesList,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(chooseButton)
        layout.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(layout)
    }

    @Deprecated("Selector de carpetas compatible")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FOLDER && resultCode == RESULT_OK) {
            val treeUri = data?.data ?: return

            val flags = data.flags and (
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            contentResolver.takePersistableUriPermission(
                treeUri,
                flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            listRoms(treeUri)
        }
    }

    private fun listRoms(treeUri: Uri) {
        gamesList.removeAllViews()

        val documentId = DocumentsContract.getTreeDocumentId(treeUri)
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            treeUri,
            documentId
        )

        var count = 0

        contentResolver.query(
            childrenUri,
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE
            ),
            null,
            null,
            null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID
            )
            val nameIndex = cursor.getColumnIndex(
                DocumentsContract.Document.COLUMN_DISPLAY_NAME
            )
            val mimeIndex = cursor.getColumnIndex(
                DocumentsContract.Document.COLUMN_MIME_TYPE
            )

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex) ?: continue
                val mime = cursor.getString(mimeIndex) ?: ""
                val id = cursor.getString(idIndex) ?: continue

                if (mime == DocumentsContract.Document.MIME_TYPE_DIR) {
                    continue
                }

                if (
                    name.endsWith(".gb", true) ||
                    name.endsWith(".gbc", true)
                ) {
                    val fileUri =
                        DocumentsContract.buildDocumentUriUsingTree(treeUri, id)

                    val button = Button(this).apply {
    text = name

    setOnClickListener {
        status.text = "Cargando ROM...\n$name"

        try {
            val cartridge = CartridgeLoader.load(
                contentResolver,
                fileUri
            )

            val mode = if (cartridge.isColor) {
                "Game Boy Color"
            } else {
                "Game Boy"
            }

            status.text = """
                ROM cargada correctamente

                Juego: ${cartridge.title}
                Archivo: $name
                Sistema: $mode
                Tipo de cartucho: 0x${
                    cartridge.cartridgeType.toString(16).uppercase()
                }
                Tamaño leído: ${cartridge.actualSizeBytes} bytes
                Tamaño declarado: ${cartridge.romSizeBytes} bytes

                Próximo paso: CPU del emulador.
            """.trimIndent()

        } catch (e: Exception) {
            status.text = "Error al cargar la ROM:\n${e.message}"
        }
    }
}

gamesList.addView(button)
count++
                }
            }
        }

        status.text = if (count > 0) {
            "Juegos encontrados: $count"
        } else {
            "No se encontraron ROMs .gb o .gbc en esta carpeta."
        }
    }
}
