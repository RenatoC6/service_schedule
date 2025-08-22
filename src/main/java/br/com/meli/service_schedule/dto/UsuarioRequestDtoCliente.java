package br.com.meli.service_schedule.dto;

public record UsuarioRequestDtoCliente(String password,
                                       String nome,
                                       String email,
                                       String cep) {
}
