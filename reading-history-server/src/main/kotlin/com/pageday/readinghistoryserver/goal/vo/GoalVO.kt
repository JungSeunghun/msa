package com.pageday.readinghistoryserver.goal.vo

import java.time.LocalDate
import java.time.LocalDateTime

data class GoalVO(
    val goalId: Long?,
    val userId: Long,
    val actionBeforeReading: String,
    val readingTime: String,
    val action: String,
    val date: LocalDate,
    val completed: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
