package com.libraryManagementSystem.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis cache configuration – active only when spring.cache.type=redis.
 * Provides per-cache TTL settings for each cache region.
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisCacheConfig {

    /**
     * Builds the ObjectMapper used for JSON serialisation of cached objects.
     * Includes JavaTimeModule for LocalDate / LocalDateTime support.
     */
    private ObjectMapper cacheObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Store type info so polymorphic types round-trip correctly
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return mapper;
    }

    /**
     * Default cache configuration: JSON serialisation, null values not cached.
     */
    private RedisCacheConfiguration defaultCacheConfig() {
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(cacheObjectMapper());

        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Per-cache TTL overrides
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Individual book by ID – 10 minutes
        cacheConfigurations.put("books", defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));

        // Book search results – 5 minutes (changes more frequently due to new additions)
        cacheConfigurations.put("allBooks", defaultCacheConfig().entryTtl(Duration.ofMinutes(5)));

        // Reference data – 30 minutes (rarely changes)
        cacheConfigurations.put("categories", defaultCacheConfig().entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("authors",    defaultCacheConfig().entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("publishers", defaultCacheConfig().entryTtl(Duration.ofMinutes(30)));

        // Dashboard analytics – 2 minutes (near real-time feel)
        cacheConfigurations.put("dashboard", defaultCacheConfig().entryTtl(Duration.ofMinutes(2)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
