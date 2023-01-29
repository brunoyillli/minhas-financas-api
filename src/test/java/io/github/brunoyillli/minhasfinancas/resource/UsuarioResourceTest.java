package io.github.brunoyillli.minhasfinancas.resource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.brunoyillli.minhasfinancas.dto.UsuarioDTO;
import io.github.brunoyillli.minhasfinancas.entity.Usuario;
import io.github.brunoyillli.minhasfinancas.exception.ErroAutenticacaoException;
import io.github.brunoyillli.minhasfinancas.exception.RegraNegocioException;
import io.github.brunoyillli.minhasfinancas.service.LancamentoService;
import io.github.brunoyillli.minhasfinancas.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTest {

	static final String API = "/api/usuarios";

	@Autowired
	MockMvc mvc;

	@MockBean
	UsuarioService service;

	@MockBean
	LancamentoService lancamentoService;

	private UsuarioDTO getUsuarioDTO() {
		return UsuarioDTO.builder().email("email@gmail.com").senha("123").nome("teste usuario").build();
	}

	private Usuario getUsuario() {
		return Usuario.builder().id(1l).email("email@gmail.com").senha("123").nome("teste usuario").build();
	}

	@Test
	@DisplayName("deve autenticar um usuario")
	public void autenticarUsuarioSucess() throws Exception {
		UsuarioDTO dto = getUsuarioDTO();
		Usuario usuario = getUsuario();
		Mockito.when(service.autenticar(dto.getEmail(), dto.getSenha())).thenReturn(usuario);
		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar"))
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
				.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}

	@Test
	@DisplayName("deve retonar bad request ao obter erro de autenticação")
	public void autenticarUsuarioBadRequest() throws Exception {
		UsuarioDTO dto = getUsuarioDTO();
		Mockito.when(service.autenticar(dto.getEmail(), dto.getSenha()))
			.thenThrow(ErroAutenticacaoException.class);
		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar"))
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	@DisplayName("deve salvar um usuario com sucesso")
	public void saveUsuarioSucess() throws Exception {
		UsuarioDTO dto = getUsuarioDTO();
		Usuario usuario = getUsuario();
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
				.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}
	
	@Test
	@DisplayName("deve retonar bad request ao obter erro ao salvar um usuario")
	public void saveUsuarioBadRequest() throws Exception {
		UsuarioDTO dto = getUsuarioDTO();
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	@DisplayName("deve retornar o saldo do usuario com sucesso")
	public void saldoUsuarioSucess() throws Exception {
		Usuario usuario = getUsuario();
		BigDecimal saldo = BigDecimal.valueOf(1000);
		Mockito.when(service.findById(Mockito.anyLong())).thenReturn(usuario);
		Mockito.when(lancamentoService.obterSaldoPorUsuario(Mockito.anyLong()))
			.thenReturn(BigDecimal.valueOf(1000));
		String json = new ObjectMapper().writeValueAsString(saldo);

		MockHttpServletRequestBuilder request = 
				MockMvcRequestBuilders.get(API.concat("/"+usuario.getId()+"/saldo"))
				.accept(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(jsonPath("$", equalTo(saldo.intValue())));
	}

}
