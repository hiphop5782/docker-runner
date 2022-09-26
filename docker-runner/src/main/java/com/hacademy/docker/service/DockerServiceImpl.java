package com.hacademy.docker.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.hacademy.docker.configuration.DockerConfigurationProperty;
import com.hacademy.docker.constant.DockerType;
import com.hacademy.docker.error.UnsupportedContainerException;
import com.hacademy.docker.storage.UserContainer;
import com.hacademy.docker.vo.SourceCodeVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DockerServiceImpl implements DockerService{
	
	private Map<String, UserContainer> runningContainers = Collections.synchronizedMap(new HashMap<>());
	
	@Autowired
	private DockerConfigurationProperty property;

	@Autowired
	private DockerClient client;
	
	private File home = new File(System.getProperty("user.home"), "docker-java");
	
	@PostConstruct
	public void prepare() {
		home.mkdirs();
	}
	
	@Override
	public String start(String remoteAddress, int javaVersion) throws UnsupportedEncodingException, FileNotFoundException, InterruptedException {
		return start(remoteAddress, javaVersion, null);
	}
	
	@Override
	public String start(String remoteAddress, int javaVersion, SourceCodeVO vo) throws UnsupportedEncodingException, FileNotFoundException, InterruptedException {
		Ports ports = new Ports();
		ports.bind(ExposedPort.tcp(10011), Ports.Binding.bindPort(10011));
		
		DockerType dockerType = DockerType.findImage("hiphop5782/jdk:"+javaVersion);
		if(dockerType == null) throw new UnsupportedContainerException("지원하지 않는 컨테이너");
		
		UserContainer userContainer = runningContainers.containsKey(remoteAddress) ? 
				runningContainers.get(remoteAddress) : UserContainer.builder().key(remoteAddress).build();
		
		CreateContainerResponse response = client.createContainerCmd(dockerType.getDockerImage())
														.withCmd("ttyd", "-o", "-p", dockerType.getPortString(), "/bin/sh")
														.withPortBindings(ports)
														.exec();
		String containerId = response.getId();
		log.info("container created = {}", containerId);
		try {
			client.startContainerCmd(containerId).exec();
		}
		catch(Exception e) {}
		
		if(vo.hasCode()) {
			String code = vo.getCode();
			log.info("code found\n{}", code);
			
			//find classname
			Matcher matcher = Pattern.compile("public\\s+class\\s+(\\w+)\\s*\\{").matcher(code);
			matcher.find();
			String className = matcher.group(1);
			log.info("class name = {}", className);
			
			//create source file
			File target = new File(home, className+".java");
			try(PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(target), "UTF-8"));) {
				writer.write(code);
			}
			log.info("file generated = {}", target.getAbsolutePath());
			
			//copy to container
			client.copyArchiveToContainerCmd(response.getId())
				.withHostResource(target.getAbsolutePath())
				.withRemotePath("/")
			.exec();
			log.info("file copy finish");
			
			//delete file
			target.delete();
			
			//compile in container source code file
			/*
			ExecCreateCmdResponse execResponse = client.execCreateCmd(response.getId()).withAttachStdout(true)
					.withAttachStderr(true).withCmd("javac", className+".java").withContainerId(response.getId())
					.exec();
			client.execStartCmd(execResponse.getId())
					.exec(new ExecStartResultCallback())
					.awaitStarted().awaitCompletion();
			log.info("compile finish");
			*/
		}
		
		userContainer.add(containerId);
		runningContainers.put(remoteAddress, userContainer);
		
		return property.getHttpHost()+":"+dockerType.getPort();
	}
	
	@Override
	public void clear() {
		List<Container> containers = client.listContainersCmd().exec();
		for(Container container : containers) {
			client.killContainerCmd(container.getId());
		}
	}
	
}
