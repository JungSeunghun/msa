package com.pageday.readinghistoryserver.goal.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Table("T_GOAL")
data class Goal(
    @Id
    val goalId: Long?,
    val userId: Long,
    val actionBeforeReading: String,
    val readingTime: String,
    val action: String,
    val date: LocalDate,
    var completed: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
