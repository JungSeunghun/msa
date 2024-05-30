package com.pageday.userserver.signup.dto

data class SignupRequest(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val name: String,
    val year: String,
    val month: String,
    val day: String,
    val phone1: String,
    val phone2: String,
    val phone3: String,
    val nickname: String
)

data class CheckUsernameRequest(
    val username: String
)

data class CheckUsernameResponse(
    val available: Boolean
)

data class CheckEmailRequest(
    val email: String
)

data class CheckEmailResponse(
    val available: Boolean
)

data class CheckNicknameRequest(
    val nickname: String
)

data class CheckNicknameResponse(
    val available: Boolean
)

data class SendEmailCodeRequest(
    val email: String
)

data class SendEmailCodeResponse(
    val code: String
)

data class VerifyEmailCodeRequest(
    val email: String,
    val code: String
)

data class VerifyEmailCodeResponse(
    val verified: Boolean
)