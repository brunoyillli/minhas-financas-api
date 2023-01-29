package io.github.brunoyillli.minhasfinancas.resource;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import io.github.brunoyillli.minhasfinancas.dto.LancamentoDTO;
import io.github.brunoyillli.minhasfinancas.dto.UsuarioDTO;
import io.github.brunoyillli.minhasfinancas.entity.Lancamento;
import io.github.brunoyillli.minhasfinancas.entity.Usuario;
import io.github.brunoyillli.minhasfinancas.entity.enums.StatusLancamento;
import io.github.brunoyillli.minhasfinancas.entity.enums.TipoLancamento;
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
	UsuarioService Usuarioservice;

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
	
}
