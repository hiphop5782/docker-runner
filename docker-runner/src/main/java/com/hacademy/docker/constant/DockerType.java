package com.hacademy.docker.constant;

import lombok.Getter;

@Getter
public enum DockerType {
	JDK8("hiphop5782/jdk:8"), 
	JDK11("hiphop5782/jdk:11"), 
	JDK13("hiphop5782/jdk:13"), 
	JDK17("hiphop5782/jdk:17");
	
	private String dockerImage;
	private DockerType(String dockerImage) {
		this.dockerImage = dockerImage;
	}
	public static DockerType findImage(String name) {
		for(DockerType type : values()) {
			if(type.getDockerImage().equals(name))
				return type;
		}
		return null;
	}
}
