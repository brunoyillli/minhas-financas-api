package io.github.brunoyillli.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.brunoyillli.minhasfinancas.entity.Usuario;
import io.github.brunoyillli.minhasfinancas.exception.ErroAutenticacaoException;
import io.github.brunoyillli.minhasfinancas.exception.RegraNegocioException;
import io.github.brunoyillli.minhasfinancas.repository.UsuarioRepository;
import io.github.brunoyillli.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository repository;
	private PasswordEncoder encoder;

	public UsuarioServiceImpl(UsuarioRepository repository, PasswordEncoder encoder) {
		super();
		this.repository = repository;
		this.encoder = encoder;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		if (!usuario.isPresent()) {
			throw new ErroAutenticacaoException("Usuario não encontrado para o e-mail informado.");
		}
		
		Boolean senhasBatem = encoder.matches(senha, usuario.get().getSenha());
		
		if (!senhasBatem) {
			throw new ErroAutenticacaoException("Senha invalida.");
		}
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(criptografarSenha(usuario));
	}

	private Usuario criptografarSenha(Usuario usuario) {
		String senhaCripto = encoder.encode(usuario.getSenha());
		usuario.setSenha(senhaCripto);
		return usuario;
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if (existe) {
			throw new RegraNegocioException("Já existe um usuario cadastrado com este e-mail");
		}

	}

	@Override
	public Usuario findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new RegraNegocioException("Usuario nao encontrado para o ID informado"));
	}

}
