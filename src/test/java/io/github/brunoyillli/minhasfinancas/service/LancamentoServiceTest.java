package io.github.brunoyillli.minhasfinancas.service;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.brunoyillli.minhasfinancas.entity.Lancamento;
import io.github.brunoyillli.minhasfinancas.entity.Usuario;
import io.github.brunoyillli.minhasfinancas.entity.enums.StatusLancamento;
import io.github.brunoyillli.minhasfinancas.entity.enums.TipoLancamento;
import io.github.brunoyillli.minhasfinancas.exception.RegraNegocioException;
import io.github.brunoyillli.minhasfinancas.repository.LancamentoRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoService lancamentoService;

	@MockBean
	LancamentoRepository lancamentoRepository;

	public Usuario getUsuario() {
		return Usuario.builder().id(1L).nome("fulano").email("usuario@gmail.com").senha("teste").build();
	}

	public Lancamento getLancamento() {
		return Lancamento.builder().ano(2023).mes(1).descricao("lancamento qualquer").valor(BigDecimal.valueOf(1000))
				.tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE).dataCadastro(LocalDate.now())
				.usuario(getUsuario()).build();
	}

	@Test
	@DisplayName("Deve salvar um lancamento")
	public void saveValidLancamento() {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		Mockito.when(lancamentoRepository.save(lancamento)).thenReturn(lancamento);
		Lancamento lancamentoSalvo = lancamentoService.salvar(lancamento);
		Assertions.assertThat(lancamentoSalvo.getId()).isEqualTo(lancamento.getId());
		Mockito.verify(lancamentoRepository, Mockito.times(1)).save(lancamento);

	}

	@Test
	@DisplayName("Deve ocorrer exceção quando tentar salvar um lancamento invalido")
	public void saveLancamentoThrow() {
		Lancamento lancamento = getLancamento();
		lancamento.setDescricao(null);
		org.junit.jupiter.api.Assertions.assertThrows(
				RegraNegocioException.class, () -> lancamentoService.salvar(lancamento));
		lancamento.setDescricao("teste");
		lancamento.setMes(null);
		org.junit.jupiter.api.Assertions.assertThrows(
				RegraNegocioException.class, () -> lancamentoService.salvar(lancamento));
		lancamento.setMes(2);
		lancamento.setAno(null);
		org.junit.jupiter.api.Assertions.assertThrows(
				RegraNegocioException.class, () -> lancamentoService.salvar(lancamento));
		lancamento.setAno(2023);
		lancamento.setUsuario(null);
		org.junit.jupiter.api.Assertions.assertThrows(
				RegraNegocioException.class, () -> lancamentoService.salvar(lancamento));
		lancamento.setUsuario(getUsuario());
		lancamento.setValor(null);
		org.junit.jupiter.api.Assertions.assertThrows(
				RegraNegocioException.class, () -> lancamentoService.salvar(lancamento));
		lancamento.setValor(BigDecimal.valueOf(1000));
		lancamento.setTipo(null);
		org.junit.jupiter.api.Assertions.assertThrows(
				RegraNegocioException.class, () -> lancamentoService.salvar(lancamento));
		Mockito.verify(lancamentoRepository,Mockito.never()).save(lancamento);
	}
	
	@Test
	@DisplayName("Deve atualizar um lancamento com sucesso")
	public void atualizarLancamento() {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		Mockito.when(lancamentoRepository.findById(lancamento.getId())).thenReturn(Optional.of(lancamento));
		Mockito.when(lancamentoRepository.save(lancamento)).thenReturn(lancamento);
		Lancamento Lancamentoatualizado = lancamentoService.atualizar(lancamento);
		Assertions.assertThat(Lancamentoatualizado.getId()).isEqualTo(lancamento.getId());
		Mockito.verify(lancamentoRepository, Mockito.times(1)).save(lancamento);
	}
	
	@Test
	@DisplayName("Deve lancar uma excecao ao lancamento não encontrado")
	public void atualizarLancamentoThrowException() {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		Mockito.when(lancamentoRepository.findById(lancamento.getId())).thenReturn(Optional.empty());
		org.junit.jupiter.api.Assertions.assertThrows(
				RegraNegocioException.class, () -> lancamentoService.atualizar(lancamento));
		Mockito.verify(lancamentoRepository,Mockito.never()).save(lancamento);
	}
	
	@Test
	@DisplayName("Deve deletar um lancamento com sucesso")
	public void deleteLancamento() {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		Mockito.when(lancamentoRepository.findById(lancamento.getId())).thenReturn(Optional.of(lancamento));
		lancamentoService.deletar(lancamento.getId());
		Mockito.verify(lancamentoRepository, Mockito.times(1)).delete(lancamento);
	}
	
	@Test
	@DisplayName("Deve lancar um excecao ao deletar um lancamento")
	public void deleteLancamentoThrowException() {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		Mockito.when(lancamentoRepository.findById(lancamento.getId())).thenReturn(Optional.empty());
		org.junit.jupiter.api.Assertions.assertThrows(
				RegraNegocioException.class, () -> lancamentoService.deletar(lancamento.getId()));
		
		Mockito.verify(lancamentoRepository, Mockito.never()).delete(lancamento);	
	}
	
	@Test
	@DisplayName("Deve filtrar lancamentos com sucesso")
	public void filtrarLancamentoSucess() {
		Lancamento lancamento = getLancamento();
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(lancamentoRepository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		List<Lancamento> resultado = lancamentoService.buscar(lancamento);
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}
	
	@Test
	@DisplayName("Deve atualizar o status de um lancamento")
	public void AtualizarStatusLancamentoSucess() {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1l);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		
		Mockito.doReturn(lancamento).when(lancamentoService).atualizar(lancamento);
		lancamentoService.atualizarStatus(lancamento, novoStatus);
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(lancamentoService).atualizar(lancamento);
	}
	
	@Test
	@DisplayName("Deve buscar um lancamento por id com sucesso")
	public void findByIdLancamentoSucess() {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		Mockito.when(lancamentoRepository.findById(lancamento.getId())).thenReturn(Optional.of(lancamento));
		Lancamento resultado = lancamentoService.findById(lancamento.getId());
		Assertions.assertThat(resultado).isNotNull();
	}
	
	@Test
	@DisplayName("Deve lancar uma exceccao ao buscar um lancamento por id nao existente")
	public void findByIdLancamentoThrowException() {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		Mockito.when(lancamentoRepository.findById(lancamento.getId())).thenReturn(Optional.empty());
		RegraNegocioException exception = org.junit.jupiter.api.Assertions.assertThrows(
				RegraNegocioException.class, () -> lancamentoService.findById(lancamento.getId()));
		org.junit.jupiter.api.Assertions.assertEquals("Lancamento não encontrado", exception.getMessage());
	}
	
	@Test
	@DisplayName("Deve buscar o saldo do usuario com sucesso")
	public void findSaldoUsuarioSucess() {
		Mockito.when(lancamentoRepository.obterSaldoPorTipoLancamentoUsuario(
				1L, TipoLancamento.RECEITA)).thenReturn(BigDecimal.valueOf(5000));
		Mockito.when(lancamentoRepository.obterSaldoPorTipoLancamentoUsuario(
				1L, TipoLancamento.DESPESA)).thenReturn(BigDecimal.valueOf(2000));
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(1L);
		Assertions.assertThat(saldo).isEqualTo(BigDecimal.valueOf(3000));
	}
	
	@Test
	@DisplayName("Deve buscar o saldo zerado do usuario com sucesso")
	public void findSaldoZeradoUsuarioSucess() {
		Mockito.when(lancamentoRepository.obterSaldoPorTipoLancamentoUsuario(
				1L, TipoLancamento.RECEITA)).thenReturn(null);
		Mockito.when(lancamentoRepository.obterSaldoPorTipoLancamentoUsuario(
				1L, TipoLancamento.DESPESA)).thenReturn(null);
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(1L);
		Assertions.assertThat(saldo).isEqualTo(BigDecimal.ZERO);
	}
	
	

}
