package com.hacademy.docker.component;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.hacademy.docker.configuration.PortProperty;

@Component
public class PortManager {
	
	@Autowired
	private PortProperty portProps;
	
	private int start;
	
	@Autowired
	private DockerClient client;
	
	@PostConstruct
	public void prepare() {
		start = portProps.getBegin();
	}
	
	public int create() {
		int copy = start;
		start += 1;
		start -= portProps.getBegin();
		start %= portProps.range();
		start += portProps.getBegin();
		for(Container c : list()) {
			System.out.println(c.ports);
		}
		return copy;
	}
	
	public List<Container> list() {
		return client.listContainersCmd().exec();
	}
	
}
