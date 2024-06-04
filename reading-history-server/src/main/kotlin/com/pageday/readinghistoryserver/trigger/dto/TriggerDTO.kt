package com.pageday.readinghistoryserver.trigger.dto

data class GetTriggerRequest(val id: Long)
data class GetTriggerResponse(val contents: String)
data class SaveTriggerRequest(val contents: String)
data class SaveTriggerResponse(val contents: String)