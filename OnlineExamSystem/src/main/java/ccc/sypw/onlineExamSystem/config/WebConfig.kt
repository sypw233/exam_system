package ccc.sypw.onlineExamSystem.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    
    /**
     * 配置CORS跨域支持
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // 允许所有路径进行跨域
            .allowedOrigins("http://localhost:5173") // 允许的前端地址
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
            .allowedHeaders("*") // 允许所有请求头
            .allowCredentials(true) // 允许携带 cookie
            .maxAge(3600) // 设置预检请求的有效期
    }
    
    /**
     * 配置异步支持，用于流式响应
     */
    override fun configureAsyncSupport(configurer: AsyncSupportConfigurer) {
        configurer.setDefaultTimeout(60000) // 设置异步请求超时时间为60秒
    }
}
