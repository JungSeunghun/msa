package com.pageday.userserver.common.validation

import java.util.regex.Pattern

object RegexPatterns {
    val name: Pattern = Pattern.compile("^[가-힣a-zA-Z\\s]{2,30}$")
    val username: Pattern = Pattern.compile("^(?=.*[a-z])[a-z0-9]{4,16}$")
    val email: Pattern = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    val nickname: Pattern = Pattern.compile("^(?=.*[가-힣a-zA-Z])[가-힣a-zA-Z0-9]{2,10}$")
    val password: Pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#\$%^&*])[A-Za-z\\d!@#\$%^&*]{8,16}$")
    val year: Pattern = Pattern.compile("^(19\\d{2}|20\\d{2})$")
    val month: Pattern = Pattern.compile("^(1[0-2]|[1-9])$")
    val day: Pattern = Pattern.compile("^(3[01]|[12][0-9]|[1-9])$")
    val phone1: Pattern = Pattern.compile("^\\d{2,3}$")
    val phone2: Pattern = Pattern.compile("^\\d{3,4}$")
    val phone3: Pattern = Pattern.compile("^\\d{4}$")
}