package com.ops.hunting.knowledge.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

	@Value("${elasticsearch.host:localhost}")
	private String elasticsearchHost;

	@Value("${elasticsearch.port:9200}")
	private int elasticsearchPort;

	@Value("${elasticsearch.scheme:http}")
	private String elasticsearchScheme;

	@Bean
	public RestHighLevelClient elasticsearchClient() {
		return new RestHighLevelClient(
				RestClient.builder(new HttpHost(elasticsearchHost, elasticsearchPort, elasticsearchScheme)));
	}
}
