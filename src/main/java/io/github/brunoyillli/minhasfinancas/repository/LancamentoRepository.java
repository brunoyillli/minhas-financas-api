package io.github.brunoyillli.minhasfinancas.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.brunoyillli.minhasfinancas.entity.Lancamento;
import io.github.brunoyillli.minhasfinancas.entity.enums.StatusLancamento;
import io.github.brunoyillli.minhasfinancas.entity.enums.TipoLancamento;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

	@Query(value = "select sum(l.valor) from Lancamento l join l.usuario u "
			+ "where u.id =:idUsuario and l.tipo =:tipo and l.status = :status group by u")
	BigDecimal obterSaldoPorTipoLancamentoUsuarioEStatus(@Param("idUsuario") Long idUsuario, 
			@Param("tipo") TipoLancamento tipo, @Param("status") StatusLancamento status);

}
