package com.login.authentication.payload.response;

import java.util.List;

public class UserInfoResponse {
	private Long id;
	private String username;
	private String email;
	private List<String> tiposUsuario;

	public UserInfoResponse(Long id, String username, String email, List<String> tiposUsuario) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.tiposUsuario = tiposUsuario;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getTiposUsuario() {
		return tiposUsuario;
	}
}
