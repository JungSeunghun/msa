package com.pageday.userserver.signin.controller

import com.pageday.userserver.common.jwt.util.JwtUtil
import com.pageday.userserver.signin.dto.LoginRequest
import com.pageday.userserver.signin.dto.LoginResponse
import com.pageday.userserver.signin.service.SigninService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/api/signin")
class SigninController(
    private val signinService: SigninService,
    private val jwtUtil: JwtUtil
) {

    @PostMapping
    suspend fun signin(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val (username, password) = loginRequest
        val user= signinService.signin(username, password)
        if (user != null) {
            val accessToken = jwtUtil.generateAccessToken(user.username, user.userId!!)
            val refreshToken = jwtUtil.generateRefreshToken(user.username, user.userId)
            return ResponseEntity.ok(LoginResponse(accessToken, refreshToken))
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}