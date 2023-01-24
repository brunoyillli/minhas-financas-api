package io.github.brunoyillli.minhasfinancas.resource;

import javax.websocket.server.PathParam;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.brunoyillli.minhasfinancas.dto.LancamentoDTO;
import io.github.brunoyillli.minhasfinancas.entity.Lancamento;
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
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto ) {
		try {
			Lancamento lancamento = converter(dto);
			lancamento = service.salvar(lancamento);
			return new ResponseEntity<Lancamento>(lancamento, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathParam("id") Long id, @RequestBody LancamentoDTO dto) {
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
	
	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		lancamento.setUsuario(usuarioService.findById(dto.getUsuario()));
		lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		return lancamento;
	}
	
}
