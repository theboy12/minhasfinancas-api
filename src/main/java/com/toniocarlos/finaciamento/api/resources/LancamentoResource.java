package com.toniocarlos.finaciamento.api.resources;

import java.util.List;
import java.util.Optional;

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

import com.toniocarlos.finaciamento.api.dto.AtualizaStatusDTO;
import com.toniocarlos.finaciamento.api.dto.LancamentoDTO;
import com.toniocarlos.finaciamento.exception.RegraNegocioException;
import com.toniocarlos.finaciamento.model.entity.Lancamento;
import com.toniocarlos.finaciamento.model.entity.Usuario;
import com.toniocarlos.finaciamento.model.enums.StatusLancamento;
import com.toniocarlos.finaciamento.model.enums.TipoLancamento;
import com.toniocarlos.finaciamento.services.LancamentoService;
import com.toniocarlos.finaciamento.services.UsuarioServices;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {

	private final LancamentoService service;
	private final UsuarioServices usuarioService;
	
	/*
	public LancamentoResource(LancamentoService service, UsuarioServices usuarioService) {
		this.service = service;
		this.usuarioService = usuarioService;
	}
	*/
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		
		try {
			Lancamento entidade = converter(dto);
			entidade = service.salvar(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false)Integer ano,
			@RequestParam("usuario") Long idUsuario
			) {
		Lancamento lancamentoFiltro = new Lancamento();
		
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		
		if(!usuario.isPresent()) {
			return ResponseEntity
					.badRequest()
					.body("Não foi possível realizar a consulta. Nenhum usuário encontrado para o ID informado.");		
		} else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return service.obterPorId(id).map(entity ->{
			
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			}catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(()->
		new ResponseEntity("Lancamento não encontrado na Base de Dados!",HttpStatus.BAD_GATEWAY));
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		
		return service.obterPorId(id).map(entity->{
			
			StatusLancamento status = StatusLancamento.valueOf(dto.getStatus());
			if(status == null) {
				return ResponseEntity
						.badRequest()
						.body("Não foi possível atualizar o status do lancamento. Status inválido.");
			}
				try {
					entity.setStatus(status);
					service.atualizar(entity);
					return ResponseEntity.ok(entity);
				} catch (RegraNegocioException e) {
					
					return ResponseEntity.badRequest().body(e.getMessage());
				}
		}).orElseGet(() -> 
		
		new ResponseEntity("Lancamento não encontrado na Base de Dados.",HttpStatus.BAD_REQUEST));
		
		
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return service.obterPorId(id).map((entity ->{
			service.deletar(entity);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		})).orElseGet(() ->new ResponseEntity("Lancamento não encontrado na Base de Dados!",HttpStatus.BAD_GATEWAY));
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService
			.obterPorId(dto.getIdUsuario())
			.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o ID informado!"));
		
		lancamento.setUsuario(usuario);
		
		if(dto.getTipo() != null) 
		{	
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		
		if(dto.getStatus() != null)
		{
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		
		return lancamento;
		
	}
}
