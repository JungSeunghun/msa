package com.pageday.readinghistoryserver.goal.controller

import com.pageday.readinghistoryserver.common.jwt.util.JwtUtil
import com.pageday.readinghistoryserver.goal.dto.GetGoalResponse
import com.pageday.readinghistoryserver.goal.dto.SaveGoalRequest
import com.pageday.readinghistoryserver.goal.dto.UpdateGoalCompletionRequest
import com.pageday.readinghistoryserver.goal.model.Goal
import com.pageday.readinghistoryserver.goal.service.GoalService
import com.pageday.readinghistoryserver.goal.vo.GoalVO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


@RestController
@RequestMapping("/v1/api/goal")
class GoalController(
    private val goalService: GoalService,
    private val jwtUtil: JwtUtil
) {
    @GetMapping
    suspend fun getGoal(
        @RequestHeader("Authorization") authHeader: String,
        @RequestParam date: String,
    ): ResponseEntity<GetGoalResponse> {
        val token = authHeader.substring(7)
        val userId = jwtUtil.getUserIdFromAccessToken(token)

        val date = LocalDate.parse(date)
        val goal = goalService.getGoalByUserIdAndDate(userId, date)
        return if (goal != null) {
            val goalResponse = GetGoalResponse(
                goalId = goal.goalId!!,
                actionBeforeReading = goal.actionBeforeReading,
                readingTime = goal.readingTime,
                action = goal.action,
                date = goal.date,
                completed = goal.completed
            )
            ResponseEntity.ok(goalResponse)
        } else {
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
    }

    @PostMapping
    suspend fun saveGoal(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody saveGoalRequest: SaveGoalRequest
    ): ResponseEntity<GoalVO> {
        val token = authHeader.substring(7)
        val userId = jwtUtil.getUserIdFromAccessToken(token)

        val savedGoal = goalService.saveGoal(saveGoalRequest, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGoal)
    }


    @PutMapping("/completion")
    suspend fun updateGoalCompletion(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody updateGoalCompletionRequest: UpdateGoalCompletionRequest
    ): Goal? {
        val token = authHeader.substring(7)
        val userId = jwtUtil.getUserIdFromAccessToken(token)

        return goalService.updateGoalCompletion(updateGoalCompletionRequest, userId)
    }
}