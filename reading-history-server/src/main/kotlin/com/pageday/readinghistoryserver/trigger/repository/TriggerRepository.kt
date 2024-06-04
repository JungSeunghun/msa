package com.pageday.readinghistoryserver.trigger.repository

import com.pageday.readinghistoryserver.trigger.model.Trigger
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TriggerRepository : ReactiveCrudRepository<Trigger, Long> {
    @Query("SELECT * FROM T_TRIGGER WHERE user_id = :userId ORDER BY created_at DESC LIMIT 1")
    suspend fun findLatestByUserId(userId: Long): Trigger?
}
