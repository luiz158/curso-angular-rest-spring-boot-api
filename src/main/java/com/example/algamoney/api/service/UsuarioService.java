package com.example.algamoney.api.service;

import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
        Usuario usuarioSalvo = buscarUsuarioPeloCodigo(codigo);
        usuarioSalvo.setAtivo(ativo ? 1 : 0);
        usuarioRepository.save(usuarioSalvo);
    }

    public Usuario buscarUsuarioPeloCodigo(Long codigo) {
        Optional<Usuario> usuarioSalvo = usuarioRepository.findById(codigo);
        if (!usuarioSalvo.isPresent()) {
            throw new EmptyResultDataAccessException(1);
        }
        return usuarioSalvo.get();
    }

}
