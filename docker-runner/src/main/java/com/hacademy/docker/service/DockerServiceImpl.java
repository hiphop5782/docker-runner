package com.hacademy.docker.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.hacademy.docker.component.PortManager;
import com.hacademy.docker.configuration.DockerConfigurationProperty;
import com.hacademy.docker.constant.DockerType;
import com.hacademy.docker.error.UnsupportedContainerException;
import com.hacademy.docker.vo.SourceCodeVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DockerServiceImpl implements DockerService{
	
	@Autowired
	private DockerConfigurationProperty dockerProps;

	@Autowired
	private PortManager portManager;
	
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
		DockerType dockerType = DockerType.findImage("hiphop5782/jdk:"+javaVersion);
		if(dockerType == null) throw new UnsupportedContainerException("지원하지 않는 컨테이너");
		
		int port = portManager.create();
		log.info("port selected = {}", port);
		
		String containerId = createContainer(dockerType, port);
		log.info("container created = {}", containerId);
		
		//start container (if crash generate next port and retry)
		while(true) {
			try {
				startContainer(containerId);
				break;
			}
			catch(Exception e) {
				port = portManager.create();
			}
		}
		
		if(vo.hasCode()) {
			File source = createSourceFile(vo.getCode());
			
			copySourceFileToContainer(containerId, source);
			
			//delete file
			deleteSourceFile(source);
			
			//compile in container source code file
			//compileSourceFile(containerId, source);
		}
		
		return dockerProps.getHttpHost()+":"+port;
	}
	
	@Override
	public void clear() {
		List<Container> containers = client.listContainersCmd().exec();
		for(Container container : containers) {
			client.killContainerCmd(container.getId());
		}
	}
	
	private String createContainer(DockerType dockerType, int port) {
		Ports ports = new Ports();
		ports.bind(ExposedPort.tcp(port), Ports.Binding.bindPort(port));
		
		return client.createContainerCmd(dockerType.getDockerImage())
			.withCmd("ttyd", "-p", String.valueOf(port), "/bin/sh")
			.withExposedPorts(ExposedPort.tcp(port))
			.withPortBindings(ports)
			.exec().getId();
	}
	
	private void startContainer(String containerId) {
		client.startContainerCmd(containerId).exec();
	}
	
	private String findClassName(String code) {
		Matcher matcher = Pattern.compile("public\\s+class\\s+(\\w+)\\s*\\{").matcher(code);
		matcher.find();
		return matcher.group(1);
	}
	
	private File createSourceFile(String code) throws UnsupportedEncodingException, FileNotFoundException {
		log.info("code found\n{}", code);
		
		//find classname
		String className = findClassName(code);
		log.info("class name = {}", className);
		
		//create source file
		File target = new File(home, className+".java");
		try(PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(target), "UTF-8"));) {
			writer.write(code);
		}
		
		return target;
	}
	
	private void copySourceFileToContainer(String containerId, File source) {
		//copy to container
		client.copyArchiveToContainerCmd(containerId)
			.withHostResource(source.getAbsolutePath())
			.withRemotePath("/")
		.exec();
		log.info("file copy finish");
	}
	
	private void deleteSourceFile(File source) {
		source.delete();
	}
	
//	private void compileSourceFile(String containerId, File source) {
//		ExecCreateCmdResponse execResponse = client.execCreateCmd(response.getId()).withAttachStdout(true)
//						.withAttachStderr(true).withCmd("javac", className+".java").withContainerId(response.getId())
//						.exec();
//				client.execStartCmd(execResponse.getId())
//						.exec(new ExecStartResultCallback())
//						.awaitStarted().awaitCompletion();
//				log.info("compile finish");
//	}
	
}
