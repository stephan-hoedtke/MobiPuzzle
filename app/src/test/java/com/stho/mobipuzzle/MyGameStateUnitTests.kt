package com.stho.mobipuzzle

import com.stho.mobipuzzle.game.Mode
import com.stho.mobipuzzle.game.MyGame
import com.stho.mobipuzzle.game.MyGameState
import com.stho.mobipuzzle.game.Status
import org.junit.Assert
import org.junit.Test

class MyGameStateUnitTests {

    @Test
    fun plainState_isCorrect() {
        val state = MyGameState.plainValue
        assertStateValue(state, 1, 1)
        assertStateValue(state, 2, 2)
        assertStateValue(state, 9, 9)
        assertStateValue(state, 15, 15)
        assertStateValue(state, 16, 0)
    }

    @Test
    fun moves_areCorrect() {
        val state = MyGameState.defaultValue
        assertGameStatus(state, Status.NEW)

        val game = MyGame(state)
        game.moveTo(16, 15)
            .moveTo(12, 16)
            .moveTo(9, 10)
            .moveTo(13, 9)
            .moveTo(16, 15)

        assertGameStatus(game.gameState, Status.FINISHED)
    }

    @Test
    fun evaluations_areCorrect() {
        val state = MyGameState.plainValue
        assertEvaluation(state, 1.0)

        val game = MyGame(state)
        game.moveTo(15, 16)
        assertEvaluation(game.gameState, 1, 1)

        game.moveTo(13, 14)
        assertEvaluation(game.gameState, 3, 3)

        game.moveTo(9, 13)
            .moveTo(10, 9)
            .moveTo(14, 10)
        assertEvaluation(game.gameState, 5, 6)
    }

    private fun assertEvaluation(state: MyGameState, misplaces: Int, distance: Int) {
        val distancePenalty = distance * 0.001
        val misplacesPenalty = misplaces * 0.001
        val expectedValue = 0.3 - distancePenalty - misplacesPenalty
        assertEvaluation(state, expectedValue)
    }

    private fun assertEvaluation(state: MyGameState, expectedValue: Double) {
        val actualValue = state.evaluate()
        Assert.assertEquals("Evaluation is not correct", expectedValue, actualValue, EVALUATION_DELTA)
    }

    private fun assertGameStatus(state: MyGameState, expectedStatus: Status) {
        val actualStatus = state.status
        Assert.assertEquals("Invalid status", expectedStatus, actualStatus)
    }

    private fun assertStateValue(state: MyGameState, index: Int, expectedValue: Int) {
        val actualValue = state[index]
        Assert.assertEquals("Invalid value at $index", expectedValue, actualValue)
    }

    companion object {
        private const val EVALUATION_DELTA = 0.0000001
    }
}