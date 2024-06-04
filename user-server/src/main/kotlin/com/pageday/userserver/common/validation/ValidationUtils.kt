package com.pageday.userserver.common.validation

import com.pageday.userserver.common.exception.InvalidFieldException
import java.util.regex.Pattern

fun validatePattern(pattern: Pattern, value: String, fieldName: String) {
    if (!pattern.matcher(value).matches()) {
        throw InvalidFieldException("$fieldName is invalid")
    }
}
