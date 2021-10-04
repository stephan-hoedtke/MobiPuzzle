package com.stho.mobipuzzle.game


data class Move(val fromFieldNumber: Int, val toFieldNumber: Int, val empty: Int) {

    val direction: Direction = Direction.getDirection(fromFieldNumber, toFieldNumber)

    val movingFields: IntArray =
        getMovingFields(fromFieldNumber, toFieldNumber, empty)

    companion object {

        fun create(action: MyAction, empty: Int): Move =
            Move(fromFieldNumber = action.fromFieldNumber, toFieldNumber = action.toFieldNumber, empty)

        private fun getMovingFields(fromFieldNumber: Int, toFieldNumber: Int, empty: Int): IntArray =
            if (toFieldNumber == empty) {
                intArrayOf(fromFieldNumber)
            }
            else {
                val delta = toFieldNumber - fromFieldNumber
                getMovingFields(toFieldNumber, toFieldNumber + delta, empty) + fromFieldNumber
            }
    }
}

