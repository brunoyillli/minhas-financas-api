package io.github.brunoyillli.minhasfinancas.service;

import io.github.brunoyillli.minhasfinancas.entity.Usuario;

public interface UsuarioService {

	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
}
