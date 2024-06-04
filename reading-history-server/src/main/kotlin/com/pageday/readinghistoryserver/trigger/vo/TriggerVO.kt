package com.pageday.readinghistoryserver.trigger.vo

import java.time.LocalDateTime

data class TriggerVO(
    val triggerId: Long?,
    val userId: Long,
    val contents: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
