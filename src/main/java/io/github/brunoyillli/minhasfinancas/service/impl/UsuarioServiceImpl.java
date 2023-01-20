package io.github.brunoyillli.minhasfinancas.service.impl;

import org.springframework.stereotype.Service;

import io.github.brunoyillli.minhasfinancas.entity.Usuario;
import io.github.brunoyillli.minhasfinancas.exception.RegraNegocioException;
import io.github.brunoyillli.minhasfinancas.repository.UsuarioRepository;
import io.github.brunoyillli.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	private UsuarioRepository repository;
	
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Usuario salvarUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if(existe) {
			throw new RegraNegocioException("Já existe um usuario cadastrado com este e-mail");
		}
		
	}

}
