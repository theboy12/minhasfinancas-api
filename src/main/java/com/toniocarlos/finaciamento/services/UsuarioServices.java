package com.toniocarlos.finaciamento.services;

import java.util.List;

import com.toniocarlos.finaciamento.model.entity.Usuario;

public interface UsuarioServices {

	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);

	
}
