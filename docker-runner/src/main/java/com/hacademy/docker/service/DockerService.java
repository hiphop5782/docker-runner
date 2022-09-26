package com.hacademy.docker.service;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import com.hacademy.docker.vo.SourceCodeVO;

public interface DockerService {
	String start(String remoteAddress, int javaVersion) throws UnsupportedEncodingException, FileNotFoundException;
	String start(String remoteAddress, int javaVersion, SourceCodeVO vo) throws UnsupportedEncodingException, FileNotFoundException;
	void clear();
}
