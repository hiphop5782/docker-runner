package com.hacademy.docker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hacademy.docker.service.DockerService;

@SpringBootTest
class DockerRunnerApplicationTests {

	@Autowired
	private DockerService service;
	
	@Test
	void contextLoads() {
		service.clear();
	}

}
