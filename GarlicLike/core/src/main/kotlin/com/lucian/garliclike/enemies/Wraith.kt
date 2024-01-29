package com.lucian.garliclike.enemies

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.lucian.garliclike.Player

class Wraith : Enemy {
    override var healthPoints: Int = 85
    override var damage: Int = 20
    private val speed = 95f

    private val texture1 = TextureRegion(Texture("assets/characters/WraithAnim1.png"))
    private val texture2 = TextureRegion(Texture("assets/characters/WraithAnim2.png"))
    override var textureRegion: TextureRegion = texture1
    override val width: Float = 52f
    override val height: Float = 62f
    override val hitbox: Rectangle = Rectangle()

    override var dead: Boolean = false

    // Reduce hitbox size from sprite size
    override val hitboxWidthModifier: Float = 0f
    override val hitboxHeightModifier: Float = 0f

    override var position = Vector2(200f, 200f)
    private var animationTime = 0f

    override fun update(delta: Float, playerPosition: Vector2) {

        animationTime += delta

        val direction = playerPosition.cpy().sub(position).nor()

        position.add(direction.scl(speed * delta))

        textureRegion = if (animationTime % 1f < 0.5f) texture1 else texture2

        hitbox.set(position.x, position.y, width, height)
        hitbox.setSize(width - hitboxWidthModifier, height - hitboxHeightModifier)
        // NOTE FUTURE: When you make the ghost be able to move and have a left and right facing sprite this calculation might have to change
        hitbox.setPosition(position.x, position.y)
    }


    override fun draw(batch: SpriteBatch) {
        batch.draw(textureRegion, position.x, position.y, width, height)
    }


    override fun setEnemyPosition(vector2: Vector2) {
        position = Vector2(vector2.x, vector2.y)
    }


    override fun takeDamage(amount: Int) {
        healthPoints -= amount
        println("Wraith took $amount damage. Health now: $healthPoints")
        if (healthPoints <= 0) {
            dead = true
            if (dead)
            {
                println("Wraith is DEAD!")
            }
        }

    }

    override fun dispose() {
        texture1.texture.dispose()
        texture2.texture.dispose()
    }
}
