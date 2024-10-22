package org.phinxt.assignment.service

import org.springframework.stereotype.Service

@Service
class CoordinateValidationService : CoordinateValidationI {

    // Here we should assume valid values for all parameters.
    // However, we revalidate them so that we can have a standalone service that can be used in other places as well
    override fun validateCoordinates(roomSize: List<Int>?, coords: List<Int>?, patches: List<List<Int>>?, instructions: String?): ValidationResult {
        if (!areParametersValid(
                roomSize = roomSize,
                coords =  coords,
                patches = patches
        )) {
            return ValidationResult(valid = false, message = "Parameters passed are not valid (non-null, positive coordinate points)")
        }

        if (!areInstructionsValid(
                instructions = instructions
            )) {
            return ValidationResult(valid = false, message = "Instructions passed are invalid - non cardinal directions characters")
        }


        if (!isPointInsideRectangle(
            roomSize = roomSize!!,
            point = coords!!
        )) {
            return ValidationResult(valid = false, message = "Hoover is not on a valid starting point")
        }

        patches?.forEach { patch ->
            if (!isPointInsideRectangle(
                    roomSize = roomSize,
                    point = patch
                )) {
                return ValidationResult(valid = false, message = "Patch $patch is not a valid point inside the room")
            }
        }

        return ValidationResult(valid = true)
    }

    override fun isPointInsideRectangle(roomSize: List<Int>, point: List<Int>): Boolean {
        // define the variables for clarity
        val roomX = roomSize.get(0)
        val roomY = roomSize.get(1)

        val pointX = point.get(0)
        val pointY = point.get(1)

        // we assume that the points (0,0) and (roomX,roomY) are valid positions for the hoover
        if (pointX >= 0 && pointX <= roomX && pointY >= 0 && pointY <= roomY)
            return true
        return false
    }

    private fun areParametersValid(roomSize: List<Int>?, coords: List<Int>?, patches: List<List<Int>>?): Boolean {
        if (!validate(roomSize)) return false
        if (!validate(coords)) return false
        patches?.forEach { patch ->
            if (!validate(patch)) return false
        }
        return true
    }

    private fun validate(value: List<Int>?): Boolean {
        if (value == null || value.size != 2 || value.get(0) < 0 || value.get(1) < 0)
            return false
        return true
    }

    private fun areInstructionsValid(instructions: String?): Boolean {
        val allowedChars = "NEWS"
        return instructions?.uppercase()?.all { it in allowedChars } ?: true
    }
}

data class ValidationResult(val valid: Boolean, val message: String? = null)