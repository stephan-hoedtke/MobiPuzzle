package com.stho.mobipuzzle

import com.stho.mobipuzzle.game.Mode

class Settings(
    var mode: Mode = Mode.NUMBERS,
    var theme: Theme = Theme.WHITE,
    var showCongratulation: Boolean = true,
    var showEngineDetails: Boolean = false,
    var showBestAction: Boolean = false,
)

