package com.lucian.garliclike

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap

// This function is responsible for handling rectangle objects within a layer that might be used for collision.
//  objectNamePrefix is used to identify objects based on a common prefix in their names
//  example, we got objects building01, building02 if we use a prefix and set it to "building" it will grab anything that has building in that object layer
class RectangleObjectRenderer(private val tiledMap: TiledMap, private val player: Player) {

    fun renderRectangleObjects(layerName: String, objectNamePrefix: String) {
        // As before, we get the layer from the map.
        val objectLayer = tiledMap.layers.get(layerName) as MapLayer
        // This list will hold the rectangle objects we want to handle for collisions.
        val rectanglesToRender = mutableListOf<RectangleMapObject>()

        // We look through all objects in the layer, but only retrieve those of type RectangleMapObject.
        // This type of object typically represents areas on the map for collisions, triggers, etc.
        objectLayer.objects.getByType(RectangleMapObject::class.java).forEach { obj ->
            // If the name of the object starts with the specified prefix, we add it to our list.
            if (obj.name.startsWith(objectNamePrefix)) {
                rectanglesToRender.add(obj)
            }
        }

        // Now, we handle collision detection with the player.
        rectanglesToRender.forEach { rectObj ->
            // We get the bounding rectangle of the object, which defines its position and size on the map.
            val wallRect = rectObj.rectangle
            // We then check if the player's hitbox intersects with this rectangle.
            if (player.hitbox.overlaps(wallRect)) {
                // If they do overlap, it means the player has collided with this object.
                player.resolveCollision()
            }
        }
    }
}

