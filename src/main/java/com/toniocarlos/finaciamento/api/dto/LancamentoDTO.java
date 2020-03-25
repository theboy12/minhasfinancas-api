package com.toniocarlos.finaciamento.api.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LancamentoDTO {

	private Long id;
	private String descricao;
	private int mes;
	private int ano;
	private BigDecimal valor;
	private Long idUsuario;
	private String tipo;
	private String status;
}
