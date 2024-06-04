package com.pageday.readinghistoryserver.trigger.controller

import com.pageday.readinghistoryserver.common.jwt.util.JwtUtil
import com.pageday.readinghistoryserver.trigger.dto.GetTriggerResponse
import com.pageday.readinghistoryserver.trigger.dto.SaveTriggerRequest
import com.pageday.readinghistoryserver.trigger.dto.SaveTriggerResponse
import com.pageday.readinghistoryserver.trigger.service.TriggerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1/api/trigger")
class TriggerController(
    private val triggerService: TriggerService,
    private val jwtUtil: JwtUtil
) {
    private val logger: Logger = LoggerFactory.getLogger(TriggerController::class.java)

    @GetMapping
    suspend fun getTrigger(@RequestHeader("Authorization") authHeader: String): ResponseEntity<GetTriggerResponse> {
        val token = authHeader.substring(7)

        val userId = jwtUtil.getUserIdFromAccessToken(token)

        val trigger = triggerService.getTrigger(userId)
        return if(trigger != null) {
            ResponseEntity.ok(GetTriggerResponse(trigger.contents))
        } else {
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        }
    }

    @PostMapping("/save")
    suspend fun saveTrigger(@RequestHeader("Authorization") authHeader: String, @RequestBody saveTriggerRequest: SaveTriggerRequest) : ResponseEntity<SaveTriggerResponse> {
        val token = authHeader.substring(7)
        val userId = jwtUtil.getUserIdFromAccessToken(token)

        val trigger = triggerService.saveTrigger(saveTriggerRequest.contents, userId)
        return if (trigger != null) {
            ResponseEntity.ok(SaveTriggerResponse(trigger.contents))
        } else {
            ResponseEntity.status(HttpStatus.NOT_MODIFIED).build()
        }
    }

}