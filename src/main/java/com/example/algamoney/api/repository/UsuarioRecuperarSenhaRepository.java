package com.example.algamoney.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.algamoney.api.model.UsuarioRecuperarSenha;

public interface UsuarioRecuperarSenhaRepository extends JpaRepository<UsuarioRecuperarSenha, Long> {

	public Optional<UsuarioRecuperarSenha> findByToken(String token);
}
