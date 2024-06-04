package com.pageday.readinghistoryserver.goal.service

import com.pageday.readinghistoryserver.goal.dto.SaveGoalRequest
import com.pageday.readinghistoryserver.goal.dto.UpdateGoalCompletionRequest
import kotlinx.coroutines.flow.toList
import com.pageday.readinghistoryserver.goal.model.Goal
import com.pageday.readinghistoryserver.goal.repository.GoalRepository
import com.pageday.readinghistoryserver.goal.vo.GoalVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime


@Service
class GoalService(
    private val goalRepository: GoalRepository
) {
    suspend fun getGoalByUserIdAndDate(userId: Long, date: LocalDate): Goal? {
        return goalRepository.findByUserIdAndDate(userId, date)
    }

    suspend fun saveGoal(saveGoalRequest: SaveGoalRequest, userId: Long): GoalVO {
        val existingGoal = goalRepository.findByUserIdAndDate(userId, LocalDate.parse(saveGoalRequest.date))

        val goal = if (existingGoal != null) {
            existingGoal.copy(
                actionBeforeReading = saveGoalRequest.actionBeforeReading,
                readingTime = saveGoalRequest.readingTime,
                action = saveGoalRequest.action,
                updatedAt = LocalDateTime.now(),
            )
        } else {
            Goal(
                goalId = saveGoalRequest.goalId,
                userId = userId,
                date = LocalDate.parse(saveGoalRequest.date),
                actionBeforeReading = saveGoalRequest.actionBeforeReading,
                readingTime = saveGoalRequest.readingTime,
                action = saveGoalRequest.action,
                completed = saveGoalRequest.completed,
                createdAt = LocalDateTime.now(),
                updatedAt = null
            )
        }
        val savedGoal = goalRepository.save(goal).awaitSingle()
        return GoalVO (
            goalId = savedGoal.goalId,
            userId = savedGoal.userId,
            date = savedGoal.date,
            actionBeforeReading = savedGoal.actionBeforeReading,
            readingTime = savedGoal.readingTime,
            action = savedGoal.action,
            completed = savedGoal.completed,
            createdAt = savedGoal.createdAt,
            updatedAt = savedGoal.updatedAt
        )
    }

    suspend fun updateGoalCompletion(updateGoalCompletionRequest: UpdateGoalCompletionRequest, userId: Long): Goal? {
        val goal = goalRepository.findById(updateGoalCompletionRequest.goalId).awaitSingle()
        return if (goal != null && goal.userId == userId) {
            val updatedGoal = goal.copy(completed = updateGoalCompletionRequest.completed)
            goalRepository.save(updatedGoal).awaitSingle()
        } else {
            null
        }
    }

    fun getAllGoalsByUserId(userId: Long): Flow<Goal> {
        return goalRepository.findAllByUserId(userId)
    }
}