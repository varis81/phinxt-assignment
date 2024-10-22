package org.phinxt.assignment.service

interface NavigationServiceI {

    fun navigate(roomSize: List<Int>, coords: List<Int>, patches: List<List<Int>>?, instructions: String?, validate: Boolean): HooverNavigationResult

}