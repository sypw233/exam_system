package ccc.sypw.onlineExamSystem.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import java.util.*
import javax.crypto.SecretKey


object JwtUtils {

    private var SECRET_KEY: SecretKey = Jwts.SIG.HS256.key().build()
    private const val EXPIRATION_TIME = 1000 * 60 * 60 * 24 // 过期时间：1天
    private const val ISSUER = "OnlineExamSystem" // 可以设置为你的应用名称

    /**
     * 生成JWT Token
     */
    fun generateToken(username: String, role: String): String {
        return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .issuer(ISSUER)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SECRET_KEY)
            .compact()
    }

    /**
     * 从JWT Token中提取用户名（即 Subject）
     */
    fun getUsernameFromToken(token: String): String? {
        return getClaimsFromToken(token)?.payload?.subject
    }


    fun getRoleFromToken(token: String): String? {
        return getClaimsFromToken(token)?.payload?.get("role") as? String
    }

    fun getIdFromToken(token: String): Long? {
        return getClaimsFromToken(token)?.payload?.get("id") as? Long
    }

    /**
     * 从JWT Token中获取Claims
     */
    private fun getClaimsFromToken(token: String): Jws<Claims>? {
        return try {
            Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * 验证JWT Token是否有效
     */
    fun validateToken(token: String, username: String): Boolean {
        val claims = getClaimsFromToken(token)
        return claims != null && claims.payload?.subject == username && !isTokenExpired(token)
    }

    /**
     * 判断Token是否过期
     */
    private fun isTokenExpired(token: String): Boolean {
        val expirationDate = getClaimsFromToken(token)?.payload?.expiration
        return expirationDate?.before(Date()) ?: true
    }

}
