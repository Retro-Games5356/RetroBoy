
package com.retroboy.emulator

import android.content.ContentResolver
import android.net.Uri

data class CartridgeInfo(
    val title: String,
    val isColor: Boolean,
    val cartridgeType: Int,
    val romSizeBytes: Int,
    val actualSizeBytes: Int
)

object CartridgeLoader {

    fun load(
        resolver: ContentResolver,
        uri: Uri
    ): CartridgeInfo {
        val rom = resolver.openInputStream(uri)?.use {
            it.readBytes()
        } ?: throw IllegalArgumentException(
            "No se pudo leer el archivo ROM"
        )

        require(rom.size >= 0x150) {
            "La ROM es demasiado pequeña o está dañada"
        }

        val title = String(
            rom,
            0x134,
            0x143 - 0x134,
            Charsets.US_ASCII
        ).trim('\u0000', ' ')

        val colorFlag = rom[0x143].toInt() and 0xFF
        val isColor = colorFlag == 0x80 ||
                      colorFlag == 0xC0

        val cartridgeType = rom[0x147].toInt() and 0xFF
        val romSizeCode = rom[0x148].toInt() and 0xFF

        val romSizeBytes = when (romSizeCode) {
            in 0..8 -> 32 * 1024 * (1 shl romSizeCode)
            0x52 -> 72 * 16 * 1024
            0x53 -> 80 * 16 * 1024
            0x54 -> 96 * 16 * 1024
            else -> 0
        }

        return CartridgeInfo(
            title = title.ifBlank { "Juego sin nombre" },
            isColor = isColor,
            cartridgeType = cartridgeType,
            romSizeBytes = romSizeBytes,
            actualSizeBytes = rom.size
        )
    }
}
