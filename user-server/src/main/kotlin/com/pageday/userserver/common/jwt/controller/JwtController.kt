package com.pageday.userserver.common.jwt.controller

import com.pageday.userserver.common.jwt.dto.*
import com.pageday.userserver.common.jwt.service.JwtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/api/jwt")
class JwtController(
    private val jwtService: JwtService
) {

    @PostMapping("/refresh-token")
    suspend fun refreshToken(@RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<RefreshTokenResponse> {
        val refreshTokenResponse = jwtService.refreshToken(refreshTokenRequest.refreshToken)
        return if (refreshTokenResponse != null) {
            ResponseEntity.ok(refreshTokenResponse)
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/validate")
    suspend fun validate(@RequestHeader("Authorization") accessToken: String): ResponseEntity<ValidateResponse> {
        val accessToken = accessToken.replace("Bearer ", "")
        val validateResponse = jwtService.validateAccessToken(accessToken)
        return if (validateResponse != null) {
            ResponseEntity.ok(validateResponse)
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}
