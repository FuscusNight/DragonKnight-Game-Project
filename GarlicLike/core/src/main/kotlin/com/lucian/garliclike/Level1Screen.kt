package com.lucian.garliclike

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.Game
import com.badlogic.gdx.Input
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.utils.viewport.FitViewport
import com.lucian.garliclike.enemies.*
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener



class Level1Screen : Screen {

    private lateinit var spriteBatch: SpriteBatch
    private lateinit var stage: Stage
    private lateinit var pauseStage: Stage
    private lateinit var skin: Skin
    private lateinit var camera: OrthographicCamera
    private lateinit var drawHud : DrawHud
    private lateinit var tiledMap: TiledMap
    private lateinit var tiledMapRenderer: OrthogonalTiledMapRenderer
    private lateinit var debug: Debug
    private lateinit var collisionManager: CollisionManager
    private lateinit var tileObjectRender: TileObjectRenderer
    private lateinit var rectangleObjectRender: RectangleObjectRenderer
    private lateinit var enemyFactory: EnemyFactory
    private lateinit var spawnManager: SpawnManager

    internal var isPaused = false
    private val player = Player()
    private val enemies = mutableListOf<Enemy>()
    private val spawnPoints = mutableListOf<Vector2>()
    private val level1Music = Level1Music()

    override fun show() {
    /**THE BIG INSTANTIATING ZONE BE HERE START*/
        tiledMap = TmxMapLoader().load("assets/levels/level1/untitled.tmx")
        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap, 32f / 32f) // 32f / 32f is the unit scale, adjusts per tile size
        // Sprite batches are a collection of sprites
        spriteBatch = SpriteBatch()
        collisionManager = CollisionManager(player, enemies)
        tileObjectRender = TileObjectRenderer(tiledMap, spriteBatch)
        rectangleObjectRender = RectangleObjectRenderer(tiledMap, player)
        drawHud = DrawHud(player , spriteBatch)
        // Enemy creation START
        enemyFactory = EnemyFactory()
        // enemies.add(Ghost().apply { setEnemyPosition(Vector2(1550f, 1450f)) }) manual spawning
        // Loads the Tiled map and gets the 'spawnLocations' layer
        val spawnLayer = tiledMap.layers.get("spawnLocations")
        spawnLayer?.objects?.forEach { obj ->  // obj is spawnLayer FYI reminder
            if (obj.name == "spawn" && obj is RectangleMapObject) {
                // Get the x, y coordinates of the spawn point
                val x = obj.rectangle.x
                val y = obj.rectangle.y
                // Add the spawn point to the list
                spawnPoints.add(Vector2(x, y))
            }
        }
        // Defines world dimensions, for example, 1280x720, using this to avoid image stretching out
        val worldWidth = 1280f
        val worldHeight = 720f
        stage = Stage(FitViewport(worldWidth, worldHeight))

        // Instantiates the spawn manager with the populated spawn points
        spawnManager = SpawnManager(enemyFactory, spawnPoints.toTypedArray())
        // Enemy creation END

        // Initializes the pause menu
        pauseStage = Stage(FitViewport(worldWidth, worldHeight))
        skin = Skin(Gdx.files.internal("assets/menu/GarlicUISkin.json"))

        // A type of camera that provides an orthogonal ie bird perspective (non-perspective) view, which is often used in 2D games.
        camera = OrthographicCamera()
    /**THE BIG INSTANTIATING ZONE BE HERE END*/

        level1Music.playRandomTrack()

