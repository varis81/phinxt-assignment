package org.phinxt.assignment.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.phinxt.assignment.api.HooverNavigationApiRequestDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class RoboticHooverApiControllerIT {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `validation should fail if roomsize is null`() {
        // When I make a POST request to the endpoint
        val result = mockMvc.post("/api/v1/robotic-hoover/navigate") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "coords": [1,2],
              "patches": [
                [1,0],
                [2,2],
                [2,3]
              ],
              "instructions": "NNESEESWNWW"
            }""".trimIndent()
        }

        // Then the response should be BAD REQUEST (400)
        result.andExpect {
            status { isBadRequest() }
            content { string("Array must be non null and contain exactly two non-negative elements") }
        }
    }

    @Test
    fun `validation should fail if roomsize does not contain two points`() {
        // Given a non valid input
        val input = createValidInput().copy(roomSize = listOf(1,2,3))

        // When I make a POST request to the endpoint
        val result = mockMvc.post("/api/v1/robotic-hoover/navigate") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(input)
        }

        // Then the response should be BAD REQUEST (400)
        result.andExpect {
            status { isBadRequest() }
            content { string("Array must be non null and contain exactly two non-negative elements") }
        }
    }

    @Test
    fun `validation should fail if roomsize has negative coordinate points`() {
        // When I make a POST request to the endpoint
        val result = mockMvc.post("/api/v1/robotic-hoover/navigate") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "roomSize": [1,-2],
              "coords": [1,2],
              "patches": [
                [1,0],
                [2,2],
                [2,3]
              ],
              "instructions": "NNESEESWNWW"
            }""".trimIndent()
        }

        // Then the response should be BAD REQUEST (400)
        result.andExpect {
            status { isBadRequest() }
            content { string("Array must be non null and contain exactly two non-negative elements") }
        }
    }

    @Test
    fun `validation should fail if coords is null`() {
        // When I make a POST request to the endpoint
        val result = mockMvc.post("/api/v1/robotic-hoover/navigate") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "roomSize": [1,2],
              "patches": [
                [1,0],
                [2,2],
                [2,3]
              ],
              "instructions": "NNESEESWNWW"
            }""".trimIndent()
        }

        // Then the response should be BAD REQUEST (400)
        result.andExpect {
            status { isBadRequest() }
            content { string("Array must be non null and contain exactly two non-negative elements") }
        }
    }

    @Test
    fun `validation should fail if coords does not contain two points`() {
        // Given a non valid input
        val input = createValidInput().copy(coords = listOf(1))

        // When I make a POST request to the endpoint
        val result = mockMvc.post("/api/v1/robotic-hoover/navigate") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(input)
        }

        // Then the response should be BAD REQUEST (400)
        result.andExpect {
            status { isBadRequest() }
            content { string("Array must be non null and contain exactly two non-negative elements") }
        }
    }

    @Test
    fun `validation should fail if patches are not a set of coordinates`() {
        // Given
        val input = createValidInput().copy(patches = listOf(listOf(1,2,3)))

        // When I make a POST request to the endpoint
        val result = mockMvc.post("/api/v1/robotic-hoover/navigate") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(input)
        }

        // Then the response should be BAD REQUEST (400)
        result.andExpect {
            status { isBadRequest() }
            content { string("Patch array must contain exactly two non-negative elements") }
        }
    }

    @Test
    fun `validation should fail if patches are not a set of non negative coordinates`() {
        // Given
        val input = createValidInput().copy(patches = listOf(listOf(1,-2)))

        // When I make a POST request to the endpoint
        val result = mockMvc.post("/api/v1/robotic-hoover/navigate") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(input)
        }

        // Then the response should be BAD REQUEST (400)
        result.andExpect {
            status { isBadRequest() }
            content { string("Patch array must contain exactly two non-negative elements") }
        }
    }

    @Test
    fun `validation should fail if hoover is not inside the room`() {
        // Given
        val input = createValidInput().copy(coords = listOf(7,7))

        // When I make a POST request to the endpoint
        val result = mockMvc.post("/api/v1/robotic-hoover/navigate") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(input)
        }

        // Then the response should be BAD REQUEST (400)
        result.andExpect {
            status { isBadRequest() }
            content { string("Some or all points supplied are not inside the room: Hoover is not on a valid starting point") }
        }
    }

    @Test
    fun `validation should fail if a patch is not inside the room`() {
        // Given
        val input = createValidInput().copy(patches = listOf(listOf(1,0), listOf(2,7), listOf(2,3)),)

        // When I make a POST request to the endpoint
        val result = mockMvc.post("/api/v1/robotic-hoover/navigate") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(input)
        }

        // Then the response should be BAD REQUEST (400)
        result.andExpect {
            status { isBadRequest() }
            content { string("Some or all points supplied are not inside the room: Patch [2, 7] is not a valid point inside the room") }
        }
    }

    @Test
    fun `happy path execution`() {
        // When I make a POST request to the endpoint
        val result = mockMvc.post("/api/v1/robotic-hoover/navigate") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "roomSize": [5,5],
              "coords": [1,2],
              "patches": [
                [1,0],
                [2,2],
                [2,3]
              ],
              "instructions": "NNESEESWNWW"
            }""".trimIndent()
        }

        // Then the response should be Ok (200)
        result.andExpect {
            status { isOk() }
            content { string("{\"coords\":[1,3],\"patches\":1}") }
        }
    }

    @Test
    fun `happy path execution - lower case instructions`() {
        // When I make a POST request to the endpoint
        val result = mockMvc.post("/api/v1/robotic-hoover/navigate") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "roomSize": [5,5],
              "coords": [1,2],
              "patches": [
                [1,0],
                [2,2],
                [2,3]
              ],
              "instructions": "nnESEESWNww"
            }""".trimIndent()
        }

        // Then the response should be Ok (200)
        result.andExpect {
            status { isOk() }
            content { string("{\"coords\":[1,3],\"patches\":1}") }
        }
    }

    @Test
    fun `when no instructions are passed, and a patch is at the starting point, it should be cleaned`() {
        // When I make a POST request to the endpoint
        val result = mockMvc.post("/api/v1/robotic-hoover/navigate") {
            contentType = MediaType.APPLICATION_JSON
            content = """
            {
              "roomSize": [5,5],
              "coords": [2,3],
              "patches": [
                [1,3],
                [2,2],
                [2,3]
              ]
            }""".trimIndent()
        }

        // Then the response should be Ok (200)
        result.andExpect {
            status { isOk() }
            content { string("{\"coords\":[2,3],\"patches\":1}") }
        }
    }

    private fun createValidInput() =
        HooverNavigationApiRequestDto(
            roomSize = listOf(5,5),
            coords = listOf(1,2),
            patches = listOf(listOf(1,0), listOf(2,2), listOf(2,3)),
            instructions = "NNESEESWNWW"
        )
}