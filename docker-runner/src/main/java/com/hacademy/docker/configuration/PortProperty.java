package com.hacademy.docker.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "docker.port")
public class PortProperty {
	private int begin, end;
	public int range() {
		return end - begin + 1;
	}
}
