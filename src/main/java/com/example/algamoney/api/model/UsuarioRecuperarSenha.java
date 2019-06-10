package com.example.algamoney.api.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "usuario_recuperar_senha")
public class UsuarioRecuperarSenha {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	private String token;
	private Long codigoUsuario;

	public UsuarioRecuperarSenha() {
		
	}
	
	public UsuarioRecuperarSenha(Long codigo, String token, Long codigoUsuario) {
		super();
		this.codigo = codigo;
		this.token = token;
		this.codigoUsuario = codigoUsuario;
	}

	/* ########################################### */
	/* GETTERS AND SETTERS */
	/* ########################################### */
	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getCodigoUsuario() {
		return codigoUsuario;
	}

	public void setCodigoUsuario(Long codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}
}
