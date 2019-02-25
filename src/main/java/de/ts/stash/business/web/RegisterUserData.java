package de.ts.stash.business.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class RegisterUserData {

	private final String username;
	private final String password;

	public byte[] asJson() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this).getBytes();
	}
}
