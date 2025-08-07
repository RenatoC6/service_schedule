package br.com.meli.service_schedule.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("prestador")
public class PrestadorModel extends UsuarioModel {}

