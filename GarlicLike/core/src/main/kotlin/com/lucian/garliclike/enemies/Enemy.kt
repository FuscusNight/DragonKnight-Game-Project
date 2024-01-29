package com.lucian.garliclike.enemies

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import com.lucian.garliclike.Player

interface Enemy : Disposable {
    var healthPoints: Int
    var damage: Int
    var textureRegion: TextureRegion
    val hitbox: Rectangle
    val hitboxWidthModifier: Float
    val hitboxHeightModifier: Float
    val position: Vector2
    val width: Float
    val height: Float
    val dead: Boolean

    fun takeDamage(amount: Int)
    fun setEnemyPosition(vector2: Vector2)
    fun update(delta: Float, playerPosition: Vector2)
    fun draw(batch: SpriteBatch)
}
