package ccc.sypw.onlineExamSystem.dto

data class PageResponse(
    val current: Int,
    val size: Int,
    val total: Int,
    val records: Any?
)