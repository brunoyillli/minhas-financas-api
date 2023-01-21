package io.github.brunoyillli.minhasfinancas.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.brunoyillli.minhasfinancas.entity.Usuario;
import io.github.brunoyillli.minhasfinancas.exception.ErroAutenticacaoException;
import io.github.brunoyillli.minhasfinancas.exception.RegraNegocioException;
import io.github.brunoyillli.minhasfinancas.repository.UsuarioRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioService service;

	@MockBean
	UsuarioRepository repository;

	public Usuario getUsuarioValido() {
		return Usuario.builder().id(1l).nome("fulano").email("usuario@gmail.com").senha("teste").build();
	}

	@Test
	@DisplayName("Deve validar que o email não está sendo usado com sucesso e-mail com sucesso")
	public void validEmailSucess() {
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		service.validarEmail("teste@gmail.com");
	}

	@Test
	@DisplayName("Deve lançar exceção ao validar que o e-mail já está cadastrado")
	public void validExistEmailThrowException() {
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		Assertions.assertThrows(RegraNegocioException.class, () -> service.validarEmail("teste@mail.com.br"));
	}

	@Test
	@DisplayName("Deve autenticar um usuario com sucesso")
	public void authenticateUserSucess() {
		Usuario usuario = getUsuarioValido();
		Mockito.when(repository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

		Usuario result = service.autenticar(usuario.getEmail(), usuario.getSenha());

		org.assertj.core.api.Assertions.assertThat(result).isNotNull();
	}

	@Test
	@DisplayName("Deve lançar exceção ao não encontrar usuario com email informado")
	public void authenticateUserNotFoundByEmailThrowException() {
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		Assertions.assertThrows(ErroAutenticacaoException.class,
				() -> service.autenticar("email@email.com.br", "senha"));
	}

	@Test
	@DisplayName("Deve lançar exceção ao digitar senha invalida")
	public void authenticateUserInvalidPasswordThrowException() {
		Usuario usuario = getUsuarioValido();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		Assertions.assertThrows(ErroAutenticacaoException.class,
				() -> service.autenticar("email@email.com.br", "senhainvalida"));
	}

	@Test
	@DisplayName("Deve salvar um usuario com sucesso")
	public void saveUserSucess() {
		Usuario usuario = getUsuarioValido();
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		Usuario usuarioSalvo = service.salvarUsuario(usuario);
		org.assertj.core.api.Assertions.assertThat(usuarioSalvo).isNotNull();
	}

	@Test
	@DisplayName("Deve lancar excecao ao salvar um usuario com email ja cadastrado")
	public void saveUserEmailNotValidThrowException() {
		Usuario usuario = getUsuarioValido();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(usuario.getEmail());
		Assertions.assertThrows(RegraNegocioException.class, () -> service.salvarUsuario(usuario));
	}
}
