package com.pageday.userserver.common.user.repository

import com.pageday.userserver.common.user.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface UserRepository : ReactiveCrudRepository<User, Long> {
    suspend fun findByUsername(username: String): User?
    suspend fun findByNickname(nickname: String): User?
    suspend fun findByEmail(email: String): User?
}