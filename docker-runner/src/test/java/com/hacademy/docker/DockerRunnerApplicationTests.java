package com.hacademy.docker;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.hacademy.docker.configuration.DockerConfigurationProperty;
import com.hacademy.docker.service.DockerService;

@SpringBootTest
class DockerRunnerApplicationTests {

	@Autowired
	private DockerClient client;

	@Test
	void test() {
		Ports ports = new Ports();
		ports.bind(ExposedPort.tcp(10011), Ports.Binding.bindPort(10011));

		try {
			CreateContainerResponse createResponse = client.createContainerCmd("hiphop5782/jdk:11")
					.withCmd("ttyd", "-p", "10011", "/bin/sh").withExposedPorts(ExposedPort.tcp(10011))
					.withPortBindings(ports).exec();

			client.startContainerCmd(createResponse.getId()).exec();

			ExecCreateCmdResponse execResponse = client.execCreateCmd(createResponse.getId()).withAttachStdout(true)
					.withAttachStderr(true).withCmd("ls").withContainerId(createResponse.getId())
					.exec();
			ExecStartResultCallback callback = client.execStartCmd(execResponse.getId())
					.exec(new ExecStartResultCallback(System.out, System.err));
			System.out.println("<callback>");
			callback.onComplete();
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			//remove
			List<Container> list = client.listContainersCmd().exec();
			for (Container container : list) {
				System.out.println(container);
				client.killContainerCmd(container.getId()).exec();
			}
		}
	}

}
