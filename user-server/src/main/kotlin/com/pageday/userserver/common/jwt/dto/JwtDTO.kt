package com.pageday.userserver.common.jwt.dto


data class RefreshTokenRequest(val refreshToken: String)
data class RefreshTokenResponse(val accessToken: String, val refreshToken: String)
data class ValidateResponse(val username: String)