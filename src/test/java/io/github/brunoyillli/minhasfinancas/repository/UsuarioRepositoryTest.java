package io.github.brunoyillli.minhasfinancas.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.brunoyillli.minhasfinancas.entity.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;

	public Usuario getUsuarioValido() {
		return Usuario.builder().id(1l).nome("fulano").email("usuario@gmail.com").senha("teste").build();
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
		boolean result = repository.existsByEmail("teste@email.com");
		Assertions.assertThat(result).isFalse();
	}

	@Test
	@DisplayName("Deve salvar um usuario com sucesso")
	public void saveUserSucess() {
		Usuario usuario = getUsuarioValido();
		Usuario usuarioSalvo = repository.save(usuario);
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}

	@Test
	@DisplayName("Deve buscar um usuario por email com sucesso")
	public void findUserByEmailSucess() {
		Usuario usuario = getUsuarioValido();
		repository.save(usuario);
		Optional<Usuario> result = repository.findByEmail(usuario.getEmail());
		Assertions.assertThat(result.isPresent()).isTrue();
	}

	@Test
	@DisplayName("Deve buscar um usuario e retornar null")
	public void findUserByEmailNull() {
		Optional<Usuario> result = repository.findByEmail("teste@email.com");
		Assertions.assertThat(result.isPresent()).isFalse();
	}
}
