package com.stho.mobipuzzle.game

data class MyBoard(private val data: ULong) {

    fun getValue(index: Int): Int =
        when (index) {
            1 -> ((data shr 0) and 0x000000000000000FUL).toInt()
            2 -> ((data shr 4) and 0x000000000000000FUL).toInt()
            3 -> ((data shr 8) and 0x000000000000000FUL).toInt()
            4 -> ((data shr 12) and 0x000000000000000FUL).toInt()
            5 -> ((data shr 16) and 0x000000000000000FUL).toInt()
            6 -> ((data shr 20) and 0x000000000000000FUL).toInt()
            7 -> ((data shr 24) and 0x000000000000000FUL).toInt()
            8 -> ((data shr 28) and 0x000000000000000FUL).toInt()
            9 -> ((data shr 32) and 0x000000000000000FUL).toInt()
            10 -> ((data shr 36) and 0x000000000000000FUL).toInt()
            11 -> ((data shr 40) and 0x000000000000000FUL).toInt()
            12 -> ((data shr 44) and 0x000000000000000FUL).toInt()
            13 -> ((data shr 48) and 0x000000000000000FUL).toInt()
            14 -> ((data shr 52) and 0x000000000000000FUL).toInt()
            15 -> ((data shr 56) and 0x000000000000000FUL).toInt()
            16 -> ((data shr 60) and 0x000000000000000FUL).toInt()
            else -> 0
        }

    fun setValue(index: Int, value: Int): MyBoard =
        when (index) {
            1 -> MyBoard((data and 0xFFFFFFFFFFFFFFF0UL) or ((value and 0x0F).toULong() shl 0))
            2 -> MyBoard((data and 0xFFFFFFFFFFFFFF0FUL) or ((value and 0x0F).toULong() shl 4))
            3 -> MyBoard((data and 0xFFFFFFFFFFFFF0FFUL) or ((value and 0x0F).toULong() shl 8))
            4 -> MyBoard((data and 0xFFFFFFFFFFFF0FFFUL) or ((value and 0x0F).toULong() shl 12))
            5 -> MyBoard((data and 0xFFFFFFFFFFF0FFFFUL) or ((value and 0x0F).toULong() shl 16))
            6 -> MyBoard((data and 0xFFFFFFFFFF0FFFFFUL) or ((value and 0x0F).toULong() shl 20))
            7 -> MyBoard((data and 0xFFFFFFFFF0FFFFFFUL) or ((value and 0x0F).toULong() shl 24))
            8 -> MyBoard((data and 0xFFFFFFFF0FFFFFFFUL) or ((value and 0x0F).toULong() shl 28))
            9 -> MyBoard((data and 0xFFFFFFF0FFFFFFFFUL) or ((value and 0x0F).toULong() shl 32))
            10 -> MyBoard((data and 0xFFFFFF0FFFFFFFFFUL) or ((value and 0x0F).toULong() shl 36))
            11 -> MyBoard((data and 0xFFFFF0FFFFFFFFFFUL) or ((value and 0x0F).toULong() shl 40))
            12 -> MyBoard((data and 0xFFFF0FFFFFFFFFFFUL) or ((value and 0x0F).toULong() shl 44))
            13 -> MyBoard((data and 0xFFF0FFFFFFFFFFFFUL) or ((value and 0x0F).toULong() shl 48))
            14 -> MyBoard((data and 0xFF0FFFFFFFFFFFFFUL) or ((value and 0x0F).toULong() shl 52))
            15 -> MyBoard((data and 0xF0FFFFFFFFFFFFFFUL) or ((value and 0x0F).toULong() shl 56))
            16 -> MyBoard((data and 0x0FFFFFFFFFFFFFFFUL) or ((value and 0x0F).toULong() shl 60))
            else -> MyBoard(0x0UL)
        }

    fun setEmpty(index: Int): MyBoard =
        setValue(index, 0)

    fun isEmpty(index: Int) =
        getValue(index) == 0

    fun toIntArray(): IntArray =
        intArrayOf(
            getValue(1),
            getValue(2),
            getValue(3),
            getValue(4),
            getValue(5),
            getValue(6),
            getValue(7),
            getValue(8),
            getValue(9),
            getValue(10),
            getValue(11),
            getValue(12),
            getValue(13),
            getValue(14),
            getValue(15),
            getValue(16),
        )

    fun swap(i: Int, j: Int): MyBoard {
        val x = getValue(i)
        val y = getValue(j)
        return setValue(i, y).setValue(j, x)
    }

    fun move(action: MyAction): MyBoard =
        move(from = action.fromFieldNumber, to = action.toFieldNumber)

    fun move(from: Int, to: Int): MyBoard =
        setValue(to, value = getValue(from)).setValue(from,value = 0)

    fun isEqualTo(other: MyBoard): Boolean =
        data == other.data

    fun getIndexOf(value: Int): Int =
        (1..16).firstOrNull { value == getValue(it) } ?: -1

    companion object {
        /**
         * Creates a new MyField from an IntArray with a permutation of the numbers 1 to 16
         */
        fun fromArray(array: IntArray): MyBoard =
            MyBoard(0x0UL)
                .setValue(1, array[0])
                .setValue(2, array[1])
                .setValue(3, array[2])
                .setValue(4, array[3])
                .setValue(5, array[4])
                .setValue(6, array[5])
                .setValue(7, array[6])
                .setValue(8, array[7])
                .setValue(9, array[8])
                .setValue(10, array[9])
                .setValue(11, array[10])
                .setValue(12, array[11])
                .setValue(13, array[12])
                .setValue(14, array[13])
                .setValue(15, array[14])
                .setValue(16, array[15])
    }
}

