package com.login.authentication.models;

import javax.persistence.*;

@Entity
@Table(name = "tipo_usuario")
public class TipoUsuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private EnumTipoUsuario name;

  public TipoUsuario() {

  }

  public TipoUsuario(EnumTipoUsuario name) {
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public EnumTipoUsuario getName() {
    return name;
  }

  public void setName(EnumTipoUsuario name) {
    this.name = name;
  }
}