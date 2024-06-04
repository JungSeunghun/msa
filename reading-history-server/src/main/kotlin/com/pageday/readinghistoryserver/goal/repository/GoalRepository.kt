package com.pageday.readinghistoryserver.goal.repository

import com.pageday.readinghistoryserver.goal.model.Goal
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface GoalRepository: ReactiveCrudRepository<Goal, Long> {
    suspend fun findByUserIdAndDate(userId: Long, date: LocalDate): Goal?
    fun findAllByUserId(userId: Long): Flow<Goal>
}