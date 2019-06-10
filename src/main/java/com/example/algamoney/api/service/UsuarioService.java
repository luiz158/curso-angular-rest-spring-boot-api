package com.example.algamoney.api.service;

import com.example.algamoney.api.model.Permissao;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.lang.reflect.Array;
import java.util.*;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario salvar(Usuario usuario) {
        usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));

        Permissao p2 = new Permissao();
        p2.setCodigo(2L);
        p2.setDescricao("ROLE_PESQUISAR_CATEGORIA");

        Permissao p5 = new Permissao();
        p5.setCodigo(5L);
        p5.setDescricao("ROLE_PESQUISAR_PESSOA");

        Permissao p8 = new Permissao();
        p8.setCodigo(8L);
        p8.setDescricao("ROLE_PESQUISAR_LANCAMENTO");

        List<Permissao> perm = new ArrayList<>();
        perm.add(p2);
        perm.add(p5);
        perm.add(p8);
        usuario.setPermissoes(perm);

        return usuarioRepository.save(usuario);
    }

    public Usuario alterarSenha(String email, String senhaAntiga, String novaSenha) {
        Optional<Usuario> user = usuarioRepository.findByEmail(email);

        if (!user.isPresent()) {
            throw new EmptyResultDataAccessException(1);
        }

        Usuario usuarioSalvo = user.get();

        if (!(new BCryptPasswordEncoder().matches(senhaAntiga, usuarioSalvo.getSenha()))) {
            throw new EmptyResultDataAccessException(1);
        }

        usuarioSalvo.setSenha(new BCryptPasswordEncoder().encode(novaSenha));
        usuarioRepository.save(usuarioSalvo);

        return usuarioSalvo;
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
