package io.github.brunoyillli.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.List;

import io.github.brunoyillli.minhasfinancas.entity.Lancamento;
import io.github.brunoyillli.minhasfinancas.entity.enums.StatusLancamento;

public interface LancamentoService {
	
	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Long id);
	
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	Lancamento findById(Long id);
	
	void validar(Lancamento lancamento);
	
	BigDecimal obterSaldoPorUsuario(Long id);
}
