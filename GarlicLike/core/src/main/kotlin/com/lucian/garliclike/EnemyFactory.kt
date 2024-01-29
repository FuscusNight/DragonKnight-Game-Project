package com.lucian.garliclike

import com.badlogic.gdx.math.Vector2
import com.lucian.garliclike.enemies.Ghost
import com.lucian.garliclike.enemies.SkeletonKnife
import com.lucian.garliclike.enemies.StrongGhost
import com.lucian.garliclike.enemies.Wraith

// Apply performs initialization (or other operations) on a newly created object and then returns that object
// Apply function returns the object it was called on after executing the block.
// This is useful for initializing objects or configuring them before use.
class EnemyFactory {
    // createGhost creates a Ghost object at a specific position.
    fun createGhost(position: Vector2): Ghost {
        // 'apply' is used to configure the Ghost object before it is returned.
        return Ghost().apply { setEnemyPosition(position) }
    }

    fun createSkeletonKnife(position: Vector2): SkeletonKnife {
        return SkeletonKnife().apply { setEnemyPosition(position) }
    }

    fun createStrongGhost(position: Vector2): StrongGhost {
        return StrongGhost().apply { setEnemyPosition(position) }
    }

    fun createWraith(position: Vector2): Wraith {
        return Wraith().apply { setEnemyPosition(position) }
    }
}

