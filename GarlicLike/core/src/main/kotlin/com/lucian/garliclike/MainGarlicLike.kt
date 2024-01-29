package com.lucian.garliclike

import com.badlogic.gdx.Game

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
/**
 * This class is the main entry point of the application.
 * It extends the Game class, which handles setting and switching screens for the game.
 */
class MainGarlicLike : Game() {
    /**
     * The create method is called when the application is created.
     * Here, we set the initial screen of the game.
     */
    override fun create() {
        setScreen(FirstScreen())
    }
}
