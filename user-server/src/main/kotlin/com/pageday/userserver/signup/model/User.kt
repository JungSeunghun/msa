package com.pageday.userserver.signup.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("TC_USER")
data class User(
    @Id val userId: Long? = null,
    val username: String,
    val password: String,
    val phoneNumber: String,
    val email: String,
    val nickname: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
