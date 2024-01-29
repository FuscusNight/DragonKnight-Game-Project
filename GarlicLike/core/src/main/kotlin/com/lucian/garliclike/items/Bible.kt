/*package com.lucian.garliclike.items

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.lucian.garliclike.Player
import com.lucian.garliclike.enemies.Enemy

class Bible: Weapon("Bible", "Power Of Jesus") {
    override val damage: Int = 10
    override val range: Float = 300f // Determines how far the Bible orbits around the player
    override var attackDelay: Float = 0f
    override var attackTimer: Float = 0f
    override var isRangedAttackActive: Boolean = true // Always active

    private val texture = Texture("assets/items/weapons/Bible.png")
    private val orbitRadius = range
    private var orbitAngle = 0f
    private val orbitSpeed = 10 // Degrees per second
    private var playerPosition = Vector2() // Store player's position

    // Adjust the size of the Bible
    private val bibleWidth = 30f
    private val bibleHeight = 30f

    override fun attack(target: Enemy, delta: Float, player: Player) {
        // Update the player's position
        playerPosition.set(player.position)

        // Update the orbiting angle
        orbitAngle += orbitSpeed * delta // Ensure consistent movement regardless of frame rate
        orbitAngle %= 360 // Ensure the angle stays within 0-360 degrees

        // Check for collision with enemies
        val orbitPosition = Vector2(player.position.x + orbitRadius * Math.cos(Math.toRadians(orbitAngle.toDouble())).toFloat(),
            player.position.y + orbitRadius * Math.sin(Math.toRadians(orbitAngle.toDouble())).toFloat())

        if (Vector2.dst(orbitPosition.x, orbitPosition.y, target.position.x, target.position.y) < texture.width / 2f) {
            target.takeDamage(damage)
        }
    }

    override fun resetAttackFlag() {
        // Weapon is always on so no need
    }

    override fun drawProjectile(batch: SpriteBatch) {
        val orbitPosition = Vector2(
            playerPosition.x + orbitRadius * Math.cos(Math.toRadians(orbitAngle.toDouble())).toFloat(),
            playerPosition.y + orbitRadius * Math.sin(Math.toRadians(orbitAngle.toDouble())).toFloat()
        )

        // Draw the Bible with the adjusted size
        batch.draw(texture, orbitPosition.x - bibleWidth / 2, orbitPosition.y - bibleHeight / 2, bibleWidth, bibleHeight)
    }

    override fun isInRange(playerPosition: Vector2, enemy: Enemy): Boolean {
        // This weapon is always in range as it orbits the player
        return true
    }

    fun dispose() {
        texture.dispose()
    }
}*/

