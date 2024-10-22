package org.phinxt.assignment.util

import jakarta.validation.Constraint
import org.phinxt.assignment.api.NestedArrayValidator
import org.phinxt.assignment.api.TwoElementArrayValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * Creates a logger for the containing class.
 * Usage:
 *      companion object {
 *          private val log = logger()
 *      }
 */
fun Any.logger(): Logger {
    val clazz = if (this::class.isCompanion) this::class.java.enclosingClass else this::class.java
    return LoggerFactory.getLogger(clazz)
}


@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NestedArrayValidator::class])
annotation class ValidTwoElementNestedArray(
    val message: String = "Patch array must contain exactly two non-negative elements",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = []
)

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [TwoElementArrayValidator::class])
annotation class TwoElementArray(
    val message: String = "Array must be non null and contain exactly two non-negative elements",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = []
)

fun List<Int>.toPoint(): Point {
    return Point(this[0], this[1])
}

fun List<List<Int>>.toPoints(): List<Point> {
    var result = mutableListOf<Point>()
    this.forEach {
        result.add(Point(it[0], it[1]))
    }
    return result
}

data class Point(val x: Int, val y: Int)