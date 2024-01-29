package com.lucian.garliclike

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject

// This function is responsible for rendering the tile objects from a specific layer of the tiled map.
// It takes two parameters: the name of the object layer and the name of objects to render inside the layer.
class TileObjectRenderer(private val tiledMap: TiledMap, private val spriteBatch: SpriteBatch) {

    fun renderTileObjects(layerName: String, objectName: String) {
        // We retrieve the specified MapLayer from the tiled map using its name.
        // This is cast to MapLayer because the get() method returns a generic MapLayer which can hold various types of objects.
        val objectLayer = tiledMap.layers.get(layerName) as MapLayer
        // We create an empty list to hold the objects that we will render.
        val objectsToRender = mutableListOf<MapObject>()

        // We go through each object in the object layer.
        objectLayer.objects.forEach { obj ->
            // We check if the object is of the type TiledMapTileMapObject, which means it's a tile object,
            // and if its name matches the object name we want to render.
            if (obj is TiledMapTileMapObject && obj.name == objectName) {
                // If both conditions are true, we add the object to our list of objects to render.
                objectsToRender.add(obj)
            }
        }
        // Now we go through each object we need to render.
        objectsToRender.forEach { obj ->
            // We again check if the object is a TiledMapTileMapObject to avoid casting errors.
            if (obj is TiledMapTileMapObject) {
                // We retrieve the tile object and its associated texture region.
                // The texture region contains the pixel data for the tile image.
                val textureRegion = obj.tile.textureRegion
                // We use the sprite batch to draw the texture region at the position of the tile object.
                // We also apply the object's origin, scale, and rotation properties.
                spriteBatch.draw(
                    textureRegion, obj.x, obj.y, obj.originX, obj.originY,
                    obj.textureRegion.regionWidth.toFloat(), obj.textureRegion.regionHeight.toFloat(),
                    obj.scaleX, obj.scaleY, obj.rotation
                )
            }
        }
    }
}

