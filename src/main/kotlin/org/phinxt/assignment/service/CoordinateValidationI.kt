package org.phinxt.assignment.service

interface CoordinateValidationI {

    fun validateCoordinates(roomSize: List<Int>?, coords: List<Int>?, patches: List<List<Int>>?, instructions: String?): ValidationResult

    fun isPointInsideRectangle(roomSize: List<Int>, point: List<Int>): Boolean
}