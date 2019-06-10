package com.example.algamoney.api.dto;

public class RecuperarSenha {

	private String token;
	private String senha;

	public RecuperarSenha() {
	}

	public RecuperarSenha(String token, String senha) {
		this.token = token;
		this.senha = senha;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
}
