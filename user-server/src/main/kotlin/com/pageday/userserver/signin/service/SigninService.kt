package com.pageday.userserver.signin.service

import com.pageday.userserver.common.user.model.User
import com.pageday.userserver.common.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class SigninService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    suspend fun signin(username: String, password: String): User? {
        val user = userRepository.findByUsername(username)
        if(user == null || !passwordEncoder.matches(password, user.password)) {
            return null
        } else {
            return user
        }
    }
}