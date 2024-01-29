package com.lucian.garliclike

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.lucian.garliclike.enemies.Enemy
import com.lucian.garliclike.enemies.SkeletonKnife
import com.lucian.garliclike.items.Weapon

class Debug(private val camera: OrthographicCamera, private val player: Player, private val enemies: List<Enemy>, private val collisionObjects: List<RectangleMapObject>) {
    private val shapeRenderer = ShapeRenderer()

    // lets me activate all functions with a simple .render so less code in Level1Scren
    fun render() {
        drawAttackRange()
        drawHitBoxes()
        drawEnemyAttackRanges()
        drawCollisionObjectOutlines()
    }

    private fun drawAttackRange() {
        // Blending is primarily used for rendering transparent and semi-transparent objects with libGDX and OpenGL.
        // When we enable blending, we allow OpenGL to take into account the alpha value of the pixels
        // which represents their opacity) and blend these pixels with the background pixels accordingly
        Gdx.gl.glEnable(GL20.GL_BLEND)
        // projectionMatrix determines how the shapes are projected onto the screen
        // Setting the shapeRenderer's projection matrix to the camera's combined matrix
        // ensures that the shapes are rendered correctly according to the camera's perspective.
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.CYAN

        // Go through inventory, filter out only ones marked as instances of Weapon, draw each weapon's range
        player.getInventory().filterIsInstance<Weapon>().forEach { weapon ->
            shapeRenderer.circle(player.position.x, player.position.y, weapon.range)
        }

        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun drawHitBoxes() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        // Draw player hitbox
        shapeRenderer.color = Color.GREEN
        shapeRenderer.rect(player.hitbox.x, player.hitbox.y, player.hitbox.width, player.hitbox.height)

        // Draw enemy hitbox
        shapeRenderer.color = Color.RED
        enemies.forEach { enemy ->
            shapeRenderer.rect(enemy.hitbox.x, enemy.hitbox.y, enemy.hitbox.width, enemy.hitbox.height)
        }
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)

        drawAttackRange()
    }

    private fun drawCollisionObjectOutlines() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.ORANGE // Use a color that stands out

        collisionObjects.forEach { rectObj ->
            val rect = rectObj.rectangle
            shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height)
        }

        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun drawEnemyAttackRanges() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.YELLOW // Choose a different color for visibility

        enemies.forEach { enemy ->
            if (enemy is SkeletonKnife) {
                shapeRenderer.circle(enemy.position.x, enemy.position.y, enemy.attackRange)
            }
        }

        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }
}
