package br.com.meli.service_schedule.dto;

public record UsuarioRequestDto(String password,
                                String nome,
                                String email,
                                String endereco,
                                String cep,
                                String cidade,
                                String estado){
}
