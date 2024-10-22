package org.phinxt.assignment.service

import org.phinxt.assignment.util.Point

interface NavigationServiceI {

    fun navigate(roomSize: Point, coords: Point, patches: List<Point>?, instructions: String?, validate: Boolean): HooverNavigationResult

}