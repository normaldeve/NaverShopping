package com.navershop.navershop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 외부 API 연동 설정
 *
 * @author junnukim1007gmail.com
 * @date 25. 10. 29.
 */
@Configuration
public class NaverApiConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
