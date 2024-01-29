package com.lucian.garliclike

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
//import com.lucian.garliclike.items.Bible
import com.lucian.garliclike.items.Fireball
import com.lucian.garliclike.items.Cross
import com.lucian.garliclike.items.Sword

class DrawHud (private val player : Player , private val spriteBatch : SpriteBatch ){

    private lateinit var heartTexture: Texture
    private lateinit var heartHalfTexture: Texture
    private lateinit var fireballIconTexture: Texture
    private lateinit var crossIconTexture: Texture
    private lateinit var swordTexture: Texture
    private lateinit var bibleTexture: Texture
    init {
        heartTexture = Texture("assets/hud/heart.png")
        heartHalfTexture = Texture("assets/hud/heartHalf.png")
        fireballIconTexture = Texture("assets/items/weapons/Fireball.png")
        crossIconTexture = Texture("assets/items/weapons/Cross.png")
        swordTexture = Texture("assets/items/weapons/Sword.png")
        bibleTexture = Texture("assets/items/weapons/Bible.png")

    }

    internal fun drawPlayerHealth() {

        val health = player.hp
        val heartWidth = 32f
        val heartHeight = 32f
        val startX = 10f
        val startY = 10f

        //loop iterates to draw full heart icons. The number of iterations is determined by the player's health divided by 10.
        // For example, if health is 100, the loop runs 10 times, drawing 10 full hearts.
        for (i in 0 until health / 10) {
            // startX + i * heartWidth, places each heart next to the previous one.
            // the first heart is at 10f, the second at 42f, and the third at 74f etc,
            // each separated by 32 pixels, which is the width of each heart icon.
            spriteBatch.draw(heartTexture, startX + i * heartWidth, startY, heartWidth, heartHeight)
        }
        // checks whether the player's health leaves a remainder of 5 or more when divided by 10.
        if (health % 10 >= 5) {
            //health/10: This calculates how many full hearts are to be displayed (example 35 would be 3)
            // health/10: * heartWidth: This calculates the X position offset for the half heart based on the number of full hearts.
            spriteBatch.draw(heartHalfTexture, startX + (health / 10) * heartWidth, startY, heartWidth, heartHeight)
        }

    }
    internal fun drawPlayerInventory() {

        val inventory = player.getInventory()
        val iconWidth = 32f // Size of each icon
        val iconHeight = 32f // Size of each icon
        val startX = 10f // Start position of the first icon
        val startY = Gdx.graphics.height - 50f // Position from the top of the screen

        /**
        / Maybe update this so it just each weapon to draw, so we don't have to be specifically tell it to check for specific weapons */
        //Iterates over each item in the player's inventory. index is the position of the item in the inventory, and item is the actual item.
        inventory.forEachIndexed { index, item ->
            when (item) {
                //case checks if the item is an instance of the Fireball
                // same calculations as for heart
                is Fireball -> spriteBatch.draw(fireballIconTexture, startX + index * iconWidth, startY, iconWidth, iconHeight)
                is Cross -> spriteBatch.draw(crossIconTexture, startX + index * iconWidth, startY, iconWidth, iconHeight)
                is Sword -> spriteBatch.draw(swordTexture, startX + index * iconWidth, startY, iconWidth, iconHeight)
                //is Bible -> spriteBatch.draw(bibleTexture, startX + index * iconWidth, startY, iconWidth, iconHeight)
                // Add cases for other items in the future
            }
        }
    }
}
