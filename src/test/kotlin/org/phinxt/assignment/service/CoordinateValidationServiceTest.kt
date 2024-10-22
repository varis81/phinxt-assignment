package org.phinxt.assignment.service

import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
class CoordinateValidationServiceTest {

    lateinit var coordinateValidationService: CoordinateValidationService

    @BeforeEach
    fun setUp() {
        coordinateValidationService = CoordinateValidationService()
    }

    @Test
    fun `validation should fail if hoover is not inside the room`() {
        // Given
        val roomSize = listOf(5,5)
        val coords = listOf(6,2)
        val patches = listOf(listOf(1,2), listOf(2,3))
        val instructions = "NEWSSEWS"

        // When
        val result = coordinateValidationService.validateCoordinates(roomSize, coords, patches, instructions)

        // Then
        assertEquals(false, result.valid)
        assertEquals("Hoover is not on a valid starting point", result.message)
    }

    @Test
    fun `validation should fail if a patch of dust is not inside the room`() {
        // Given
        val roomSize = listOf(5,5)
        val coords = listOf(1,2)
        val patches = listOf(listOf(12,2), listOf(2,3))
        val instructions = "NEWSSEWS"

        // When
        val result = coordinateValidationService.validateCoordinates(roomSize, coords, patches, instructions)

        // Then
        assertEquals(false, result.valid)
        assertEquals("Patch [12, 2] is not a valid point inside the room", result.message)
    }

    @Test
    fun `validation should fail if input is not semantically correct - roomSize is null`() {
        // Given
        val roomSize = null
        val coords = listOf(1,2)
        val patches = listOf(listOf(12,2), listOf(2,3))
        val instructions = "NEWSSEWS"

        // When
        val result = coordinateValidationService.validateCoordinates(roomSize, coords, patches, instructions)

        // Then
        assertEquals(false, result.valid)
        assertEquals("Parameters passed are not valid (non-null, positive coordinate points)", result.message)
    }

    @Test
    fun `validation should fail if input is not semantically correct - a coordinate is negative`() {
        // Given
        val roomSize = listOf(5,5)
        val coords = listOf(-1,2)
        val patches = listOf(listOf(12,2), listOf(2,3))
        val instructions = "NEWSSEWS"

        // When
        val result = coordinateValidationService.validateCoordinates(roomSize, coords, patches, instructions)

        // Then
        assertEquals(false, result.valid)
        assertEquals("Parameters passed are not valid (non-null, positive coordinate points)", result.message)
    }

    @Test
    fun `validation should fail if input is not semantically correct - a patch is negative`() {
        // Given
        val roomSize = listOf(5,5)
        val coords = listOf(1,2)
        val patches = listOf(listOf(2,2), listOf(-2,3))
        val instructions = "NEWSSEWS"

        // When
        val result = coordinateValidationService.validateCoordinates(roomSize, coords, patches, instructions)

        // Then
        assertEquals(false, result.valid)
        assertEquals("Parameters passed are not valid (non-null, positive coordinate points)", result.message)
    }

    @Test
    fun `validation should pass`() {
        // Given
        val roomSize = listOf(5,5)
        val coords = listOf(1,2)
        val patches = listOf(listOf(2,2), listOf(2,3))
        val instructions = "NEWSSEWS"

        // When
        val result = coordinateValidationService.validateCoordinates(roomSize, coords, patches, instructions)

        // Then
        assertEquals(true, result.valid)
    }

    @Test
    fun `validation should pass even if there are no patches`() {
        // Given
        val roomSize = listOf(5,5)
        val coords = listOf(1,2)
        val patches = emptyList<List<Int>>()
        val instructions = "NEWSSEWS"

        // When
        val result = coordinateValidationService.validateCoordinates(roomSize, coords, patches, instructions)

        // Then
        assertEquals(true, result.valid)
    }

    @Test
    fun `validation should fail if instructions contain non cardinal directions chars`() {
        // Given
        val roomSize = listOf(5,5)
        val coords = listOf(1,2)
        val patches = emptyList<List<Int>>()
        val instructions = "NEWSSEWSU"

        // When
        val result = coordinateValidationService.validateCoordinates(roomSize, coords, patches, instructions)

        // Then
        assertEquals(false, result.valid)
        assertEquals("Instructions passed are invalid - non cardinal directions characters", result.message)
    }
}