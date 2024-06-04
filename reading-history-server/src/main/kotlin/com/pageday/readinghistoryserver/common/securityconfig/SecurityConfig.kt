package com.pageday.readinghistoryserver.common.securityconfig

import com.pageday.readinghistoryserver.common.jwt.util.JwtUtil
import com.pageday.userserver.common.jwt.config.JwtProperties
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*
import javax.crypto.SecretKey


@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtProperties: JwtProperties,
    private val jwtUtil: JwtUtil
) {
    private val accessSecretKey: SecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.access.secretKey))
    private val logger: Logger = LoggerFactory.getLogger(SecurityConfig::class.java)

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.csrf { csrf -> csrf.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers(
                        "/v1/api/signup/**",
                        "/v1/api/signin/**",
                    ).permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun jwtAuthenticationWebFilter(): AuthenticationWebFilter {
        val authenticationManager = jwtAuthenticationManager()
        val authenticationFilter = AuthenticationWebFilter(authenticationManager)
        authenticationFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter())
        return authenticationFilter
    }

    @Bean
    fun jwtAuthenticationManager(): ReactiveAuthenticationManager {
        return ReactiveAuthenticationManager { authentication ->
            val accessToken = authentication.credentials.toString()
            if (jwtUtil.validateAccessToken(accessToken)) {
                val claims = jwtUtil.getClaimsFromToken(accessToken, accessSecretKey)
                val username = claims.subject
                val userid = claims.get("userid", String::class.java)

                val auth = UsernamePasswordAuthenticationToken(username, accessToken, listOf<GrantedAuthority>())
                auth.details = mapOf("userid" to userid)
                Mono.just(auth)
            } else {
                logger.warn("JWT Access Token validation failed")
                Mono.empty()
            }
        }
    }

    @Bean
    fun jwtServerAuthenticationConverter(): ServerAuthenticationConverter {
        return ServerAuthenticationConverter { exchange: ServerWebExchange ->
            val token = jwtUtil.extractToken(exchange)
            if (token != null) {
                val auth = UsernamePasswordAuthenticationToken(null, token)
                Mono.just(auth)
            } else {
                Mono.empty()
            }
        }
    }
}
