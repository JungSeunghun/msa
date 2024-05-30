package com.pageday.userserver.signup.service

import com.pageday.userserver.signup.dto.SignupRequest
import com.pageday.userserver.signup.exception.*
import com.pageday.userserver.signup.model.User
import com.pageday.userserver.signup.repository.UserRepository
import com.pageday.userserver.signup.snowflake.SnowflakeIdGenerator
import com.pageday.userserver.signup.vo.UserVO
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val snowflakeIdGenerator: SnowflakeIdGenerator
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    suspend fun isUsernameAvailable(username: String): Boolean {
        return userRepository.findByUsername(username) == null
    }

    suspend fun isNicknameAvailable(nickname: String): Boolean {
        return userRepository.findByNickname(nickname) == null
    }

    suspend fun isEmailAvailable(email: String): Boolean {
        return userRepository.findByEmail(email) == null
    }

    suspend fun registerUser(signupRequest: SignupRequest): UserVO {
        if (signupRequest.password != signupRequest.confirmPassword) {
            throw PasswordMismatchException()
        }

        try {
            val existingUsername = userRepository.findByUsername(signupRequest.username)
            if (existingUsername != null) {
                throw UsernameAlreadyExistsException()
            }

            val existingEmail = userRepository.findByEmail(signupRequest.email)
            if (existingEmail != null) {
                throw EmailAlreadyExistsException()
            }

            val existingNickname = userRepository.findByNickname(signupRequest.nickname)
            if (existingNickname != null) {
                throw NicknameAlreadyExistsException()
            }

            val hashedPassword = passwordEncoder.encode(signupRequest.password)
            val userId = snowflakeIdGenerator.nextId()

            val user = User(
                userId = userId,
                username = signupRequest.username,
                password = hashedPassword,
                email = signupRequest.email,
                phoneNumber = "${signupRequest.phone1}${signupRequest.phone2}${signupRequest.phone3}",
                nickname = signupRequest.nickname,
                createdAt = LocalDateTime.now(),
                updatedAt = null,
            )

            val savedUser = userRepository.save(user).awaitSingle()

            return UserVO(
                userId = savedUser.userId,
                username = savedUser.username,
                nickname = savedUser.nickname,
                email = savedUser.email,
                phoneNumber = savedUser.phoneNumber,
                createdAt = savedUser.createdAt,
                updatedAt = savedUser.updatedAt
            )
        } catch (e: Exception) {
            // Print stack trace for debugging
            e.printStackTrace()
            throw UserServiceException("Error occurred during registration")
        }
    }

}