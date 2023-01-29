package io.github.brunoyillli.minhasfinancas.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import io.github.brunoyillli.minhasfinancas.entity.Lancamento;
import io.github.brunoyillli.minhasfinancas.entity.Usuario;
import io.github.brunoyillli.minhasfinancas.entity.enums.StatusLancamento;
import io.github.brunoyillli.minhasfinancas.entity.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository lancamentoRepository;

	@Autowired
	UsuarioRepository usuarioRepository;

	public Usuario getUsuario() {
		return Usuario.builder().nome("fulano").email("usuario@gmail.com").senha("teste").build();
	}

	public Lancamento getLancamento() {
		return Lancamento.builder().ano(2023).mes(1).descricao("lancamento qualquer").valor(BigDecimal.valueOf(1000))
				.tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE).dataCadastro(LocalDate.now())
				.usuario(getUsuario()).build();

	}

	@Test
	@DisplayName("Deve salvar um lancamento")
	public void saveLancamento() {
		usuarioRepository.save(getUsuario());
		Lancamento lancamento = lancamentoRepository.save(getLancamento());
		Assertions.assertThat(lancamento.getId()).isNotNull();
	}

	@Test
	@DisplayName("Deve deletar um lancamento")
	public void deleteLancamento() {
		usuarioRepository.save(getUsuario());
		Lancamento lancamento = lancamentoRepository.save(getLancamento());
		lancamentoRepository.deleteById(lancamento.getId());
		Optional<Lancamento> result = lancamentoRepository.findById(lancamento.getId());
		org.junit.jupiter.api.Assertions.assertFalse(result.isPresent());
	}

	@Test
	@DisplayName("Deve atualizar um lancamento")
	public void atualizarLancamento() {
		usuarioRepository.save(getUsuario());
		Lancamento lancamento = lancamentoRepository.save(getLancamento());
		lancamento.setAno(2022);
		lancamento.setDescricao("lancamento atualizado");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		Lancamento lancamentoAtualizado = lancamentoRepository.save(lancamento);
		Assertions.assertThat(lancamentoAtualizado.getAno()).isEqualTo(2022);
		Assertions.assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("lancamento atualizado");
		Assertions.assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
	}

	@Test
	@DisplayName("Deve buscar um lancamento por ID")
	public void findLancamentoByID() {
		usuarioRepository.save(getUsuario());
		Lancamento lancamento = lancamentoRepository.save(getLancamento());
		Optional<Lancamento> lancamentoEncontrado = lancamentoRepository.findById(lancamento.getId());
		Assertions.assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}
}
