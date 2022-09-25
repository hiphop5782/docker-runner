package com.hacademy.docker.constant;

import lombok.Getter;

@Getter
public enum DockerType {
	JDK8("hiphop5782/jdk:8", 10008), 
	JDK11("hiphop5782/jdk:11", 10011), 
	JDK13("hiphop5782/jdk:13", 10013), 
	JDK17("hiphop5782/jdk:17", 10017);
	
	private String dockerImage;
	private int port;
	private DockerType(String dockerImage, int port) {
		this.dockerImage = dockerImage;
		this.port = port;
	}
	public String getPortString() {
		return String.valueOf(port);
	}
	public static DockerType findImage(String name) {
		for(DockerType type : values()) {
			if(type.getDockerImage().equals(name))
				return type;
		}
		return null;
	}
}
