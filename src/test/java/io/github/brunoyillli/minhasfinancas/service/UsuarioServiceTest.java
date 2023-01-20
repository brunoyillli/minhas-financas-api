package io.github.brunoyillli.minhasfinancas.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.brunoyillli.minhasfinancas.entity.Usuario;
import io.github.brunoyillli.minhasfinancas.exception.RegraNegocioException;
import io.github.brunoyillli.minhasfinancas.repository.UsuarioRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@Autowired
	UsuarioService service;
	
	@Autowired
	UsuarioRepository repository;
	
	public Usuario getUsuarioValido() {
		return Usuario.builder()
				.id(1l).nome("fulano").email("usuario@gmail.com").senha("teste").build();
	}
	
	@Test
	@DisplayName("Deve validar que o email não está sendo usado com sucesso e-mail com sucesso")
	public void validEmailSucess() {
		repository.deleteAll();
		
		service.validarEmail("teste@gmail.com");
	}
	
	@Test
	@DisplayName("Deve lançar exceção ao validar que o e-mail já está cadastrado")
	public void validExistEmailThrowException() {
		Usuario usuario = getUsuarioValido();
		repository.save(usuario);
		Assertions.assertThrows(RegraNegocioException.class, () -> service.validarEmail(usuario.getEmail()));
	}
}
