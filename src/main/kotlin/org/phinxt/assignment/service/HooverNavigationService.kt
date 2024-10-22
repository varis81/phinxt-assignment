package org.phinxt.assignment.service

import org.phinxt.assignment.api.HooverNavigationApiResponseDto
import org.phinxt.assignment.util.Point
import org.phinxt.assignment.util.logger
import org.springframework.stereotype.Service

@Service
class HooverNavigationService (
    private val validationService: CoordinateValidationService
) :  NavigationServiceI {

    override fun navigate(roomSize: Point, coords: Point, patches: List<Point>?, instructions: String?, validate: Boolean): HooverNavigationResult {
        // We add a validate parameter here, so that this service can be used as a standalone service.
        // In our case, validate shall be null since we already validated the input
        if (validate) {
            val validationResult = validationService.validateCoordinates(roomSize, coords, patches, instructions)
            if (!validationResult.valid) {
                // this is not an error - invalid input should be treated as business as usual in this case
                log.warn("Hoover can't be navigated due to invalid input parameters: ${validationResult.message}")
                throw NavigationServiceInvalidInputException("Navigation Service input coordinates are not valid: ${validationResult.message}")
            }
        }

        // We convert the patches list to a map with the key being the coordinates of each patch - this is for faster patch lookup
        // We need to constantly check if there is a patch at our current position
        val patchMap = convertPatchesToMap(patches)
        var currentHooverPosition = coords
        var patchesCleaned = 0

        // Patch is incremented in case there is a patch on the position of the hoover - since the hoover is always on.
        if (isThereAPatchHere(currentHooverPosition, patchMap)) {
            patchesCleaned++
        }

        // If there are no instructions, the hoover stays in place.
        if (instructions.isNullOrEmpty()) {
            return HooverNavigationResult(coords = listOf(currentHooverPosition.x, currentHooverPosition.y), patchesCleaned)
        }

        instructions.uppercase().forEach { direction ->
            currentHooverPosition = moveHoover(roomSize, currentHooverPosition, direction)
            if (isThereAPatchHere(currentHooverPosition, patchMap)) {
                patchesCleaned++
            }
        }

        return HooverNavigationResult(coords = listOf(currentHooverPosition.x, currentHooverPosition.y), patchesCleaned)
    }

    private fun moveHoover(roomSize: Point, currentHooverPosition: Point, direction: Char) =
        when (direction) {
            'N' ->  checkIfPointIsValid(roomSize, Point(x = currentHooverPosition.x, y = currentHooverPosition.y + 1), currentHooverPosition)
            'E' ->  checkIfPointIsValid(roomSize, Point(x = currentHooverPosition.x + 1, y = currentHooverPosition.y), currentHooverPosition)
            'W' ->  checkIfPointIsValid(roomSize, Point(x = currentHooverPosition.x - 1, y = currentHooverPosition.y), currentHooverPosition)
            'S' ->  checkIfPointIsValid(roomSize, Point(x = currentHooverPosition.x, y = currentHooverPosition.y - 1), currentHooverPosition)
            else -> {
                log.error("Invalid instruction character supplied: $direction")
                throw InvalidInstrivtionException("Instruction is not valid: ${direction}")
            }
        }

    private fun checkIfPointIsValid(roomSize: Point, newPoint: Point, currentHooverPosition: Point) =
        if (validationService.isPointInsideRectangle(
                roomSize = roomSize,
                point = newPoint
        ))
            newPoint
        else
            currentHooverPosition

    private fun convertPatchesToMap(patches: List<Point>?): MutableMap<String, Point> {
        val map = mutableMapOf<String, Point>()
        patches?.forEach { patch ->
            map.put(getPointSignature(patch), patch)
        }
        return map
    }

    private fun getPointSignature(point: Point) =
        "X${point.x}Y${point.y}"

    private fun isThereAPatchHere(point: Point, patches: MutableMap<String, Point>): Boolean {
        if (getPointSignature(point) in patches) {
            // if there is a hit, we have to remove the entry from the map, otherwise we might clean something more than once
            patches.remove(getPointSignature(point))
            return true
        }
        return false
    }

    companion object {
        val log = logger()
    }
}

class NavigationServiceInvalidInputException(error: String) : RuntimeException(error)

class InvalidInstrivtionException(error: String) : RuntimeException(error)

data class HooverNavigationResult(val coords: List<Int>, val patches: Int = 0)

fun HooverNavigationResult.convertToDto(): HooverNavigationApiResponseDto {
    return HooverNavigationApiResponseDto(this.coords, this.patches)
}