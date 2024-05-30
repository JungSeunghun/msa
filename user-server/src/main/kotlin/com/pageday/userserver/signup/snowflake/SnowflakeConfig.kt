package com.pageday.userserver.config

import com.pageday.userserver.signup.snowflake.SnowflakeIdGenerator
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "snowflake")
class SnowflakeConfig {
    var datacenterId: Long = 0L
    var workerId: Long = 0L

    @Bean
    fun snowflakeIdGenerator(): SnowflakeIdGenerator {
        return SnowflakeIdGenerator(datacenterId, workerId)
    }
}
