package com.hacademy.docker;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.command.ExecStartResultCallback;

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
					.withCmd("ttyd", "-p", "10011", "/bin/sh")
					.withExposedPorts(ExposedPort.tcp(10011))
					.withPortBindings(ports).exec();

			client.startContainerCmd(createResponse.getId()).exec();
			
			File target = new File(System.getProperty("user.home"), "Hello.java");
			client.copyArchiveToContainerCmd(createResponse.getId())
												.withHostResource(target.getAbsolutePath())
												.withRemotePath("/")
											.exec();

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
