package com.pageday.userserver.signup.repository

import com.pageday.userserver.signup.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface UserRepository : ReactiveCrudRepository<User, Long> {
    suspend fun findByUsername(username: String): User?
    suspend fun findByNickname(nickname: String): User?
    suspend fun findByEmail(email: String): User?
}