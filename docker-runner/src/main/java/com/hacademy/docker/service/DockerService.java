package com.hacademy.docker.service;

import com.hacademy.docker.constant.DockerType;

public interface DockerService {
	String start(DockerType type);
	String create(DockerType type);
	void clear();
}
