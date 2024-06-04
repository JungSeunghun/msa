package com.pageday.userserver.signup.controller

import com.pageday.userserver.signup.dto.*
import com.pageday.userserver.signup.service.EmailService
import com.pageday.userserver.signup.service.SignupService
import com.pageday.userserver.signup.vo.UserVO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1/api/signup")
class SignupController(
    private val signupService: SignupService,
    private val emailService: EmailService
) {

    @PostMapping("/check-username")
    suspend fun checkUsername(@RequestBody request: CheckUsernameRequest): ResponseEntity<CheckUsernameResponse> {
        val available = signupService.isUsernameAvailable(request.username)
        return ResponseEntity.ok(CheckUsernameResponse(available))
    }

    @PostMapping("/check-nickname")
    suspend fun checkNickname(@RequestBody request: CheckNicknameRequest): ResponseEntity<CheckNicknameResponse> {
        val available = signupService.isNicknameAvailable(request.nickname)
        return ResponseEntity.ok(CheckNicknameResponse(available))
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signup(@RequestBody request: SignupRequest): UserVO {
        return signupService.registerUser(request)
    }

    @PostMapping("/verify-email-code")
    suspend fun verifyEmailCode(@RequestBody request: VerifyEmailCodeRequest): ResponseEntity<VerifyEmailCodeResponse> {
        val verified = emailService.verifyEmailCode(request.email, request.code)
        return ResponseEntity.ok(VerifyEmailCodeResponse(verified))
    }
}