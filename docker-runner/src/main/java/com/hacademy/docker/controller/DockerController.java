package com.hacademy.docker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hacademy.docker.constant.DockerType;
import com.hacademy.docker.service.DockerService;

@RestController
@CrossOrigin("*")
@RequestMapping("/docker")
public class DockerController {
	
	@Autowired
	private DockerService dockerService;
	
	@GetMapping("/start/{type:.+}")
	public String start(@PathVariable String type) {
		DockerType dockerType = DockerType.findImage("hiphop5782/"+type);
		return dockerService.start(dockerType);
	}
	
}
