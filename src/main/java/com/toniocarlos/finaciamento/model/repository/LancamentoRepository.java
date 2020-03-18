package com.toniocarlos.finaciamento.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.toniocarlos.finaciamento.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
