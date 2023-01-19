	package io.github.brunoyillli.minhasfinancas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.brunoyillli.minhasfinancas.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

}
