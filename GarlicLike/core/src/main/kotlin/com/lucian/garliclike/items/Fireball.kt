package com.lucian.garliclike.items

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import com.lucian.garliclike.Player
import com.lucian.garliclike.enemies.Enemy

class Fireball:Weapon ("Fireball", "A fiery projectile") {
    override val damage: Int = 5
    override val range: Float = 250f
    override var attackDelay: Float = 0.5f
    override var attackTimer: Float = 0f
    override var isRangedAttackActive: Boolean = false

    // Fix for calling auto attack multiple times making it go crazy fast when multiple enemies around
    private var hasAttackedThisFrame: Boolean = false

    private val texture = Texture("assets/items/weapons/Fireball.png")
    private var projectilePosition: Vector2? = null
    private var targetEnemy: Enemy? = null
    private val projectileSize = Vector2(20f, 20f)

    override fun attack(target: Enemy, delta: Float, player: Player) {
        // purpose is to immediately exit the attack method if hasAttackedThisFrame is true
        if (hasAttackedThisFrame) {
            return
        }

        attackTimer += delta // Increment timer every frame

        if (isRangedAttackActive) {
            moveProjectileTowardsTarget(delta)
            checkCollisionWithTarget()
        } else {
            // Trigger the fireball if the attack delay has passed and the target is in range
            if (attackTimer >= attackDelay && isInRange(player.position, target)) {
                projectilePosition = Vector2(player.position)
                targetEnemy = target
                isRangedAttackActive = true
                attackTimer = 0f // Reset timer after attack is launched
            }
        }

        if (target.dead) {
            resetProjectile()
        }
        hasAttackedThisFrame = true
    }
    override fun resetAttackFlag() {
        hasAttackedThisFrame = false
    }

    /**
     * Remember, "?." is safecall to avoid NullPointerException
    * let is part of Kotlin that executes a block of code if the object (in this case, targetEnemy is not null).
        * use of the safe call operator?. combined with let. means that let will only be executed if targetEnemy and projectilePosition are not null
     * let: Context Object: Accessed via it (or a custom name). Returns: Can return result of the lambda block {} without assigning it to a new variable.
     * https://kotlinlang.org/docs/scope-functions.html (let, it, run, with, apply, and also.)
    */
    private fun moveProjectileTowardsTarget(delta: Float) {
        // checks if targetEnemy is not null and then executes the code block with enemy as the non-null targetEnemy.
        targetEnemy?.let { enemy -> // targetEnemy renamed to enemy in the lambda
            projectilePosition?.let { position -> // projectilePosition renamed to position in the lambda
                // calculates the line direction from the fireball to the enemy.
                // Vector2(enemy.position) creates a new vector at the enemy's position.
                    // sub(position) subtracts the fireball's position from this vector  resulting in a vector pointing from the fireball to the enemy
                    // .nor() normalizes this vector to have a length of 1, which is useful for ensuring that the fireball moves consistently towards the enemy, no matter how far away the enemy is.
                val direction = Vector2(enemy.position).sub(position).nor()
                val speed = 200 // speed of the fireball
                // updates the fireball's position depending on speed and last rendered delta frame.
                position.add(direction.scl(speed * delta))
            }
        }
    }

    // Just draws the fireball
        // "it" refers to the projectilePosition
        // In Java, lambda expressions always require the arrow syntax (->), even for single-parameter lambdas but not in Kotlin
        // In scope functions like let (and also), if we're fine with the default parameter name it, you don't need to use ->
    override fun drawProjectile(batch: SpriteBatch) {
        projectilePosition?.let {
            batch.draw(texture, it.x, it.y, projectileSize.x, projectileSize.y)
        }
    }

    private fun checkCollisionWithTarget() {
        targetEnemy?.let { enemy ->
            // Check if close enough to the enemy
            if (projectilePosition?.dst(enemy.position)!! < 10f) { //  // Proximity threshold for collision
                enemy.takeDamage(damage)
                resetProjectile()
            }
        }
    }

    private fun resetProjectile() {
        projectilePosition = null
        targetEnemy = null
        isRangedAttackActive = false // Reset the active flag
    }

    override fun isInRange(playerPosition: Vector2, enemy: Enemy): Boolean {
        val attackRange = Circle(playerPosition.x, playerPosition.y, range)
        return attackRange.contains(enemy.position)
    }

    fun dispose() {
        texture.dispose()
    }
}
