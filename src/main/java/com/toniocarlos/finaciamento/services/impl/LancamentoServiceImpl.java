package com.toniocarlos.finaciamento.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toniocarlos.finaciamento.exception.RegraNegocioException;
import com.toniocarlos.finaciamento.model.entity.Lancamento;
import com.toniocarlos.finaciamento.model.enums.StatusLancamento;
import com.toniocarlos.finaciamento.model.repository.LancamentoRepository;
import com.toniocarlos.finaciamento.services.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {

	
	private LancamentoRepository lancamentoRepository;
	
	public LancamentoServiceImpl(LancamentoRepository lancamentoRepository) {
		this.lancamentoRepository = lancamentoRepository;
	}
	
	
	
	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return lancamentoRepository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		validar(lancamento);
		return lancamentoRepository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		lancamentoRepository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		Example example = Example.of(lancamentoFiltro,ExampleMatcher
				.matching()
				.withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING));
		
		return lancamentoRepository.findAll(example);
		
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
	lancamento.setStatus(status);
	atualizar(lancamento);
	}



	@Override
	public void validar(Lancamento lancamento) {
		
		if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			
			throw new RegraNegocioException("Informe uma Descrição válida!");
		}
		
		if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			
			throw new RegraNegocioException("Informe um Mês válido!");
		}
		
		if(lancamento.getAno() == null || lancamento.toString().length() != 4) {
			
			throw new RegraNegocioException("Informe um Ano válido!");
		}
		
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			
			throw new RegraNegocioException("Usuário Inexistente!");
		}
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			
			throw new RegraNegocioException("Informe um Valor válido!");
		}
		
		if(lancamento.getTipo() == null) {
			
			throw new RegraNegocioException("Informe um Tipo de Lançamento!");
		}
	}
	
}
