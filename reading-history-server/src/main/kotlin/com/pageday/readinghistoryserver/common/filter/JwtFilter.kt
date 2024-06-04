package com.pageday.readinghistoryserver.common.filter

import com.pageday.readinghistoryserver.common.jwt.util.JwtUtil
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Component
class JwtFilter(private val jwtUtil: JwtUtil) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authHeader = exchange.request.headers.getFirst("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            val userId = jwtUtil.getUserIdFromAccessToken(token)
            exchange.attributes["userId"] = userId
        }
        return chain.filter(exchange)
    }

}