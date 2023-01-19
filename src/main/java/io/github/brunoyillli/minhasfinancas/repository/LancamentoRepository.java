package io.github.brunoyillli.minhasfinancas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.brunoyillli.minhasfinancas.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
