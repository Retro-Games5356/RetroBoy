
package com.retroboy.emulator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CpuTest {

    @Test
    fun executesBasicInstructions() {
        val cpu = Cpu()

        // LD A, 0x42
        // XOR A
        // INC A
        // HALT
        val rom = ByteArray(0x104)
        rom[0x100] = 0x3E.toByte()
        rom[0x101] = 0x42
        rom[0x102] = 0xAF.toByte()
        rom[0x103] = 0x3C

        cpu.loadRom(rom)

        assertEquals(2, cpu.step())
        assertEquals(0x42, cpu.a)

        assertEquals(1, cpu.step())
        assertEquals(0, cpu.a)

        assertEquals(1, cpu.step())
        assertEquals(1, cpu.a)

        assertEquals(0x0104, cpu.pc)
        assertFalse(cpu.halted)
    }

    @Test
    fun haltStopsInstructionExecution() {
        val cpu = Cpu()
        val rom = ByteArray(0x102)

        rom[0x100] = 0x76.toByte()
        cpu.loadRom(rom)

        assertEquals(1, cpu.step())
        assertTrue(cpu.halted)
        assertEquals(0x0101, cpu.pc)
    }
}
