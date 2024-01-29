package com.lucian.garliclike.enemies

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class SkeletonKnife : Enemy  {
    override var healthPoints: Int = 35
    override var damage: Int = 10
    private val speed = 30f

    private val texture1 = TextureRegion(Texture("assets/characters/SkeletonKnifeAnim1.png"))
    private val texture2 = TextureRegion(Texture("assets/characters/SkeletonKnifeAnim2.png"))
    override var textureRegion: TextureRegion = texture1
    override val width: Float = 42f
    override val height: Float = 52f
    override val hitbox: Rectangle = Rectangle()
    override var dead: Boolean = false

    internal val projectiles = mutableListOf<EnemyProjectile>()
    internal val attackRange = 250f
    private var attackTimer = 0f
    private val attackDelay = 1.5f

    // Reduce hitbox size from sprite size
    override val hitboxWidthModifier: Float = 0f
    override val hitboxHeightModifier: Float = 0f

    override var position = Vector2(200f, 200f)
    private var animationTime = 0f

    inner class SkeletonKnifeProjectile(private val startPosition: Vector2, private val target: Vector2) : EnemyProjectile {

        override var projectileTexture = Texture("assets/items/enemyWeapons/Knife.png")
        override var projectileSpeed = 145f
        override val projectileSize = Vector2(15f, 15f)
        override var disposed = false
        override var lifetime = 0f
        override val maxLifeTime = 4f

        private var position = startPosition.cpy()

        // Calculate direction once when the projectile is created
        private val direction = target.cpy().sub(startPosition).nor()

        override fun update(delta: Float, playerPosition: Vector2) {

            lifetime += delta
            if (lifetime > maxLifeTime) {
                disposed = true
                return
            }

            // this bit here makes sure the projectile moves the exact amount it should per frame, regardless of frame rate changes.
            val moveAmount = projectileSpeed * delta
            position.add(direction.x * moveAmount, direction.y * moveAmount)

            if (position.dst(playerPosition) < 10f) {
                disposed = true
            }
        }

        override fun draw(batch: SpriteBatch) {
            if (!disposed) {
                batch.draw(projectileTexture, position.x, position.y, projectileSize.x, projectileSize.y)
            }
        }

        override fun isDisposed(): Boolean = disposed

        override fun dispose() {
            projectileTexture.dispose()
        }

        override fun getPosition(): Vector2 {
            return position
        }
    }


    override fun update(delta: Float, playerPosition: Vector2) {

        animationTime += delta
        attackTimer += delta

        if (shouldAttack(playerPosition) && attackTimer >= attackDelay) {
            projectiles.add(SkeletonKnifeProjectile(position, playerPosition))
            attackTimer = 0f
        }

        projectiles.forEach { it.update(delta, playerPosition) }
        projectiles.removeAll { it.isDisposed() }


        val direction = playerPosition.cpy().sub(position).nor()

        position.add(direction.scl(speed * delta))

        textureRegion = if (animationTime % 1f < 0.5f) texture1 else texture2

        hitbox.set(position.x, position.y, width, height)
        hitbox.setSize(width - hitboxWidthModifier, height - hitboxHeightModifier)

        hitbox.setPosition(position.x, position.y)
    }

    private fun shouldAttack(playerPosition: Vector2): Boolean {
        return Vector2.dst(position.x, position.y, playerPosition.x, playerPosition.y) <= attackRange
    }

    override fun draw(batch: SpriteBatch) {
        batch.draw(textureRegion, position.x, position.y, width, height)

        projectiles.forEach { it.draw(batch) }
    }


    override fun setEnemyPosition(vector2: Vector2) {
        position = Vector2(vector2.x, vector2.y)
    }


    override fun takeDamage(amount: Int) {
        healthPoints -= amount
        println("Knife Skeleton took $amount damage. Health now: $healthPoints")
        if (healthPoints <= 0) {
            dead = true
            if (dead)
            {
                println("Knife Skeleton is DEAD!")
            }
        }

    }

    override fun dispose() {
        texture1.texture.dispose()
        texture2.texture.dispose()
    }
}
