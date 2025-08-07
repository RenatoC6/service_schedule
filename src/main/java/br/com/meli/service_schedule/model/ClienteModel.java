package br.com.meli.service_schedule.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("cliente")
public class Cliente extends Usuario {}


