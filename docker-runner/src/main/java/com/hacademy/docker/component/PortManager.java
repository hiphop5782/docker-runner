package com.hacademy.docker.component;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hacademy.docker.configuration.PortProperty;

@Component
public class PortManager {
	
	@Autowired
	private PortProperty portProps;
	
	private int start;
	
	@PostConstruct
	public void prepare() {
		start = portProps.getBegin();
	}
	
	public int create() {
		int copy = start;
		start++;
		return copy;
	}
	
}
