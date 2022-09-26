package com.hacademy.docker.vo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import lombok.Data;

@Data
public class SourceCodeVO {
	private String code;
	public boolean hasCode() {
		return code != null;
	}
	public String getCodeUtf8() throws UnsupportedEncodingException {
		return URLDecoder.decode(code, StandardCharsets.UTF_8.name());
	}
}
