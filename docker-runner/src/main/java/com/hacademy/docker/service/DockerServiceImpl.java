package com.hacademy.docker.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.hacademy.docker.configuration.DockerConfigurationProperty;
import com.hacademy.docker.constant.DockerType;

@Service
public class DockerServiceImpl implements DockerService{
	
	private Map<DockerType, String> runningContainers = Collections.synchronizedMap(new HashMap<>());
	
	@Autowired
	private DockerConfigurationProperty property;

	@Autowired
	private DockerClient client;
	
	@Override
	public String start(DockerType type) {
		if(runningContainers.containsKey(type)) {
			return runningContainers.get(type);
		}
		return create(type);
	}
	
	@Override
	public String create(DockerType type) {
		CreateContainerResponse response = client.createContainerCmd(type.getDockerImage())
																		.withCmd("ttyd", "-p", type.getPortString(), "/bin/sh")
																		.withExposedPorts(ExposedPort.tcp(type.getPort()))
																		.exec();
		String containerId = response.getId();
		client.startContainerCmd(containerId).exec();
		runningContainers.put(type, containerId);
		return property.getHttpHost()+":"+type.getPort();
	}

	@Override
	public void clear() {
		List<Container> containers = client.listContainersCmd().exec();
		for(Container container : containers) {
			client.killContainerCmd(container.getId());
		}
	}
	
}
