package ccc.sypw.onlineExamSystem.dto

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
) {
    companion object {
        /**
         * 成功响应
         */
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(200, "操作成功", data)
        }
        
        /**
         * 成功响应（无数据）
         */
        fun success(message: String = "操作成功"): ApiResponse<String> {
            return ApiResponse(200, message, message)
        }
        
        /**
         * 错误响应
         */
        fun <T> error(message: String, code: Int = 400): ApiResponse<T> {
            return ApiResponse(code, message, null)
        }
    }
}