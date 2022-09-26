package com.hacademy.docker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.AttachContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.AttachContainerResultCallback;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;

@SpringBootTest
class DockerRunnerApplicationTests {

	private DockerClient client;
	
	@BeforeEach
	void before() {
		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost("tcp://192.168.0.27:2375").build();
		client = DockerClientBuilder.getInstance(config).withDockerCmdExecFactory(new NettyDockerCmdExecFactory()).build();
	}

	@Test
	void test() {
		Ports ports = new Ports();
		ports.bind(ExposedPort.tcp(10011), Ports.Binding.bindPort(10011));

		try {
			CreateContainerResponse createResponse = client.createContainerCmd("hiphop5782/jdk:11")
					.withCmd("ttyd", "-p", "10011", "/bin/sh")
					.withExposedPorts(ExposedPort.tcp(10011))
					.withPortBindings(ports)
					.withStdinOpen(true)
					.withWorkingDir("/")
					.exec();

			client.startContainerCmd(createResponse.getId()).exec();
			
			File target = new File(System.getProperty("user.home"), "Hello.java");
			client.copyArchiveToContainerCmd(createResponse.getId())
												.withHostResource(target.getAbsolutePath())
												.withRemotePath("/")
											.exec();
			
			
			ExecCreateCmdResponse execResponse = client.execCreateCmd(createResponse.getId()).withAttachStdout(true)
					.withAttachStderr(true).withCmd("ls").withContainerId(createResponse.getId())
					.exec();
			client.execStartCmd(execResponse.getId())
					.exec(new ExecStartResultCallback(System.out, System.err))
					.awaitStarted().awaitCompletion();
			
			System.out.println("────────────────────────────────────────────");
			
			execResponse = client.execCreateCmd(createResponse.getId()).withAttachStdout(true)
					.withAttachStderr(true).withCmd("cat", "Hello.java").withContainerId(createResponse.getId())
					.exec();
			client.execStartCmd(execResponse.getId()).
					exec(new ExecStartResultCallback(System.out, System.err))
					.awaitStarted().awaitCompletion();
			
			System.out.println("────────────────────────────────────────────");
			
			execResponse = client.execCreateCmd(createResponse.getId()).withAttachStdout(true)
					.withAttachStderr(true).withCmd("javac", "Hello.java").withContainerId(createResponse.getId())
					.exec();
			client.execStartCmd(execResponse.getId())
					.exec(new ExecStartResultCallback(System.out, System.err))
					.awaitStarted().awaitCompletion();
			
			System.out.println("────────────────────────────────────────────");
			
			execResponse = client.execCreateCmd(createResponse.getId()).withAttachStdout(true)
					.withAttachStderr(true)
					.withAttachStdin(true).withCmd("java", "Hello").withContainerId(createResponse.getId())
					.exec();
			client.execStartCmd(execResponse.getId())
					.exec(new ExecStartResultCallback(System.out, System.err))
					.awaitStarted().awaitCompletion();
			
			RemoveContainerCmd removeContainerCmd = client.removeContainerCmd(createResponse.getId()).withRemoveVolumes(true)
					.withForce(true);
			removeContainerCmd.exec();
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
//			remove
//			List<Container> list = client.listContainersCmd().exec();
//			for (Container container : list) {
//				System.out.println(container);
//				client.killContainerCmd(container.getId()).exec();
//			}
		}
	}

}
