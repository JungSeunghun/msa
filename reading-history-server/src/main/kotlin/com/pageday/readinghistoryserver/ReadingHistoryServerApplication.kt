package com.pageday.readinghistoryserver

import com.pageday.readinghistoryserver.common.webconfig.CorsProperties
import com.pageday.userserver.common.jwt.config.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(
    JwtProperties::class,
    CorsProperties::class,
)
class ReadingHistoryServerApplication

fun main(args: Array<String>) {
    runApplication<ReadingHistoryServerApplication>(*args)
}
