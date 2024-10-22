package org.phinxt.assignment.api

data class HooverNavigationApiResponseDto (
    val coords: List<Int>,

    val patches: Int = 0
)
