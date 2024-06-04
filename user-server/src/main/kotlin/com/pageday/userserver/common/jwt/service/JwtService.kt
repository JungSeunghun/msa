package com.pageday.userserver.common.jwt.service

import com.pageday.userserver.common.jwt.util.JwtUtil
import com.pageday.userserver.common.jwt.dto.RefreshTokenResponse
import com.pageday.userserver.common.jwt.dto.ValidateResponse
import org.springframework.stereotype.Service

@Service
class JwtService(
    private val jwtUtil: JwtUtil
) {

    suspend fun refreshToken(refreshToken: String): RefreshTokenResponse? {
        return if (jwtUtil.validateRefreshToken(refreshToken)) {
            val userName = jwtUtil.getUsernameFromRefreshToken(refreshToken)
            val userId = jwtUtil.getUserIdFromRefreshToken(refreshToken)
            val newAccessToken = jwtUtil.generateAccessToken(userName, userId)
            val newRefreshToken = jwtUtil.generateRefreshToken(userName, userId)
            RefreshTokenResponse(newAccessToken, newRefreshToken)
        } else {
            null
        }
    }

    suspend fun validateAccessToken(accessToken: String): ValidateResponse? {
        return if (jwtUtil.validateAccessToken(accessToken)) {
            val userId = jwtUtil.getUsernameFromAccessToken(accessToken)
            ValidateResponse(userId)
        } else {
            null
        }
    }
}
