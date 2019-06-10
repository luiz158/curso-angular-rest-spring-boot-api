package com.example.algamoney.api.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.mail.Mailer;
import com.example.algamoney.api.model.Permissao;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.model.UsuarioRecuperarSenha;
import com.example.algamoney.api.repository.UsuarioRecuperarSenhaRepository;
import com.example.algamoney.api.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private UsuarioRecuperarSenhaRepository usuarioRecuperarSenhaRepository;
	@Autowired
	private Mailer mailer;

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

	public void enviarToken(String email) {
		try {

			Optional<Usuario> user = usuarioRepository.findByEmail(email);

			if (!user.isPresent()) {
				throw new EmptyResultDataAccessException(1);
			}

			Usuario usuario = user.get();

			// Criando token: ID + TIMESTAMP
			String text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ssmmHHddMMyyyy"))
					+ usuario.getCodigo().toString() + "MessiMaiorQueCR7";

			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
			String hash = bytesToHex(hashBytes);

			UsuarioRecuperarSenha urs = new UsuarioRecuperarSenha();
			urs.setCodigoUsuario(usuario.getCodigo());
			urs.setToken(hash);

			usuarioRecuperarSenhaRepository.save(urs);

			System.out.println(hash);

			mailer.enviarEmail("ALGAMONEY-API", Arrays.asList(usuario.getEmail()), "Recuperação de Senha", hash);

		} catch (NoSuchAlgorithmException e) {
			// Erro que não irá ocorrer mas tenho que tratar
			e.printStackTrace();
		}
	}

	public void modificarSenhaToken(String token, String novaSenha) {

		Optional<UsuarioRecuperarSenha> byToken = usuarioRecuperarSenhaRepository.findByToken(token);

		if (!byToken.isPresent()) {
			throw new EmptyResultDataAccessException(1);
		}

		Usuario byId = usuarioRepository.findById(byToken.get().getCodigoUsuario()).get();
		byId.setSenha(new BCryptPasswordEncoder().encode(novaSenha));
		usuarioRepository.save(byId);
	}

	// Converte um array de bytes em String Hexadecimal
	private String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}
}
