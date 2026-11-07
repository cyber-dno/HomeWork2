package gigabank.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {
    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.jedis.pool.max-active:8}")
    private int poolMaxActive;

    @Value("${spring.data.redis.jedis.pool.max-idle:8}")
    private int poolMaxIdle;

    @Value("${spring.data.redis.jedis.pool.min-idle:0}")
    private int poolMinIdle;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);

        GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(poolMaxActive);
        poolConfig.setMaxIdle(poolMaxIdle);
        poolConfig.setMinIdle(poolMinIdle);

        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .usePooling()
                .poolConfig(poolConfig)
                .build();

        return new JedisConnectionFactory(config, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJacksonJsonRedisSerializer jsonSerializer = jsonSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig())
                .withInitialCacheConfigurations(customCacheConfig())
                .transactionAware()
                .build();
    }

    private GenericJacksonJsonRedisSerializer jsonSerializer() {
        return GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(
                        BasicPolymorphicTypeValidator.builder()
                            .allowIfSubType("gigabank.model")
                            .build())
                .build();
    }

    private RedisCacheConfiguration baseCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer()));
    }

    private RedisCacheConfiguration defaultCacheConfig() {
        return baseCacheConfig().entryTtl(Duration.ofMinutes(5));
    }

    private Map<String, RedisCacheConfiguration> customCacheConfig() {
        Map<String, RedisCacheConfiguration> config = new HashMap<>();
        config.put("userCache",
                baseCacheConfig().entryTtl(Duration.ofMinutes(5)));
        config.put("accountsCache",
                baseCacheConfig().entryTtl(Duration.ofMinutes(10)));
        config.put("transactionsCache",
                baseCacheConfig().entryTtl(Duration.ofMinutes(2)));
        return config;
    }
}
