package com.navershop.navershop.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 *
 * @author junnukim1007gmail.com
 * @date 25. 11. 5.
 */
@Configuration
public class NaverApiLimiterConfig {
    @Bean
    public RateLimiter naverRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(3)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofSeconds(5))
                .build();
        return RateLimiter.of("naver-global", config);
    }
}
