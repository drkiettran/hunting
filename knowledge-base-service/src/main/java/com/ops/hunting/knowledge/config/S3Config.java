package com.ops.hunting.knowledge.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

	@Value("${aws.region:us-east-1}")
	private String awsRegion;

	@Value("${aws.s3.endpoint:}")
	private String s3Endpoint;

	@Value("${aws.accessKey:}")
	private String accessKey;

	@Value("${aws.secretKey:}")
	private String secretKey;

	@Bean
	public S3Client s3Client() {
		var builder = S3Client.builder().region(Region.of(awsRegion));

		// Configure credentials if provided
		if (!accessKey.isEmpty() && !secretKey.isEmpty()) {
			AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
			builder.credentialsProvider(StaticCredentialsProvider.create(credentials));
		}

		// Configure endpoint if provided (for LocalStack or custom S3-compatible
		// storage)
		if (!s3Endpoint.isEmpty()) {
			builder.endpointOverride(URI.create(s3Endpoint));
		}

		return builder.build();
	}
}
