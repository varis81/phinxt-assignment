package org.phinxt.assignment.api

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.phinxt.assignment.util.ValidTwoElementNestedArray


class NestedArrayValidator : ConstraintValidator<ValidTwoElementNestedArray, List<List<Int>>?> {
    override fun isValid(value: List<List<Int>>?, context: ConstraintValidatorContext?): Boolean {
        if (value == null || value.isEmpty() || value.size == 0 )
            return true
        for (patch in value) {
            if (patch.size != 2 || patch.get(0) < 0 || patch.get(1) < 0)
                return false
        }
        return true
    }
}