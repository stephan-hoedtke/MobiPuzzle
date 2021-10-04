package com.stho.mobipuzzle.mcts.policies

import com.stho.mobipuzzle.game.MyGameState
import com.stho.mobipuzzle.mcts.IAction
import com.stho.mobipuzzle.mcts.IGameState
import com.stho.mobipuzzle.mcts.ISimulationPolicy

class BestEvaluationSimulationPolicy : ISimulationPolicy {

    /**
     * Requires a not-empty list of actions to choose from
     */
    override fun chooseAction(state: IGameState, actions: List<IAction>): IAction =
        chooseActionWithBestEvaluation(state, actions.toList())

    private fun chooseActionWithBestEvaluation(state: IGameState, actions: List<IAction>): IAction {
        var evaluation = 0.0
        var action: IAction? = null
        actions.forEach {
            val newEvaluation = state.apply(it).evaluate()
            if (action == null || newEvaluation > evaluation) {
                action = it
                evaluation = newEvaluation
            }
        }
        return action!!
    }
}

