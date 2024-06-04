package com.pageday.readinghistoryserver.trigger.service

import com.pageday.readinghistoryserver.trigger.dto.SaveTriggerRequest
import com.pageday.readinghistoryserver.trigger.model.Trigger
import com.pageday.readinghistoryserver.trigger.repository.TriggerRepository
import com.pageday.readinghistoryserver.trigger.vo.TriggerVO
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class TriggerService(
    private val triggerRepository: TriggerRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(TriggerService::class.java)

    suspend fun getTrigger(userId: Long): TriggerVO? {
        val trigger = triggerRepository.findLatestByUserId(userId)
        if(trigger == null) {
            return null
        } else {
            val triggerVO = TriggerVO(
                triggerId = trigger.triggerId,
                userId = trigger.userId,
                contents = trigger.contents,
                createdAt = trigger.createdAt,
                updatedAt = trigger.updatedAt
            )
            return triggerVO
        }
    }

    suspend fun saveTrigger(contents: String, userId: Long): TriggerVO {
        val existingTrigger = triggerRepository.findLatestByUserId(userId)

        val trigger = if (existingTrigger != null) {
            existingTrigger.copy(
                contents = contents,
                updatedAt = LocalDateTime.now()
            )
        } else {
            Trigger(
                triggerId = null,
                userId = userId,
                contents = contents,
                createdAt = LocalDateTime.now(),
                updatedAt = null
            )
        }

        val savedTrigger = triggerRepository.save(trigger).awaitSingle()
        return TriggerVO(
            triggerId = savedTrigger.triggerId,
            userId = savedTrigger.userId,
            contents = savedTrigger.contents,
            createdAt = savedTrigger.createdAt,
            updatedAt = savedTrigger.updatedAt
        )
    }
}