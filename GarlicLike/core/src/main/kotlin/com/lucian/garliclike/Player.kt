package com.lucian.garliclike

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.lucian.garliclike.enemies.Enemy
import com.lucian.garliclike.items.*


class Player {
    // Player's appearance
    /**
     * Replaced Texture with TextureRegion which allows more advanced drawing capabilities of textures
     * */
    private val textureLeft: TextureRegion = TextureRegion(Texture("assets/characters/dragonMCv1.png"))
    private val textureRight: TextureRegion = TextureRegion(Texture("assets/characters/Dragon_FaceRightMCv1.png"))
    private var currentTexture: TextureRegion = textureLeft

    // Player start position in world
    val position = Vector2(2225f, 2225f)

    // Player Size
    private val width = 64f
    private val height = 64f

    // Reduce Hitbox size modifiers
    private val hitboxWidthModifier = 10f
    private val hitboxHeightModifier = 10f

    // Last safe position before movement
    private var lastSafePosition = Vector2(position.x, position.y)

    // Player Stats
    internal var hp = 100
    private var speed = 200f
    internal val hitbox = Rectangle(
        position.x,
        position.y,
        width,
        height
    ) // Internal, can be used anywhere in project/modules but not other projects
    private var invulnerable = false
    private val invulnerabilityDuration = 1.5f // in seconds
    private var invulnerabilityTimer = 0f
    internal var isAlive = true

    // Player animation
    private var rotation = 0f
    private var rotationSpeed = 980f
    private val maxRotation = 10f // maximum angle (in degrees) that the texture will rotate to either side

    // Player Inventory , mutable list of Item, allows dynamic adding/removing of items
    private val inventory: MutableList<Item> = mutableListOf()

    // init block is executed when an instance of the class is created, It is executed immediately after the primary constructor of the class is called
    // https://medium.com/@ranjeet123/init-block-in-kotlin-518b050cada1
    init {
        // Adds our default weapon to inventory
        addItemToInventory(Cross())
        addItemToInventory(Fireball())
        addItemToInventory(Sword())
    }

    val attackSystem = AutomatedAttackSystem(this)

    // Inventory Functions START
    fun addItemToInventory(item: Item) {
        if (inventory.size < 30) {
            inventory.add(item)
        } else {
            println("Inventory is full")
        }
    }

    fun getInventory(): List<Item> {
        return inventory
    }
    // Inventory Functions END

