package org.phinxt.assignment.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.phinxt.assignment.api.HooverNavigationApiRequestDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class HooverNavigationServiceIT {

    @Autowired
    private lateinit var hooverNavigationService: HooverNavigationService

    @Test
    fun `patch passed over more than once, should be reported only once`() {
        // When
        val result = hooverNavigationService.navigate(
            roomSize = listOf(5,5),
            coords = listOf(1,2),
            patches = listOf(listOf(1,0), listOf(2,2), listOf(2,3)),
            instructions = "NNESEESWNWW",
            validate = true
        )

        // Then
        assertEquals(result.coords, listOf(1,3))
        assertEquals(result.patches, 1)
    }

    @Test
    fun `if no instructions and there is a patch on the starting point, it should be cleaned`() {
        // When
        val result = hooverNavigationService.navigate(
            roomSize = listOf(5,5),
            coords = listOf(1,2),
            patches = listOf(listOf(1,2), listOf(2,2), listOf(2,3)),
            instructions = null,
            validate = true
        )

        // Then
        assertEquals(result.coords, listOf(1,2))
        assertEquals(result.patches, 1)
    }

    @Test
    fun `if a wall is hit, the hoover should stay at position an not move`() {
        // When
        val result = hooverNavigationService.navigate(
            roomSize = listOf(5,5),
            coords = listOf(1,2),
            patches = listOf(listOf(1,2), listOf(0,2), listOf(2,3)),
            instructions = "WWWSS",
            validate = true
        )

        // Then
        assertEquals(result.coords, listOf(0,0))
        assertEquals(result.patches, 2)
    }

    @Test
    fun `patch passed more than once on the list, should be reported only once as cleaned`() {
        // When
        val result = hooverNavigationService.navigate(
            roomSize = listOf(5,5),
            coords = listOf(1,2),
            patches = listOf(listOf(2,3), listOf(2,3), listOf(2,3)),
            instructions = "NNESEESWNWW",
            validate = true
        )

        // Then
        assertEquals(result.coords, listOf(1,3))
        assertEquals(result.patches, 1)
    }

    @Test
    fun `lowercase instructions also work`() {
        // When
        val result = hooverNavigationService.navigate(
            roomSize = listOf(2,2),
            coords = listOf(0,0),
            patches = listOf(listOf(0,2)),
            instructions = "nNee",
            validate = true
        )

        // Then
        assertEquals(result.coords, listOf(2,2))
        assertEquals(result.patches, 1)
    }
}