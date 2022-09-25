package com.hacademy.docker.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "docker.config")
public class DockerConfigurationProperty {
	private String tcpHost, httpHost;
}
