package com.pageday.userserver.common.jwt.util

import com.pageday.userserver.common.jwt.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtUtil(private val jwtProperties: JwtProperties) {

    private val logger: Logger = LoggerFactory.getLogger(JwtUtil::class.java)

    private val accessSecretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.access.secretKey))
    private val refreshSecretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.refresh.secretKey))
    private val accessExpirationTime: Long = jwtProperties.access.expirationTime
    private val refreshExpirationTime: Long = jwtProperties.refresh.expirationTime

    fun generateAccessToken(userName: String, userId: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + accessExpirationTime)

        return Jwts.builder()
            .subject(userName)
            .claim("userId", userId)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(accessSecretKey)
            .compact()
    }

    fun generateRefreshToken(userName: String, userId: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshExpirationTime)

        return Jwts.builder()
            .subject(userName)
            .claim("userId", userId)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(refreshSecretKey)
            .compact()
    }

    fun validateAccessToken(token: String): Boolean {
        try {
            Jwts.parser().verifyWith(accessSecretKey).build().parseSignedClaims(token)
            return true
        } catch (e: Exception) {
            logger.error("Invalid access token", e);
            return false
        }
    }

    fun validateRefreshToken(token: String): Boolean {
        try {
            Jwts.parser().verifyWith(refreshSecretKey).build().parseSignedClaims(token)
            return true
        } catch (e: Exception) {
            logger.error("Invalid refresh token", e);
            return false
        }
    }

    fun getClaimsFromToken(token: String, secretKey: SecretKey): Claims {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload
    }

    fun getUsernameFromAccessToken(accessToken: String): String {
        return getClaimsFromToken(accessToken, accessSecretKey).subject
    }

    fun getUsernameFromRefreshToken(refreshToken: String): String {
        return getClaimsFromToken(refreshToken, refreshSecretKey).subject
    }

    fun getUserIdFromAccessToken(accessToken: String) : Long {
        return getClaimsFromToken(accessToken, accessSecretKey).get("userId", Long::class.java)
    }

    fun getUserIdFromRefreshToken(refreshToken: String) : Long {
        return getClaimsFromToken(refreshToken, refreshSecretKey).get("userId", Long::class.java)
    }

    fun extractToken(exchange: ServerWebExchange): String? {
        val authHeader = exchange.request.headers.getFirst("Authorization")
        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else {
            logger.warn("Authorization header not found or does not start with Bearer")
            null
        }
    }
}