package com.viewshine.exportexcel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author ChangWei[changwei@viewshine.cn]
 */
@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setConnectTimeout(3000);
        httpComponentsClientHttpRequestFactory.setReadTimeout(3000);
        httpComponentsClientHttpRequestFactory.setConnectionRequestTimeout(3000);
        return new RestTemplate(httpComponentsClientHttpRequestFactory);
    }

}
