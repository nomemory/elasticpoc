package net.andreinc.elasticpoc.catalog.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.elasticsearch.client.RestClient.builder;

@Configuration
public class ElasticSearchConfig {
    @Value("${elasticsearch.host}")
    private String elasticsearchHost;

    @Value("${elasticsearch.port}")
    private Integer elasticsearchPort;

    @Bean(destroyMethod = "close")
    public RestHighLevelClient rhlClient() {
        return new RestHighLevelClient(builder(new HttpHost(elasticsearchHost, elasticsearchPort, "http")));
    }
}
