package com.lucian.garliclike.items

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.lucian.garliclike.Player
import com.lucian.garliclike.enemies.Enemy

abstract class Weapon (override val name: String, override val description: String) : Item {
    abstract val damage: Int
    abstract val range: Float
    abstract var attackDelay: Float
    abstract var attackTimer: Float
    abstract var isRangedAttackActive: Boolean
    abstract fun attack(target: Enemy, delta: Float, player: Player)
    abstract fun isInRange(playerPosition: Vector2, enemy: Enemy): Boolean
    abstract fun resetAttackFlag()
    abstract fun drawProjectile(batch: SpriteBatch)
}
