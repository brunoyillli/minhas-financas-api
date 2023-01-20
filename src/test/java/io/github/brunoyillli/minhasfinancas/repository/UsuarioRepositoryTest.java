package io.github.brunoyillli.minhasfinancas.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.brunoyillli.minhasfinancas.entity.Usuario;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;
	
	public Usuario getUsuarioValido() {
		return Usuario.builder()
				.id(1l).nome("fulano").email("usuario@gmail.com").senha("teste").build();
	}
	
	@Test
	@DisplayName("Deve retornar true quando verificar a existencia de um Email")
	public void existsByEmailTrue() {
		Usuario usuario = getUsuarioValido();
		repository.save(usuario);
		
		boolean result = repository.existsByEmail(usuario.getEmail());
		
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	@DisplayName("Deve retornar falso quando não houver usuario cadastrado com email")
	public void existByEmailFalse() {
		repository.deleteAll();
		boolean result = repository.existsByEmail("teste@email.com");
		Assertions.assertThat(result).isFalse();
	}
}
