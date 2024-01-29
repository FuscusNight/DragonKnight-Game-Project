package com.lucian.garliclike

import com.lucian.garliclike.enemies.Enemy
import com.lucian.garliclike.enemies.SkeletonKnife

// Manages collision for player touching enemies and their attacks
    // player's weapon attack collisions such as fireball are in the dedicated weapon classes
class CollisionManager(private val player: Player, private val enemies: MutableList<Enemy>) {

    fun checkCollisions() {
        enemies.forEach { enemy ->
            if (enemy.hitbox.overlaps(player.hitbox)) {
                player.takeDamage(enemy.damage)
            }

            if (enemy is SkeletonKnife) {
                enemy.projectiles.forEach { projectile ->
                    val projectilePosition = projectile.getPosition()
                    if (!projectile.isDisposed() && player.hitbox.contains(projectilePosition.x, projectilePosition.y)) {
                        player.takeDamage(enemy.damage) // or a specific projectile damage
                        projectile.dispose() // Dispose of the projectile upon collision
                    }
                }
            }
        }
    }
}

