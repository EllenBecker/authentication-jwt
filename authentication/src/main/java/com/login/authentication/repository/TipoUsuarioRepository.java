package com.login.authentication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import  com.login.authentication.models.EnumTipoUsuario;
import  com.login.authentication.models.TipoUsuario;

@Repository
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {
  Optional<TipoUsuario> findByName(EnumTipoUsuario name);
}
