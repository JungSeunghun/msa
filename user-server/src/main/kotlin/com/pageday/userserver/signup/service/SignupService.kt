package com.pageday.userserver.signup.service

import com.pageday.userserver.common.exception.*
import com.pageday.userserver.signup.dto.SignupRequest
import com.pageday.userserver.common.user.model.User
import com.pageday.userserver.common.user.repository.UserRepository
import com.pageday.userserver.common.snowflake.SnowflakeIdGenerator
import com.pageday.userserver.signup.vo.UserVO
import com.pageday.userserver.common.validation.RegexPatterns
import com.pageday.userserver.common.validation.validatePattern
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

@Service
class SignupService(
    private val userRepository: UserRepository,
    private val snowflakeIdGenerator: SnowflakeIdGenerator,
    private val passwordEncoder: PasswordEncoder
) {

    private val logger: Logger = LoggerFactory.getLogger(SignupService::class.java)

    suspend fun isUsernameAvailable(username: String): Boolean {
        logger.info("Checking if username is available: $username")
        return userRepository.findByUsername(username) == null
    }

    suspend fun isNicknameAvailable(nickname: String): Boolean {
        logger.info("Checking if nickname is available: $nickname")
        return userRepository.findByNickname(nickname) == null
    }

    suspend fun registerUser(signupRequest: SignupRequest): UserVO {
        validatePattern(RegexPatterns.name, signupRequest.name, "Name")
        validatePattern(RegexPatterns.username, signupRequest.username, "Username")
        validatePattern(RegexPatterns.email, signupRequest.email, "Email")
        validatePattern(RegexPatterns.nickname, signupRequest.nickname, "Nickname")
        validatePattern(RegexPatterns.password, signupRequest.password, "Password")
        validatePattern(RegexPatterns.phone1, signupRequest.phone1, "Phone1")
        validatePattern(RegexPatterns.phone2, signupRequest.phone2, "Phone2")
        validatePattern(RegexPatterns.phone3, signupRequest.phone3, "Phone3")

        if (signupRequest.password != signupRequest.confirmPassword) {
            logger.error("Password mismatch for user: ${signupRequest.username}")
            throw PasswordMismatchException()
        }

        try {
            logger.info("Checking if username already exists: ${signupRequest.username}")
            val existingUsername = userRepository.findByUsername(signupRequest.username)
            if (existingUsername != null) {
                logger.warn("Username already exists: ${signupRequest.username}")
                throw UsernameAlreadyExistsException()
            }

            logger.info("Checking if nickname already exists: ${signupRequest.nickname}")
            val existingNickname = userRepository.findByNickname(signupRequest.nickname)
            if (existingNickname != null) {
                logger.warn("Nickname already exists: ${signupRequest.nickname}")
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

            logger.info("Saving user: ${signupRequest.username}")
            val savedUser = userRepository.save(user).awaitSingle()

            logger.info("User registration successful: ${signupRequest.username}")
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
            logger.error("Error occurred during registration for user: ${signupRequest.username}", e)
            throw UserServiceException("Error occurred during registration")
        }
    }
}
