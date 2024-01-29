package com.lucian.garliclike.enemies

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2

interface EnemyProjectile {
    var projectileTexture : Texture
    var projectileSpeed : Float
    val projectileSize : Vector2
    var disposed : Boolean
    var lifetime : Float
    val maxLifeTime : Float

    fun update(delta: Float, playerPosition: Vector2)
    fun draw(batch: SpriteBatch)
    fun isDisposed(): Boolean
    fun dispose()
    fun getPosition(): Vector2
}
