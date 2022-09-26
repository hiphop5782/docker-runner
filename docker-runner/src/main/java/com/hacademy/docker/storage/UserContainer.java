package com.hacademy.docker.storage;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserContainer {
	private String key;
	@Builder.Default
	private List<String> containers = new ArrayList<>();
	public void add(String containerId) {
		containers.add(containerId);
	}
}
