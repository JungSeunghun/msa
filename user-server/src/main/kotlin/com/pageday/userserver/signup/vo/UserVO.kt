package com.pageday.userserver.signup.vo

import java.time.LocalDateTime

data class UserVO(
    val userId: Long?,
    val username: String,
    val nickname: String,
    val email: String,
    val phoneNumber: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
