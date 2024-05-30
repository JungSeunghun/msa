package com.pageday.userserver.signup.controller

import com.pageday.userserver.signup.dto.*
import com.pageday.userserver.signup.service.EmailService
import com.pageday.userserver.signup.service.UserService
import com.pageday.userserver.signup.vo.UserVO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1/api/signup")
class UserController(
    private val userService: UserService,
    private val emailService: EmailService
) {

    @PostMapping("/check-username")
    suspend fun checkUsername(@RequestBody request: CheckUsernameRequest): ResponseEntity<CheckUsernameResponse> {
        val available = userService.isUsernameAvailable(request.username)
        return ResponseEntity.ok(CheckUsernameResponse(available))
    }

    @PostMapping("/check-nickname")
    suspend fun checkNickname(@RequestBody request: CheckNicknameRequest): ResponseEntity<CheckNicknameResponse> {
        val available = userService.isNicknameAvailable(request.nickname)
        return ResponseEntity.ok(CheckNicknameResponse(available))
    }

    @PostMapping("check-email")
    suspend fun checkEmail(@RequestBody request: CheckEmailRequest): ResponseEntity<CheckEmailResponse> {
        val available = userService.isEmailAvailable(request.email)
        return ResponseEntity.ok(CheckEmailResponse(available))
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signup(@RequestBody request: SignupRequest): UserVO {
        return userService.registerUser(request)
    }

    @PostMapping("/send-email-code")
    suspend fun sendEmailCode(@RequestBody request: SendEmailCodeRequest): ResponseEntity<SendEmailCodeResponse> {
        val code = emailService.sendEmailCode(request.email)
        return ResponseEntity.ok(SendEmailCodeResponse(code))
    }

    @PostMapping("/verify-email-code")
    suspend fun verifyEmailCode(@RequestBody request: VerifyEmailCodeRequest): ResponseEntity<VerifyEmailCodeResponse> {
        val verified = emailService.verifyEmailCode(request.email, request.code)
        return ResponseEntity.ok(VerifyEmailCodeResponse(verified))
    }
}