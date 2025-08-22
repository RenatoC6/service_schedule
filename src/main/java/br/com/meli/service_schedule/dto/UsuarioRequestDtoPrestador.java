package br.com.meli.service_schedule.dto;

public record UsuarioRequestDtoPrestador (String password,
    String atividadePrest, //pode ser null se for cliente
    String nome,
    String email,
    String cep){
}
