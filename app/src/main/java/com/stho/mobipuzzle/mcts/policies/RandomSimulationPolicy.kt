package com.stho.mobipuzzle.mcts.policies

import com.stho.mobipuzzle.mcts.IAction
import com.stho.mobipuzzle.mcts.ISimulationPolicy
import kotlin.random.Random

class RandomSimulationPolicy : ISimulationPolicy {

    /**
     * Requires a not-empty list of actions to choose from
     */
    override fun chooseAction(actions: List<IAction>): IAction =
        chooseActionRandomly(actions.toList())

    private fun chooseActionRandomly(actions: List<IAction>): IAction =
        actions[Random.nextInt(actions.size)]
}

