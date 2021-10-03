package com.stho.mobipuzzle.game

import com.stho.mobipuzzle.mcts.IAction
import java.security.InvalidParameterException

data class MyAction(val fromFieldNumber: Int, val toFieldNumber: Int): IAction {

    override fun isEqualTo(other: IAction): Boolean =
        isEqualTo(other as MyAction)

    private fun isEqualTo(other: MyAction): Boolean =
        fromFieldNumber == other.fromFieldNumber && toFieldNumber == other.toFieldNumber

    override fun toString(): String {
        return "($fromFieldNumber-$toFieldNumber)"
    }

    companion object {

        fun getActionsFor(empty: Int): Array<MyAction> =
            when (empty) {
                1 -> actionsFor1
                2 -> actionsFor2
                3 -> actionsFor3
                4 -> actionsFor4
                5 -> actionsFor5
                6 -> actionsFor6
                7 -> actionsFor7
                8 -> actionsFor8
                9 -> actionsFor9
                10 -> actionsFor10
                11 -> actionsFor11
                12 -> actionsFor12
                13 -> actionsFor13
                14 -> actionsFor14
                15 -> actionsFor15
                16 -> actionsFor16
                else -> throw InvalidParameterException("Actions for invalid field $empty, expected range: 1..16")
            }

        private val actionsFor1 =
            arrayOf(MyAction(2, 1), MyAction(3, 2), MyAction(4, 3), MyAction(5, 1), MyAction(9, 5), MyAction(13, 9))

        private val actionsFor2 =
            arrayOf(MyAction(1, 2), MyAction(3, 2), MyAction(4, 3), MyAction(6, 2), MyAction(10, 6), MyAction(14, 10))

        private val actionsFor3 =
            arrayOf(MyAction(1, 2), MyAction(2, 3), MyAction(4, 3), MyAction(7, 3), MyAction(11, 7), MyAction(15, 11))

        private val actionsFor4 =
            arrayOf(MyAction(1, 2), MyAction(2, 3), MyAction(3, 4), MyAction(8, 4), MyAction(12, 8), MyAction(16, 12))

        private val actionsFor5 =
            arrayOf(MyAction(6, 5), MyAction(7, 6), MyAction(8, 7), MyAction(1, 5), MyAction(9, 5), MyAction(13, 9))

        private val actionsFor6 =
            arrayOf(MyAction(5, 6), MyAction(7, 6), MyAction(8, 7), MyAction(2, 6), MyAction(10, 6), MyAction(14, 10))

        private val actionsFor7 =
            arrayOf(MyAction(5, 6), MyAction(6, 7), MyAction(8, 7), MyAction(3, 7), MyAction(11, 7), MyAction(15, 11))

        private val actionsFor8 =
            arrayOf(MyAction(5, 6), MyAction(6, 7), MyAction(7, 8), MyAction(4, 8), MyAction(12, 8), MyAction(16, 12))

        private val actionsFor9 =
            arrayOf(MyAction(10, 9), MyAction(11, 10), MyAction(12, 11), MyAction(1, 5), MyAction(5, 9), MyAction(13, 9))

        private val actionsFor10 =
            arrayOf(MyAction(9, 10), MyAction(11, 10), MyAction(12, 11), MyAction(2, 6), MyAction(6, 10), MyAction(14, 10))

        private val actionsFor11 =
            arrayOf(MyAction(9, 10), MyAction(10, 11), MyAction(12, 11), MyAction(3, 7), MyAction(7, 11), MyAction(15, 11))

        private val actionsFor12 =
            arrayOf(MyAction(9, 10), MyAction(10, 11), MyAction(11, 12), MyAction(4, 8), MyAction(8, 12), MyAction(16, 12))

        private val actionsFor13 =
            arrayOf(MyAction(14, 13), MyAction(15, 14), MyAction(16, 15), MyAction(1, 5), MyAction(5, 9), MyAction(9, 13))

        private val actionsFor14 =
            arrayOf(MyAction(13, 14), MyAction(15, 14), MyAction(16, 15), MyAction(2, 6), MyAction(6, 10), MyAction(10, 14))

        private val actionsFor15 =
            arrayOf(MyAction(13, 14), MyAction(14, 15), MyAction(16, 15), MyAction(3, 7), MyAction(7, 11), MyAction(11, 15))

        private val actionsFor16 =
            arrayOf(MyAction(13, 14), MyAction(14, 15), MyAction(15, 16), MyAction(4, 8), MyAction(8, 12), MyAction(12, 16))

    }
}
