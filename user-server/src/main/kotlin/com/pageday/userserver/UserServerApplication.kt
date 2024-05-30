package com.pageday.userserver

import com.pageday.userserver.common.webconfig.CorsProperties
import com.pageday.userserver.config.SnowflakeConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(SnowflakeConfig::class, CorsProperties::class)
class UserServerApplication

fun main(args: Array<String>) {
	runApplication<UserServerApplication>(*args)
}
