package com.pageday.readinghistoryserver.trigger.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("T_TRIGGER")
data class Trigger(
    @Id
    val triggerId: Long?,
    val userId: Long,
    val contents: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
