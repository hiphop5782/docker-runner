package com.hacademy.docker.controller;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hacademy.docker.service.DockerService;
import com.hacademy.docker.vo.SourceCodeVO;

@RestController
@RequestMapping("/docker")
public class DockerController {
	
	@Autowired
	private DockerService dockerService;

	@GetMapping("/run/java/{version}")
	public String run(@PathVariable int version, HttpServletRequest request) throws UnsupportedEncodingException, FileNotFoundException, InterruptedException {
		return dockerService.start(request.getRemoteAddr(), version);
	}
	
	@PostMapping("/run/java/{version}")
	public String run(@PathVariable int version, HttpServletRequest request, @RequestBody SourceCodeVO vo) throws UnsupportedEncodingException, FileNotFoundException, InterruptedException {
		return dockerService.start(request.getRemoteAddr(), version, vo);
	}
}
