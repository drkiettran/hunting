package com.ops.hunting.threatintel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration.MaybeSecureClientConfigurationBuilder;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.hunting.threatintelligence.repository")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

	@Value("${spring.elasticsearch.rest.uris:http://localhost:9200}")
	private String elasticsearchUrl;

	@Value("${spring.elasticsearch.rest.username:}")
	private String username;

	@Value("${spring.elasticsearch.rest.password:}")
	private String password;

	@Override
	public ClientConfiguration clientConfiguration() {
		MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder()
				.connectedTo(elasticsearchUrl.replace("http://", "").replace("https://", ""));

		if (!username.isEmpty() && !password.isEmpty()) {
			builder.withBasicAuth(username, password);
		}

		return builder.build();
	}
}
