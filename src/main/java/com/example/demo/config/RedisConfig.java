package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Cấu hình Redis cho ứng dụng Spring Boot
 * Lớp này định nghĩa các bean cần thiết để tích hợp Redis với Spring Cache
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Cấu hình RedisTemplate để thao tác với Redis
     * RedisTemplate là một helper class giúp thao tác với Redis dễ dàng hơn
     * 
     * @param connectionFactory Factory để tạo kết nối Redis
     * @return RedisTemplate đã được cấu hình
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Cấu hình Jackson2JsonRedisSerializer để chuyển đổi object thành JSON và ngược lại
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        // Cho phép truy cập vào tất cả các field của object
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // Cho phép lưu thông tin về kiểu dữ liệu khi serialize
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(mapper);

        // Cấu hình serializer cho key và value
        // Key được serialize dưới dạng string
        template.setKeySerializer(new StringRedisSerializer());
        // Value được serialize dưới dạng JSON
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // Hash key và value cũng được serialize tương tự
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }

    /**
     * Cấu hình CacheManager để quản lý cache trong ứng dụng
     * CacheManager sẽ sử dụng Redis làm backend để lưu trữ cache
     * 
     * @param connectionFactory Factory để tạo kết nối Redis
     * @return CacheManager đã được cấu hình
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Cấu hình mặc định cho tất cả các cache
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Thời gian sống mặc định của cache là 10 phút
                .disableCachingNullValues() // Không lưu cache cho các giá trị null
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(Object.class)));

        // Tạo CacheManager với các cấu hình cụ thể cho từng loại cache
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                // Cache "data" có thời gian sống 5 phút
                .withCacheConfiguration("data", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)))
                // Cache "users" có thời gian sống 1 giờ
                .withCacheConfiguration("users", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)))
                .build();
    }
} 