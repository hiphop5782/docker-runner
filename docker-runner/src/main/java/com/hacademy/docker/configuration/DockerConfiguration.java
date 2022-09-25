package com.hacademy.docker.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

@Configuration
public class DockerConfiguration {
	
	@Autowired
	private DockerConfigurationProperty property;

	@Bean
	public DockerClientConfig dockerClientConfig() {
		return DefaultDockerClientConfig.createDefaultConfigBuilder()
													.withDockerHost(property.getTcpHost())
													.build();
	}
	
	@Bean
	public DockerClient dockerClient() {
		return DockerClientBuilder.getInstance(dockerClientConfig()).build();
	}
	
}
