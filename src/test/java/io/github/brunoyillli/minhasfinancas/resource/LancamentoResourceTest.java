package io.github.brunoyillli.minhasfinancas.resource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.brunoyillli.minhasfinancas.dto.AtualizaStatusDTO;
import io.github.brunoyillli.minhasfinancas.dto.LancamentoDTO;
import io.github.brunoyillli.minhasfinancas.dto.UsuarioDTO;
import io.github.brunoyillli.minhasfinancas.entity.Lancamento;
import io.github.brunoyillli.minhasfinancas.entity.Usuario;
import io.github.brunoyillli.minhasfinancas.entity.enums.StatusLancamento;
import io.github.brunoyillli.minhasfinancas.entity.enums.TipoLancamento;
import io.github.brunoyillli.minhasfinancas.exception.RegraNegocioException;
import io.github.brunoyillli.minhasfinancas.service.LancamentoService;
import io.github.brunoyillli.minhasfinancas.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LancamentoResource.class)
@AutoConfigureMockMvc
public class LancamentoResourceTest {

	static final String API = "/api/lancamentos";
	
	@Autowired
	MockMvc mvc;

	@MockBean
	UsuarioService usuarioservice;

	@MockBean
	LancamentoService lancamentoService;

	private LancamentoDTO getLancamentoDTO() {
		return LancamentoDTO.builder().ano(2023).mes(1).descricao("lancamento qualquer").valor(BigDecimal.valueOf(1000))
				.tipo("RECEITA").status("PENDENTE").usuario(getUsuario().getId()).build();
	}
	
	public Lancamento getLancamento() {
		return Lancamento.builder().id(1l).ano(2023).mes(1).descricao("lancamento qualquer").valor(BigDecimal.valueOf(1000))
				.tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE).dataCadastro(LocalDate.now())
				.usuario(getUsuario()).build();
	}
	
	private Usuario getUsuario() {
		return Usuario.builder().id(1l).email("email@gmail.com").senha("123").nome("teste usuario").build();
	}
	
	@Test
	@DisplayName("deve salvar um lancamento com sucesso")
	public void saveLancamentoSucess() throws Exception {
		LancamentoDTO dto = getLancamentoDTO();
		Lancamento lancamento = getLancamento();
		Mockito.when(lancamentoService.salvar(Mockito.any(Lancamento.class))).thenReturn(lancamento);
		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(lancamento.getId()));
	}
	
	@Test
	@DisplayName("deve dar Bad Request um lancamento")
	public void saveLancamentoBadRequest() throws Exception {
		LancamentoDTO dto = getLancamentoDTO();
		Mockito.when(lancamentoService.salvar(Mockito.any(Lancamento.class))).thenThrow(RegraNegocioException.class);
		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	@DisplayName("deve atualizar um lancamento com sucesso")
	public void atualizarLancamentoSucess() throws Exception {
		LancamentoDTO dto = getLancamentoDTO();
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		Mockito.when(lancamentoService.findById(Mockito.any())).thenReturn(lancamento);
		Mockito.when(lancamentoService.atualizar(Mockito.any(Lancamento.class))).thenReturn(lancamento);
		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API.concat("/1"))
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(lancamento.getId()));
	}
	
	@Test
	@DisplayName("deve dar Bad Request um lancamento")
	public void atualizouLancamentoBadRequest() throws Exception {
		LancamentoDTO dto = getLancamentoDTO();
		Mockito.when(lancamentoService.findById(Mockito.any())).thenThrow(RegraNegocioException.class);
		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API.concat("/1"))
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	@DisplayName("deve retonar o filtro de um lancamento com sucesso")
	public void filtrarLancamentoSucess() throws Exception {
		Lancamento lancamento = getLancamento();
		Usuario usuario = getUsuario();
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(usuarioservice.findById(Mockito.any())).thenReturn(usuario);
		Mockito.when(lancamentoService.buscar(Mockito.any(Lancamento.class))).thenReturn(lista);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API.concat("?usuario=2"))
				.accept(MediaType.APPLICATION_JSON);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	@DisplayName("deve retonar excessao de bad request de filtro de um lancamento")
	public void filtrarLancamentoBadRequest() throws Exception {
		Mockito.when(usuarioservice.findById(Mockito.anyLong())).thenThrow(RegraNegocioException.class);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(API.concat("?usuario=2"))
				.accept(MediaType.APPLICATION_JSON);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	@DisplayName("deve deletar um lancamento com sucesso")
	public void deleteLancamentoSucess() throws Exception {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		Mockito.when(lancamentoService.findById(Mockito.any())).thenReturn(lancamento);
		Mockito.doNothing().when(lancamentoService).deletar(Mockito.anyLong());
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API.concat("/1"))
				.accept(MediaType.APPLICATION_JSON);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());
	}
	
	@Test
	@DisplayName("deve lancar uma excecao ao deletar um lancamento")
	public void deleteLancamentoThrowException() throws Exception {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		Mockito.when(lancamentoService.findById(Mockito.any())).thenThrow(RegraNegocioException.class);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(API.concat("/1"))
				.accept(MediaType.APPLICATION_JSON);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	@DisplayName("deve atualizar um status com sucesso")
	public void atualizarStatusLancamentoSucess() throws Exception {
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		AtualizaStatusDTO dto = new AtualizaStatusDTO(novoStatus.name());
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		lancamento.setStatus(novoStatus);
		Mockito.when(lancamentoService.findById(Mockito.any())).thenReturn(lancamento);
		Mockito.when(lancamentoService.atualizar(Mockito.any(Lancamento.class))).thenReturn(lancamento);
		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API.concat("/1/atualiza-status"))
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(lancamento.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("status").value(novoStatus.name()));
	}
	
	@Test
	@DisplayName("deve lancar uma excecao ao atualizar status do lancamento")
	public void atualizarStatusBadRequestLancamento() throws Exception {
		AtualizaStatusDTO dto = new AtualizaStatusDTO();
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		String json = new ObjectMapper().writeValueAsString(dto);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API.concat("/1/atualiza-status"))
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	@DisplayName("deve lancar uma excecao ao atualizar e nao receber o status do lancamento")
	public void atualizarStatusNullBadRequestLancamento() throws Exception {
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		AtualizaStatusDTO dto = new AtualizaStatusDTO(novoStatus.name());
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		String json = new ObjectMapper().writeValueAsString(dto);
		Mockito.when(lancamentoService.findById(Mockito.any())).thenThrow(RegraNegocioException.class);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(API.concat("/1/atualiza-status"))
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(json);
		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
}
