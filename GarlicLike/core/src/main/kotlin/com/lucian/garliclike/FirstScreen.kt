package com.lucian.garliclike

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport

/**
 * First screen of the application, displayed after the application is created.
 * Implements the Screen interface for handling screen lifecycle events.
 * This'll be the main menu screen at boot
 */
class FirstScreen : Screen {
    // Lateinit vars are used for properties that will be initialized later.
    // Basically, preparing variables ahead of time that we'll use but not assigning anything to them yet
    lateinit var backgroundTexture: Texture
    private lateinit var backgroundMusic: Music

    lateinit var stage: Stage
    lateinit var skin: Skin // Load a skin or create a new one

    /**
     * Called when this screen becomes the current screen.
     * Here we initialize textures, stage, skin, and UI elements.
     */
    override fun show() {
        // Defines world dimensions, for example, 800x600, using this to avoid image stretching out
            // Very buggy atm, might just end up keeping it simply always to 800 x 600
        val worldWidth = 800f
        val worldHeight = 600f
        stage = Stage(FitViewport(worldWidth, worldHeight))

        // Prepares the screen here.
        backgroundTexture = Texture("assets/menu/dragonKnightTitleMenuSplash.png")

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/xDeviruchi - Title Theme .wav"))
        backgroundMusic.isLooping = true
        backgroundMusic.volume = 0.3f
        backgroundMusic.play()


        // Initialize the stage. It's a container for UI elements.
        stage = Stage()

        // Loads the skin, which contains styles for UI widgets.
        skin = Skin(Gdx.files.internal("assets/menu/GarlicUISkin.json"))

        // Creates the buttons using the skin.
        val startButton = TextButton("Start", skin)
        startButton.setSize(110F, 50f)
        val exitButton = TextButton("Exit", skin)
        exitButton.setSize(110f, 50f)

        // Positions the buttons
        startButton.setPosition(100f, 160f)
        exitButton.setPosition(100f, 100f)

        /** Adds listeners to buttons
         * object : ClickListener() Creates anonymous class that extends "ClickListener"
         * An anonymous inner class is a way to extend a class or implement
         * an interface without having to declare a separate named class.
         * In this case, it's creating a subclass of ClickListener
         * https://www.baeldung.com/kotlin/anonymous-objects
         */
        startButton.addListener(object : ClickListener() {
                                // What kind of Input it is , X n Y are positions of the input
                                                 // ? means event can be null
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                                    (Gdx.app.applicationListener as Game).setScreen(Level1Screen())
            }
        })
        exitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.exit()
            }
        })

        // Adds buttons to the stage
        stage.addActor(startButton)
        stage.addActor(exitButton)
        Gdx.input.inputProcessor = stage
    }

    /**
     * Called when the screen should render itself.
     *  The function takes one parameter named delta of type Float. This delta parameter is automatically
     *  passed in by the LibGDX framework when it calls this function.
     *  It represents the time in seconds since the last time the render method was called.
     *
     *  Since the framework calls the render method, we don't manually pass the delta value.
     *  It's handled internally by LibGDX, which calculates the time elapsed since the last frame and provides it as the delta argument.
     */
    override fun render(delta: Float) {
        // OpenGL calls set the clear color (the color used to clear the screen) and clear the screen
        // https://libgdx.com/wiki/graphics/clearing-the-screen
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f) // sets the clear color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT) // clears the screen with the current clear color.

        // Draw the background texture.
        stage.batch.begin() // Begins a new batch of drawing, like  starting a painting session.
        stage.batch.draw(backgroundTexture, 0f, 0f, 1280f, 720f) // Draws the background texture at the specified position
        stage.batch.end() // Ends the batch of drawing. This is like saying "I'm done with this painting session."

        // Update and draw the stage (UI elements).
        // This updates the stage and all its actors. It takes the time
        // since the last frame (delta time) to update animations and positions.
                           // If FPS drops below 30, maintains game update logic at minimum 30 fps still
        stage.act(Math.min(Gdx.graphics.deltaTime, 1 / 30f))
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)

    }

    override fun pause() {
        // Invoked when your application is paused.
    }

    override fun resume() {
        // Invoked when your application is resumed after pause.
    }

    override fun hide() {
        // This method is called when another screen replaces this one.
        backgroundMusic.stop()
    }

    override fun dispose() {
        // Destroy screen's assets here.

        backgroundMusic.dispose()
        backgroundTexture.dispose()
        stage.dispose()
        skin.dispose()
    }
}
