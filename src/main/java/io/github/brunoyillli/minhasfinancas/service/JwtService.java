package io.github.brunoyillli.minhasfinancas.service;

import io.github.brunoyillli.minhasfinancas.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

public interface JwtService {

	String gerarToken(Usuario usuario);

	Claims obterClaims(String token) throws ExpiredJwtException;

	boolean isTokenValido(String token);

	String obterLoginUsuario(String token);
}
