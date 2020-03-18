package com.toniocarlos.finaciamento.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toniocarlos.finaciamento.exception.ErroAutenticacao;
import com.toniocarlos.finaciamento.exception.RegraNegocioException;
import com.toniocarlos.finaciamento.model.entity.Usuario;
import com.toniocarlos.finaciamento.model.repository.UsuarioRepository;
import com.toniocarlos.finaciamento.services.UsuarioServices;

@Service
public class UsuarioServiceImpl implements UsuarioServices {
	
	private UsuarioRepository repository;

	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent())
		{
			throw new ErroAutenticacao("Não foi possível autenticar! Email não cadastrado!");
		}
		
		if(!usuario.get().getSenha().equals(senha))
		{
			throw new ErroAutenticacao("Senha inválida!");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		
		boolean exist = repository.existsByEmail(email);
		
		if(exist) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com esse e-mail!");
		}
		
	}
	
}
