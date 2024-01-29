package com.lucian.garliclike.items

/**
 * Need modularity, base items like HP potions will be based upon item interface
 * Weapon will have their own abstract that implement Item interface
 */

interface Item {
    val name: String
    val description: String

    //fun use()
}
