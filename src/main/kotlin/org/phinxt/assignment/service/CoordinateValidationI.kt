package org.phinxt.assignment.service

import org.phinxt.assignment.util.Point

interface CoordinateValidationI {

    fun validateCoordinates(roomSize: Point?, coords: Point?, patches: List<Point>?, instructions: String?): ValidationResult

    fun isPointInsideRectangle(roomSize: Point, point: Point): Boolean
}