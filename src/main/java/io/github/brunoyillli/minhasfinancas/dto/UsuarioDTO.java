package io.github.brunoyillli.minhasfinancas.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UsuarioDTO {
	
	private String email;
	private String nome;
	private String senha;
}
