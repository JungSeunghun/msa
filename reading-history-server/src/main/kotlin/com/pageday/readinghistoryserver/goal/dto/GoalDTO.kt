package com.pageday.readinghistoryserver.goal.dto

import java.time.LocalDate
import java.time.LocalDateTime


data class GetGoalRequest(
    val date: String
)

data class GetGoalResponse(
    val goalId: Long,
    val actionBeforeReading: String,
    val readingTime: String,
    val action: String,
    val date: LocalDate,
    val completed: Boolean,
)

data class SaveGoalRequest(
    val goalId: Long?,
    val date: String,
    val actionBeforeReading: String,
    val readingTime: String,
    val action: String,
    val completed: Boolean = false
)
data class UpdateGoalCompletionRequest(
    val goalId: Long,
    val completed: Boolean
)