package kr.co.music.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.host}")
    private val host: String,

    @Value("\${spring.data.redis.port}")
    private val port: Int
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisConfig = RedisStandaloneConfiguration(host, port)
        return LettuceConnectionFactory(redisConfig)
    }
}