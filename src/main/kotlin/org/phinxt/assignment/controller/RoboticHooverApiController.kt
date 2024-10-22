package org.phinxt.assignment.controller

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.phinxt.assignment.api.HooverNavigationApiRequestDto
import org.phinxt.assignment.service.CoordinateValidationService
import org.phinxt.assignment.service.HooverNavigationService
import org.phinxt.assignment.service.convertToDto
import org.phinxt.assignment.util.logger
import org.phinxt.assignment.util.toPoint
import org.phinxt.assignment.util.toPoints
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RoboticHooverApiController(
    private val coordinateValidationService: CoordinateValidationService,
    private val hooverNavigationService: HooverNavigationService
) {

    @PostMapping("/api/v1/robotic-hoover/navigate")
    @Operation(
        description = "Call this POST request to calculate the final position of a robotic hoover, based on the input"
    )
    fun navigate(
        @Valid @RequestBody request: HooverNavigationApiRequestDto
    ): ResponseEntity<*> {
        val roomSize = requireNotNull(request.roomSize)
        val coords = requireNotNull(request.coords)
        val validationResult = coordinateValidationService.validateCoordinates(
            roomSize = roomSize.toPoint(),
            coords = coords.toPoint(),
            patches = request.patches?.toPoints(),
            instructions = request.instructions
        )
        log.info("Input validation result: $validationResult")

        if (!validationResult.valid)
            return ResponseEntity.badRequest()
                .body("Some or all points supplied are not inside the room: ${validationResult.message}")

        val hooverNavigationResult = hooverNavigationService.navigate(
            roomSize = roomSize.toPoint(),
            coords = coords.toPoint(),
            patches = request.patches?.toPoints(),
            instructions = request.instructions,
            validate = false // we already validated above
        )
        log.info("Navigation finished successfully: $hooverNavigationResult")

        return ResponseEntity.ok().body(hooverNavigationResult.convertToDto())
    }

    companion object {
        val log = logger()
    }
}