package org.phinxt.assignment.api

import org.phinxt.assignment.util.TwoElementArray
import org.phinxt.assignment.util.ValidTwoElementNestedArray

data class HooverNavigationApiRequestDto (
    @field:TwoElementArray
    val roomSize: List<Int>?,

    @field:TwoElementArray
    val coords: List<Int>?,

    @field:ValidTwoElementNestedArray
    val patches: List<List<Int>>?,

    val instructions: String?
)

