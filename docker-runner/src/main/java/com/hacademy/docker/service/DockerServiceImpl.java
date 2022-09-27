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
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
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
		return start(remoteAddress, javaVersion, new SourceCodeVO());
	}
	
	@Override
	public String start(String remoteAddress, int javaVersion, SourceCodeVO vo) throws UnsupportedEncodingException, FileNotFoundException, InterruptedException {
		DockerType dockerType = DockerType.findImage("hiphop5782/jdk:"+javaVersion);
		if(dockerType == null) throw new UnsupportedContainerException("지원하지 않는 컨테이너");
		
		int port = portManager.create();
		log.info("port selected = {}", port);
		
		String className = findClassName(vo.getCode());
		log.info("class name = {}", className);
		
		String containerId = createContainer(dockerType, port, className);
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
			File source = createSourceFile(className, vo.getCode());
			
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
	
	private String createContainer(DockerType dockerType, int port, String className) {
		CreateContainerCmd cmd = client.createContainerCmd(dockerType.getDockerImage())
			.withExposedPorts(ExposedPort.tcp(port))
			.withHostConfig(
					HostConfig.newHostConfig()
					.withPortBindings(PortBinding.parse(port+":"+port))
					.withAutoRemove(true));
		
		if(className == null) {//코드없는경우
			cmd.withCmd("ttyd", "-o", "-p", String.valueOf(port), " -t disableResizeOverlay=true -t fontSize=12 -t 'theme={\"background\":\"white\", \"foreground\":\"black\"}'", "/bin/sh");
		}
		else {//코드있는경우
			cmd.withCmd("/bin/sh", "-c", "javac "+className+".java && ttyd -p "+port+" -t disableResizeOverlay=true -t fontSize=12 -t 'theme={\"background\":\"white\", \"foreground\":\"black\"}' -o java -cp . "+className);
		}
		
		return cmd.exec().getId();
	}
	
	private void startContainer(String containerId) {
		client.startContainerCmd(containerId).exec();
	}
	
	private String findClassName(String code) {
		if(code == null) return null;
		
		Matcher matcher = Pattern.compile("public\\s+class\\s+(\\w+)\\s*\\{").matcher(code);
		matcher.find();
		return matcher.group(1);
	}
	
	private File createSourceFile(String className, String code) throws UnsupportedEncodingException, FileNotFoundException {
		log.info("code found\n{}", code);
		
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
