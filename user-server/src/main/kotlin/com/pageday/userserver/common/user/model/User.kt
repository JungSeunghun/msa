package com.pageday.userserver.common.user.model

import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("TC_USER")
data class User(
    val userId: Long?,
    val username: String,
    val password: String,
    val phoneNumber: String,
    val email: String,
    val nickname: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