    // Player Inputs START
    /**
     * Called when the screen should render itself.
     * @param delta Time since last render in seconds.
     * The delta variable is mainly used to make game framerate independent.
     * Framerate independent means that the game behaves the same no matter how many frames per second your game is running at.
     */
    fun handleInput(delta: Float) {
        // Stores last safe position before moving
        lastSafePosition.set(position.x, position.y)

        var isMoving = false
        // UP
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.y += speed * delta
        }
        // DOWN
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            position.y -= speed * delta
        }
        // LEFT
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            position.x -= speed * delta
            currentTexture = textureLeft
        }
        // RIGHT
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            position.x += speed * delta
            currentTexture = textureRight
        }

        // Check if the player is moving
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.S) ||
            Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D)
        ) {
            isMoving = true
        }

        // Update rotation for waddle effect
        if (isMoving) {
            rotation += rotationSpeed * delta
            rotation = rotation % 360 // Modules of 360 makes sure to Keep the rotation within 0-360 degrees
            // all calculation regardless of rotation speed with this calculation make sure it never goes above 360
        } else {
            rotation = 0f // Reset rotation when player stops
        }

        updateHitbox()
    }

    // Player Inputs END
    //
    //                      Why add enemies:List Enemy? Cause AutomatedAttackSystem takes two parameters
    fun update(delta: Float, enemies: List<Enemy>) {
        // resets the new flag in weapons to false so it can attack again
        inventory.filterIsInstance<Weapon>().forEach { it.resetAttackFlag() }

        attackSystem.update(delta, enemies)

        // Update invulnerability timer
        if (invulnerable) {
            invulnerabilityTimer -= delta
            if (invulnerabilityTimer <= 0) {
                invulnerable = false
            }
        }


        // Update hitbox position
        hitbox.setPosition(position.x, position.y)
        // Update hitbox size based on modifiers
        hitbox.setSize(width - hitboxWidthModifier, height - hitboxHeightModifier)
        // This line adjusts the hitbox position so that it aligns with the sprite's center.
        // By subtracting half the width and half the height of the hitbox from the player's position,
        // we move the hitbox's bottom-left corner to align with the sprite's center.
        // This is necessary because the hitbox's position is defined by its bottom-left corner.
        // Unlike lets say the ghost enemy, this had to be done here because of the waddle rotation animation that was made for the player
        // so this extra calculation was needed
        hitbox.setPosition(position.x - hitbox.width / 2, position.y - hitbox.height / 2)


    }
    // HITBOX START
    fun takeDamage(amount: Int) {
        if (!invulnerable) {
            hp -= amount
            println("Player took $amount damage. Health now: $hp")
            invulnerable = true
            invulnerabilityTimer = invulnerabilityDuration
            if (hp <= 0) {
                isAlive = false
                println("Player is DEAD!")
            }
        }
    }

    // Method to reset position after collision
    fun resolveCollision() {
        position.set(lastSafePosition.x, lastSafePosition.y)
        updateHitbox()
    }

    private fun updateHitbox() {
        hitbox.setPosition(position.x, position.y)
    }
    // HITBOX END

    // ATTACK START
    class AutomatedAttackSystem(private val player: Player) {
        // stores pairs of Weapon and Enemy. Each pair represents an ongoing attack where a specific weapon is being used against a specific enemy.
        // pair is like a simpler map, pairs two different things together into a single entity
        private val ongoingAttacks = mutableSetOf<Pair<Weapon, Enemy>>()
        fun update(delta: Float, enemies: List<Enemy>) {
            // retrieves all items from the player's inventory that are instances of the Weapon class.
            val weapons = player.getInventory().filterIsInstance<Weapon>()

            //  iterates over each weapon in the player's inventory.
            weapons.forEach { weapon ->
                //Inside the weapons loop, another loop iterates over each enemy.
                // This results in checking every combination of weapon and enemy.
                enemies.forEach { enemy ->
                    // For each weapon-enemy combination, a pair is created.
                    // The pair represents a potential attack scenario between a weapon and an enemy
                    val attackPair = Pair(weapon, enemy)

                    // Checks if enemy in range of player weapons and if a weapon is attacking already
                    if (weapon.isInRange(player.position, enemy) || ongoingAttacks.contains(attackPair)) {
                        // both true ? ATTACK!
                        weapon.attack(enemy, delta, player)
                        ongoingAttacks.add(attackPair) // Mark as ongoing attack
                    }
                    //After the attack is made, it checks if the weapon's attack is no longer active.
                    if (!weapon.isRangedAttackActive) {
                        //  If the attack is no longer active, the pair is removed from
                        ongoingAttacks.remove(attackPair)
                    }
                }
            }
            // After iterating through all weapon-enemy pairs, this line cleans up the ongoingAttacks set.
            // It removes any pairs where the weapon's attack is no longer active.
            // This ensures that the set only contains currently active attacks.
            ongoingAttacks.removeIf { (weapon, enemy) ->
                !weapon.isRangedAttackActive
            }
        }
    }
    // ATTACK END

    /**
     * In waddleRoation :
     * - Math.sin: The sine function is used here to create an oscillating effect. The sine function oscillates between -1 and 1,
     *  which, when multiplied by maxRotation, creates a smooth wavelike rotation from -maxRotation to +maxRotation.
     *  The sine function is like a wave; it goes up and down smoothly. By using the sine of an angle, we can create a nice wavy motion for the character
     * - Math.toRadians(rotation.toDouble()): The rotation variable is in degrees, but the Math.sin function expects radians.
     * This conversion is necessary for the correct calculation.
     * - .toFloat(): Converts the result to a float, which is the required type for the rotation in LibGDX.
     *
     * originX and originY:
     * Ensure that the texture rotates around its center. If we set these to (0,0), the texture would rotate around its bottom-left corner.
     * */
    fun draw(batch: SpriteBatch) {
        // Ranged attack
        inventory.filterIsInstance<Weapon>().forEach { it.drawProjectile(batch) }

        val waddleRotation = maxRotation * Math.sin(Math.toRadians(rotation.toDouble())).toFloat()

        // Calculate the origin points for rotation (center of the texture)
        val originX = width / 2
        val originY = height / 2

        // Draws the player texture with rotation around its center
        batch.draw(
            currentTexture, //  The texture to draw (either left-facing or right-facing).
            position.x - originX,
            position.y - originY, // Coordinates where to draw the Texture position, facilitate rotation around the sprite's center. This is crucial for the animated waddle effect
            originX,
            originY, // The origin point for rotation (the center of the texture).
            width,
            height, // The dimensions of the texture.
            1f,
            1f, // The scale on the X and Y axes for the sprite/texture. Here, it's set to 1, meaning no scaling is applied.
            waddleRotation //  The rotation angle for the texture. This creates the waddle effect.
        )


    }

    fun dispose() {
        textureLeft.texture.dispose()
        textureRight.texture.dispose()
    }



}
