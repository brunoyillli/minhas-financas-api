package io.github.brunoyillli.minhasfinancas.resource;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.brunoyillli.minhasfinancas.dto.AtualizaStatusDTO;
import io.github.brunoyillli.minhasfinancas.dto.LancamentoDTO;
import io.github.brunoyillli.minhasfinancas.entity.Lancamento;
import io.github.brunoyillli.minhasfinancas.entity.Usuario;
import io.github.brunoyillli.minhasfinancas.entity.enums.StatusLancamento;
import io.github.brunoyillli.minhasfinancas.entity.enums.TipoLancamento;
import io.github.brunoyillli.minhasfinancas.exception.RegraNegocioException;
import io.github.brunoyillli.minhasfinancas.service.LancamentoService;
import io.github.brunoyillli.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {

	private LancamentoService service;
	private UsuarioService usuarioService;

	public LancamentoResource(LancamentoService service, UsuarioService usuarioService) {
		this.service = service;
		this.usuarioService = usuarioService;
	}

	@PostMapping
	public ResponseEntity<?> salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamento lancamento = converter(dto);
			lancamento = service.salvar(lancamento);
			return new ResponseEntity<Lancamento>(lancamento, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PutMapping("{id}")
	public ResponseEntity<?> atualizar(@PathParam("id") Long id, @RequestBody LancamentoDTO dto) {
		try {
			Lancamento lancamentoEncontrado = service.findById(id);
			Lancamento lancamento = converter(dto);
			lancamento.setId(lancamentoEncontrado.getId());
			service.atualizar(lancamento);
			return ResponseEntity.ok(lancamento);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> buscar(@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam(value = "usuario", required = true) Long idUsuario) {
		try {
			Lancamento lancamentoFiltro = new Lancamento();
			lancamentoFiltro.setDescricao(descricao);
			lancamentoFiltro.setMes(mes);
			lancamentoFiltro.setAno(ano);
			Usuario usuario = usuarioService.findById(idUsuario);
			lancamentoFiltro.setUsuario(usuario);
			List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
			return ResponseEntity.ok(lancamentos);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@DeleteMapping("{id}")
	public ResponseEntity<?> deletar(@PathVariable("id") Long id) {
		try {
			service.findById(id);
			service.deletar(id);
			return ResponseEntity.noContent().build();
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("{id}/atualiza-status")
	public ResponseEntity<?> atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		try {
			if (dto.getStatus() == null) {
				return ResponseEntity.badRequest()
						.body("Não foi possivel atualizar o status do lancamento, envie um status valido");
			}
			Lancamento lancamentoEncontrado = service.findById(id);
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			lancamentoEncontrado.setStatus(statusSelecionado);
			service.atualizar(lancamentoEncontrado);
			return ResponseEntity.ok(lancamentoEncontrado);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("{id}")
	public ResponseEntity<?> obterLancamento(@PathVariable("id") Long id) {
		try {
			return ResponseEntity.ok(converter(service.findById(id)));
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		lancamento.setUsuario(usuarioService.findById(dto.getUsuario()));
		if (dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		if (dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		return lancamento;
	}
	
	
	private LancamentoDTO converter(Lancamento lancamento) {
		return LancamentoDTO.builder()
				.id(lancamento.getId())
				.descricao(lancamento.getDescricao())
				.valor(lancamento.getValor())
				.mes(lancamento.getMes())
				.ano(lancamento.getAno())
				.status(lancamento.getStatus().name())
				.tipo(lancamento.getTipo().name())
				.usuario(lancamento.getUsuario().getId())
				.build();
	}

}
