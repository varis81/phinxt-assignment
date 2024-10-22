package org.phinxt.assignment.api

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.phinxt.assignment.util.TwoElementArray


class TwoElementArrayValidator : ConstraintValidator<TwoElementArray, List<Int>?> {
    override fun isValid(value: List<Int>?, context: ConstraintValidatorContext?): Boolean {
        if (value == null || value.isEmpty() || value.size != 2 || value.get(0) < 0 || value.get(1) < 0 ) {
            return false
        }
        return true
    }
}