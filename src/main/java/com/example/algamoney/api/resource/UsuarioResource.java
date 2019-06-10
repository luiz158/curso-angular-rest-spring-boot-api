package com.example.algamoney.api.resource;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Usuario;
import com.example.algamoney.api.repository.UsuarioRepository;
import com.example.algamoney.api.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ApplicationEventPublisher publisher;

	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_GERENCIAR_USUARIOS') and #oauth2.hasScope('write')")
	public ResponseEntity<Usuario> criar(@Valid @RequestBody Usuario usuario, HttpServletResponse response) {
		Usuario usuarioSalvo = usuarioService.salvar(usuario);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, usuarioSalvo.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
	}

	@PostMapping("/nova-senha")
	public ResponseEntity<Usuario> novaSenha(@RequestBody String jsonStr) {

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = "";

		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}

		JSONObject jsonObj = new JSONObject(jsonStr);
		System.out.println(username);

		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.alterarSenha(username,
				jsonObj.get("antiga").toString(), jsonObj.get("nova").toString()));
	}

	@PostMapping("/recuperar-senha")
	@ResponseStatus(HttpStatus.OK)
	public void recuperarSenha(@Param(value = "email") String email) {

		usuarioService.enviarToken(email);
	}

	@PostMapping("/recuperar-senha-token")
	@ResponseStatus(HttpStatus.OK)
	public void recuperarSenhaToken(@Param(value = "token") String token,
			@Param(value = "senha") String senha) {

		usuarioService.modificarSenhaToken(token, senha);
	}

	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_GERENCIAR_USUARIOS') and #oauth2.hasScope('write')")
	public void atualizarPropriedadeAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		usuarioService.atualizarPropriedadeAtivo(codigo, ativo);
	}

	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_GERENCIAR_USUARIOS')")
	public Page<Usuario> pesquisar(@RequestParam(required = false, defaultValue = "%") String nome, Pageable pageable) {
		return usuarioRepository.findByNomeContaining(nome, pageable);
	}

}