        // Some DEBUG left over, should've moved to Debug class
        val collisionObjects = mutableListOf<RectangleMapObject>()
        // List of all object layers that contain rectangle objects for collisions, just used for DEBUGING
        val layersWithRectangles = listOf(
            "fence_objectsLeftnRight",
            "fence_objectsBottomSide",
            "fence_objectsTopSide",
            "buildings",
            "right_CliffSideWall",
            "left_CliffSideWall"
        )
        // Iterates over each layer and add all RectangleMapObjects to the collisionObjects list, just used for DEBUGING
        layersWithRectangles.forEach { layerName ->
            val objectLayer = tiledMap.layers.get(layerName) as MapLayer
            collisionObjects.addAll(objectLayer.objects.getByType(RectangleMapObject::class.java))
        }
        // Pause Menu UI START
        // Create buttons
        val resumeButton = TextButton("Resume", skin)
        val restartButton = TextButton("Restart", skin)
        val exitButton = TextButton("Exit", skin)

        // Adds button listeners
        resumeButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                isPaused = false
            }
        })
        restartButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                (Gdx.app.applicationListener as Game).setScreen(Level1Screen())
            }
        })

        exitButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.exit()
            }
        })

        // Creates a table to layout the buttons, similar to HTML, by default elements are centered
        val table = Table()
        table.setFillParent(true) // makes the table take up the entire parent element ie our entire screen
        table.add(resumeButton).pad(10f).width(150f).height(80f) // adds a widget (like a button) to the table. It places the widget in a cell in the table's grid.
        table.row() //  This moves to the next row in the table.
        table.add(restartButton).pad(10f).width(150f).height(80f)
        table.row()
        table.add(exitButton).pad(10f).width(150f).height(80f)

        // Adds the table to the stage
        pauseStage.addActor(table)
        // Pause Menu UI END

        /**
         * setToOrtho configures camera with an ortho projection, false sets Y as an upward direction
         * Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()) sets how much we see with the camera
         * and 0f is a z axis which is not used in 2D so it is 0
         *
         * camera.update() updates the cameras position each time it is called ie everytime camera properties change (like moving around)
         *
         * https://libgdx.com/wiki/graphics/2d/orthographic-camera
         */
        camera.setToOrtho(false, worldWidth, worldHeight)  // using worldWidth, worldHeight) fixed stretching issue when increasing window size
        camera.update()

        // feeding debug the parameters it needs, camera, player, enemies, colobjects to the debug class so i can render it later
        //debug = Debug(camera, player, enemies, collisionObjects)

    }

    override fun render(delta: Float) {

        // PAUSING seperated from player class, otherwise run into issue of tight coupling with player and Screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused
        }
        if (!player.isAlive) {
            (Gdx.app.applicationListener as Game).setScreen(GameOver())
            return
        }

        if (isPaused) {
            Gdx.input.inputProcessor = pauseStage
            pauseStage.act(delta)
            pauseStage.draw()
            return
        }

        player.handleInput(delta)

        camera.position.set(player.position.x, player.position.y, 0f)
        camera.update()

        // rgba(137,164,119,255) but GDX uses some other value system and had to divide em with 255 to get the RGBA i wanted
        Gdx.gl.glClearColor(/* red = */ 137 / 255f, /* green = */ 164 / 255f, /* blue = */ 119 / 255f, /* alpha = */ 255 / 255f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()

        /** Draw sprites/textures
         * SpriteBatch: It's a utility class in LibGDX used for efficient drawing of 2D images.
         * Why Use SpriteBatch: By using SpriteBatch, we can draw multiple textures (or parts of textures)
         * with a single draw call, which is more efficient and improves performance, especially when rendering many objects.
         * https://libgdx.com/wiki/graphics/2d/spritebatch-textureregions-and-sprites
         *
         *  spriteBatch.projectionMatrix = camera.combined
         *  ^ this ensures that everything drawn with the SpriteBatch is transformed according to the camera's view of the world.
         *  Basically, projection matrix tells the SpriteBatch how to draw objects from the camera's perspective
         */
        spriteBatch.projectionMatrix = camera.combined
        spriteBatch.begin()
        // Render Tile Objects
        tileObjectRender.renderTileObjects("shadows", "shadow")
        tileObjectRender.renderTileObjects("random", "thingy")
        tileObjectRender.renderTileObjects("fence_objectsLeftnRight", "fence")
        tileObjectRender.renderTileObjects("bottomDeadForrest", "tree")
        tileObjectRender.renderTileObjects("topDeadForrest", "tree")
        tileObjectRender.renderTileObjects("fence_objectsTopSide", "fence")
        tileObjectRender.renderTileObjects("fence_objectsBottomSide", "fence")
        tileObjectRender.renderTileObjects("bones", "bone")
        tileObjectRender.renderTileObjects("roadPieces", "pieces")
        tileObjectRender.renderTileObjects("random_nonsolid_decor", "decor")
        tileObjectRender.renderTileObjects("grass", "grass")
        tileObjectRender.renderTileObjects("buildings", "building")
        // Hitbox Rendering
        rectangleObjectRender.renderRectangleObjects("fence_objectsLeftnRight", "fence")
        rectangleObjectRender.renderRectangleObjects("fence_objectsBottomSide", "fence")
        rectangleObjectRender.renderRectangleObjects("fence_objectsTopSide", "fence")
        rectangleObjectRender.renderRectangleObjects("buildings", "building")
        rectangleObjectRender.renderRectangleObjects("right_CliffSideWall", "bottom_rightCliffSideWall")
        rectangleObjectRender.renderRectangleObjects("right_CliffSideWall", "top_rightCliffSideWall")
        rectangleObjectRender.renderRectangleObjects("left_CliffSideWall", "top_leftCliffSideWall")
        rectangleObjectRender.renderRectangleObjects("left_CliffSideWall", "bottom_leftCliffSideWall")

        collisionManager.checkCollisions()

        player.draw(spriteBatch)
        val newEnemies = spawnManager.update(delta, numGhosts = 5, numSkeletons = 2, numStrongGhosts = 2, numWraiths = 1)
        enemies.addAll(newEnemies)

        player.update(delta, enemies)

        // Update and draw enemies
        // Iterators https://kotlinlang.org/docs/iterators.html
        val iterator = enemies.iterator() // This line gets an iterator for the enemies list
        while (iterator.hasNext()) { // The hasNext() method of the iterator checks if there is a next element in the collection. If there is, it returns true
            val enemy = iterator.next()
            // This checks if the current enemy is marked as dead. If so, iterator.remove() is called, which removes the current
            // element(enemy) from the collection. This is safe to do while iterating with an iterator, whereas directly removing elements
            // from a collection during iteration using a basic for-loop can cause a ConcurrentModificationException error.
            // Simpler : Lets us modify a list as we are iterating/looping through it without running into problems
            if (enemy.dead) {
                iterator.remove()
                // Not dead ? Draw the enemy
            } else {
                enemy.update(delta, player.position)
                enemy.draw(spriteBatch)
            }
        }

        // HUDCAMERA CREATION START
        // Sets projection matrix for HUD (fixes HUD onto the screen)
        val hudCamera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        hudCamera.setToOrtho(false) // Set to ortho to match screen coordinates
        hudCamera.update()
        spriteBatch.projectionMatrix = hudCamera.combined
        // Draw HUD elements
        drawHud.drawPlayerHealth()
        drawHud.drawPlayerInventory()

        spriteBatch.end()
        // HUDCAMERA CREATION END

        // DEBUG ZONE START
        //debug.render()
        //shapeRenderer.end()
        // DEBUG ZONE END
    }
    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        pauseStage.viewport.update(width, height, true)

    }

    override fun pause() {
        // Invoked when your application is paused.
    }

    override fun resume() {
        // Invoked when your application is resumed after pause.
    }

    override fun hide() {
        // This method is called when another screen replaces this one.
        level1Music.stopMusic()
    }

    override fun dispose() {
        // Destroy screen's assets here.
        player.dispose()
        level1Music.dispose()
        enemies.forEach { enemy ->
            (enemy as? Disposable)?.dispose() // Cast to Disposable and call dispose if possible
        }
        spriteBatch.dispose()
        spriteBatch.dispose()
        tiledMap.dispose()
        tiledMapRenderer.dispose()
    }
}
