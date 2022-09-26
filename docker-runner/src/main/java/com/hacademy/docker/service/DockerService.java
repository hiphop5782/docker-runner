package com.hacademy.docker.service;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public interface DockerService {
	String start(String remoteAddress, int javaVersion) throws UnsupportedEncodingException, FileNotFoundException;
	String start(String remoteAddress, int javaVersion, String code) throws UnsupportedEncodingException, FileNotFoundException;
	void clear();
}
