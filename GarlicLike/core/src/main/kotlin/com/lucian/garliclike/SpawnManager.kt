package com.lucian.garliclike

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.lucian.garliclike.enemies.Enemy

class SpawnManager(
    private val enemyFactory: EnemyFactory,
    private val spawnLocations: Array<Vector2>
) {
    private var totalTimeElapsed = 0f
    private val globalSpawnLimit = 3 * 60f // 3 minutes in seconds

    private var timeSinceLastGhostSpawn = 0f
    private val ghostSpawnInterval = 10f

    private var timeSinceLastWraithSpawn = 0f
    private val wraithSpawnInterval = 60f

    private var timeSinceLastStrongGhostSpawn = 0f
    private val strongGhostSpawnInterval = 35f

    private var timeSinceSkelletonKnifeSpawn = 0f
    private val skelletonKnifeSpawnInterval = 15f

    // update is called regularly to manage spawning of enemies.
    fun update(delta: Float, numGhosts: Int, numSkeletons: Int, numStrongGhosts: Int, numWraiths: Int): List<Enemy> {
        totalTimeElapsed += delta
        val spawnedEnemies = mutableListOf<Enemy>()

        if (totalTimeElapsed < globalSpawnLimit) {
            if (timeSinceLastGhostSpawn >= ghostSpawnInterval) {
                // uses the EnemyFactory instance to create new enemy objects at the appropriate times and locations
                spawnEnemies(numGhosts, enemyFactory::createGhost, spawnedEnemies)
                timeSinceLastGhostSpawn = 0f
            }

            if (timeSinceLastWraithSpawn >= wraithSpawnInterval) {
                spawnEnemies(numWraiths, enemyFactory::createWraith, spawnedEnemies)
                timeSinceLastWraithSpawn = 0f
            }

            if (timeSinceLastStrongGhostSpawn >= strongGhostSpawnInterval) {
                spawnEnemies(numStrongGhosts, enemyFactory::createStrongGhost, spawnedEnemies)
                timeSinceLastStrongGhostSpawn = 0f
            }

            if (timeSinceSkelletonKnifeSpawn >= skelletonKnifeSpawnInterval ) {
                spawnEnemies(numSkeletons, enemyFactory::createSkeletonKnife, spawnedEnemies)
                timeSinceSkelletonKnifeSpawn = 0f
            }
            // Increments individual timers
            timeSinceLastGhostSpawn += delta
            timeSinceLastWraithSpawn += delta
            timeSinceLastStrongGhostSpawn += delta
            timeSinceSkelletonKnifeSpawn += delta

        }

        return spawnedEnemies
    }

    private fun spawnEnemies(count: Int, createEnemy: (Vector2) -> Enemy, list: MutableList<Enemy>) {
        // 1..count is a range expression in Kotlin. It creates a range starting at 1 and ending at count.
        // The two dots (..) are used to specify that it's an inclusive range, meaning it includes both the start and the end values.
        for (i in 1..count) {
            val randomIndex = MathUtils.random(spawnLocations.size - 1)
            val baseSpawnPoint = spawnLocations[randomIndex]
            // Add a small random offset to each spawn incase enemies spawn at same place at same time and don't overlap 1:1
            val offset = Vector2(MathUtils.random(-20f, 20f), MathUtils.random(-20f, 20f))
            val spawnPoint = baseSpawnPoint.add(offset)
            val enemy = createEnemy(spawnPoint)
            list.add(enemy)
        }
    }

}



