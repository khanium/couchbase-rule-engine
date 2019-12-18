package com.couchbase.demo.config;

import com.couchbase.client.java.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RulerConfig {
    public static final String FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";
    @Value("${spring.couchbase.username}")
    private String username;
    @Value("${spring.couchbase.password}")
    private String password;
    @Value("${spring.couchbase.eventing.url}")
    private String eventingNode;

    @Bean
    public WebClient webClient(Bucket bucket) {
        //TODO load webclient properties...
        // get Eventing Service Nodes
        // pick one
        return WebClient.builder().baseUrl(eventingNode).defaultHeaders(header -> header.setBasicAuth(username, password)).build();
    }


}
