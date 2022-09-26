package com.hacademy.docker.error;

public class UnsupportedContainerException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public UnsupportedContainerException() {}
	public UnsupportedContainerException(String msg) { super(msg); }
}
