package com.pageday.userserver.common.jwt.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val access: JwtSecretProperties = JwtSecretProperties(),
    val refresh: JwtSecretProperties = JwtSecretProperties()
)

data class JwtSecretProperties(
    var secretKey: String = "",
    var expirationTime: Long = 0
)