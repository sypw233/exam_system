package ccc.sypw.onlineExamSystem.filter

import ccc.sypw.onlineExamSystem.util.JwtUtils
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import java.io.IOException


@WebFilter(urlPatterns = ["/*"], asyncSupported = true) // 过滤所有请求，启用异步支持
class JwtAuthenticationFilter : Filter {
    @Throws(ServletException::class, IOException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val uri = httpRequest.requestURI
        println("当前请求$uri")
        if (uri == "/api/users/login" || uri == "/api/users/register" || uri.isNotEmpty()) {
            // 跳过鉴权，直接执行后续过滤器
            chain.doFilter(request, response)
            return
        }

        // 获取Authorization头部中的Token
        val token = httpRequest.getHeader("Authorization")?.takeIf { it.startsWith("Bearer ") }?.substring(7)
        if (token != null && JwtUtils.validateToken(token, "username")) {
            // Token有效，检查用户角色
            println("token有效$token")
            val role = JwtUtils.getRoleFromToken(token)
            if (role == null) {
                response.contentType = "application/json"
                response.writer.write("{\"error\": \"Role not found\"}")
                return
            }

            // 根据角色做不同权限的控制
            if (isAuthorizedForRole(role, uri)) {
                // 角色有权限访问该资源，允许继续访问
                chain.doFilter(request, response)
            } else {
                // 角色没有权限访问该资源，返回403 Forbidden
                response.contentType = "application/json"
                response.writer.write("{\"error\": \"Forbidden: You don't have permission to access this resource\"}")
            }
        } else {
            println("token无效")
            // Token无效或未提供，返回未授权响应
            response.contentType = "application/json"
            response.writer.write("{\"error\": \"Unauthorized\"}")
        }
    }

    /**
     * 根据角色判断是否有权限访问特定的URL
     */
    private fun isAuthorizedForRole(role: String, uri: String): Boolean {
        println("$uri 当前用户$role")
        return when (role) {
            "admin" -> {
                true
            }

            "teacher" -> {
                !uri.startsWith("/api/exams")
                !uri.startsWith("/api/exam-submissions")
                !uri.startsWith("/api/questions")
                !uri.startsWith("/api/courses")
                !uri.startsWith("/api/course-selections")
            }

            "student" -> {
                !uri.startsWith("/api/exams/")
                !uri.startsWith("/api/exam-submissions")
                !uri.startsWith("/api/questions")
                !uri.startsWith("/api/courses")
                !uri.startsWith("/api/course-selections")
            }

            else -> false
        }
    }

    override fun init(filterConfig: FilterConfig?) {
        // 初始化过滤器时的配置（如果有需要）
    }

    override fun destroy() {
        // 清理资源（如果有需要）
    }
}
