
package com.retroboy.emulator

/**
 * Primera versión de la CPU Sharp LR35902.
 *
 * Implementa un subconjunto inicial de instrucciones.
 * Todavía no es una CPU completa ni está sincronizada
 * con el hardware de Game Boy.
 */
class Cpu {

    // Memoria temporal de 64 KiB.
    // Después la conectaremos al mapa de memoria real.
    private val memory = ByteArray(0x10000)

    // Registros de 8 bits.
    var a = 0
        private set
    var f = 0
        private set
    var b = 0
        private set
    var c = 0
        private set
    var d = 0
        private set
    var e = 0
        private set
    var h = 0
        private set
    var l = 0
        private set

    // Registros de 16 bits.
    var sp = 0xFFFE
        private set
    var pc = 0x0100
        private set

    var halted = false
        private set

    var totalCycles: Long = 0
        private set

    // Banderas del registro F.
    private val flagZ = 0x80
    private val flagN = 0x40
    private val flagH = 0x20
    private val flagC = 0x10

    private fun read8(address: Int): Int =
        memory[address and 0xFFFF].toInt() and 0xFF

    private fun write8(address: Int, value: Int) {
        memory[address and 0xFFFF] = value.toByte()
    }

    private fun fetch8(): Int {
        val value = read8(pc)
        pc = (pc + 1) and 0xFFFF
        return value
    }

    private fun fetch16(): Int {
        val low = fetch8()
        val high = fetch8()
        return low or (high shl 8)
    }

    private fun getBC() = (b shl 8) or c
    private fun getDE() = (d shl 8) or e
    private fun getHL() = (h shl 8) or l

    private fun setBC(value: Int) {
        b = (value shr 8) and 0xFF
        c = value and 0xFF
    }

    private fun setDE(value: Int) {
        d = (value shr 8) and 0xFF
        e = value and 0xFF
    }

    private fun setHL(value: Int) {
        h = (value shr 8) and 0xFF
        l = value and 0xFF
    }

    private fun setFlags(value: Int) {
        f = value and 0xF0
    }

    /**
     * Ejecuta una instrucción y devuelve sus ciclos de máquina.
     * En Game Boy, cada ciclo de máquina equivale normalmente
     * a cuatro ciclos de reloj de la CPU.
     */
    fun step(): Int {
        if (halted) return 1

        val opcode = fetch8()

        val cycles = when (opcode) {

            // NOP
            0x00 -> 1

            // LD BC, d16
            0x01 -> {
                setBC(fetch16())
                3
            }

            // LD DE, d16
            0x11 -> {
                setDE(fetch16())
                3
            }

            // LD HL, d16
            0x21 -> {
                setHL(fetch16())
                3
            }

            // LD SP, d16
            0x31 -> {
                sp = fetch16()
                3
            }

            // LD r, d8
            0x06 -> { b = fetch8(); 2 }
            0x0E -> { c = fetch8(); 2 }
            0x16 -> { d = fetch8(); 2 }
            0x1E -> { e = fetch8(); 2 }
            0x26 -> { h = fetch8(); 2 }
            0x2E -> { l = fetch8(); 2 }
            0x3E -> { a = fetch8(); 2 }

            // LD (HL), A
            0x77 -> {
                write8(getHL(), a)
                2
            }

            // LD A, (HL)
            0x7E -> {
                a = read8(getHL())
                2
            }

            // LD (a16), A
            0xEA -> {
                write8(fetch16(), a)
                4
            }

            // LD A, (a16)
            0xFA -> {
                a = read8(fetch16())
                4
            }

            // LD (HL-), A
            0x32 -> {
                write8(getHL(), a)
                setHL(getHL() - 1)
                2
            }

            // LD A, (HL+)
            0x2A -> {
                a = read8(getHL())
                setHL(getHL() + 1)
                2
            }

            // XOR A
            0xAF -> {
                a = 0
                setFlags(flagZ)
                1
            }

            // INC A
            0x3C -> {
                val old = a
                a = (a + 1) and 0xFF

                var flags = f and flagC
                if (a == 0) flags = flags or flagZ
                if ((old and 0x0F) == 0x0F) {
                    flags = flags or flagH
                }
                setFlags(flags)
                1
            }

            // DEC A
            0x3D -> {
                val old = a
                a = (a - 1) and 0xFF

                var flags = (f and flagC) or flagN
                if (a == 0) flags = flags or flagZ
                if ((old and 0x0F) == 0) {
                    flags = flags or flagH
                }
                setFlags(flags)
                1
            }

            // JP a16
            0xC3 -> {
                pc = fetch16()
                4
            }

            // JR r8
            0x18 -> {
                val offset = fetch8().toByte().toInt()
                pc = (pc + offset) and 0xFFFF
                3
            }

            // HALT
            0x76 -> {
                halted = true
                1
            }

            else -> {
                val address = (pc - 1) and 0xFFFF
                throw UnsupportedOperationException(
                    "Opcode 0x${opcode.toString(16).uppercase()} " +
                    "no implementado en 0x${address.toString(16).uppercase()}"
                )
            }
        }

        totalCycles += cycles.toLong()
        return cycles
    }

    /**
     * Carga una ROM de prueba en la memoria temporal.
     * Solo copia hasta 32 KiB; todavía no hay bancos de ROM.
     */
    fun loadRom(rom: ByteArray) {
        memory.fill(0)
        val length = minOf(rom.size, 0x8000)
        rom.copyInto(memory, destinationOffset = 0, endIndex = length)

        // Las ROM de Game Boy normalmente comienzan
        // a ejecutar en 0x0100 si no se emula la BIOS.
        pc = 0x0100
        halted = false
        totalCycles = 0
    }

    /**
     * Acceso temporal para pruebas.
     */
    fun readMemory(address: Int): Int = read8(address)

    fun writeMemory(address: Int, value: Int) {
        write8(address, value)
    }
}
