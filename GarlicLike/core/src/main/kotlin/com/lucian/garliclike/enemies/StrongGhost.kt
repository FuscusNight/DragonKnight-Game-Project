package com.lucian.garliclike.enemies

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.lucian.garliclike.Player

class StrongGhost : Enemy {
    override var healthPoints: Int = 45
    override var damage: Int = 15
    private val speed = 80f

    private val texture1 = TextureRegion(Texture("assets/characters/StrongGhostAnim1.png"))
    private val texture2 = TextureRegion(Texture("assets/characters/StrongGhostAnim2.png"))
    override var textureRegion: TextureRegion = texture1
    override val width: Float = 42f
    override val height: Float = 52f
    override val hitbox: Rectangle = Rectangle()

    override var dead: Boolean = false

    // Reduce hitbox size from sprite size
    override val hitboxWidthModifier: Float = 0f
    override val hitboxHeightModifier: Float = 0f

    override var position = Vector2(200f, 200f)
    private var animationTime = 0f

    override fun update(delta: Float, playerPosition: Vector2) {
        // delta represents the time in seconds since the last frame was rendered. This value is not a whole number like 1, 2, 3, etc., but rather a small fraction,
        // typically much less than 1. The exact value depends on the frame rate. For example, at 60 frames per second, delta would be approximately 1/60, or about 0.0167 seconds.

        animationTime += delta

        // Calculate direction vector from the ghost to the player
        // Cpy copies player pos then subtracts(-) ghost position to get a vector value
        // that points the ghost towards the player
        // Nor ensures that movement calculations are consistent, regardless of the actual distance
        // Without normalization, the ghost would move faster when farther away from the player and slower when closer,
        // as the vector's length would directly influence the movement.
        val direction = playerPosition.cpy().sub(position).nor()

        // Move the ghost towards the player
        // uses the vector we got earlier and then multiplies times with speed
        // this makes yet another vector that represents the distance the ghost should move this frame to maintain consistent movement speed
        position.add(direction.scl(speed * delta))


        // Switch between two textures to create a simple animation
        // If the current time modulo 1 is less than 0.5, use the first texture; otherwise, use the second
        // 0.0167 x 60 frames is 1 second, so it increases very fast, so roughly for half a second the value will be
        // less than 0.5 when we modules the animationTime with a 1 and then past 0.5 it is higher and thus we get texture 2
        // then this process resets after 1 second past and begins a new and so we flip between the two textures constantly
        textureRegion = if (animationTime % 1f < 0.5f) texture1 else texture2

        // Update the hitbox position and size to match the current position and size of the texture region
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
        println("Strong Ghost took $amount damage. Health now: $healthPoints")
        if (healthPoints <= 0) {
            dead = true
            if (dead)
            {
                println("Strong Ghost is DEAD!")
            }


        }

    }

    override fun dispose() {
        texture1.texture.dispose()
        texture2.texture.dispose()
    }
}
