package com.pageday.userserver.signup.service

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.Duration
import kotlin.random.asKotlinRandom


@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val redisTemplate: ReactiveRedisTemplate<String, String>
) {
    private val valueOps: ReactiveValueOperations<String, String> = redisTemplate.opsForValue()
    private val encoder = BCryptPasswordEncoder()
    private val random = SecureRandom()

    suspend fun sendEmailCode(email: String): String {
        val code = generateVerificationCode()
        val encryptedCode = encoder.encode(code)
        valueOps.set(email, encryptedCode, Duration.ofMinutes(3)).awaitFirstOrNull()
        sendEmail(email, code)
        return code
    }

    private fun generateVerificationCode(): String {
        val code = (100000..999999).random(random.asKotlinRandom()).toString()
        return code
    }

    private fun sendEmail(to: String, code: String) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setTo(to)
        helper.setSubject("Your Verification Code")
        helper.setText("Your verification code is: $code", true)
        mailSender.send(message)
    }

    suspend fun verifyEmailCode(email: String, code: String): Boolean {
        val storedCode = valueOps.get(email).awaitFirstOrNull() ?: return false
        return encoder.matches(code, storedCode)
    }
}