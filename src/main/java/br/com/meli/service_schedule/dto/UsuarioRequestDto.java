package br.com.meli.service_schedule.dto;

public record UsuarioRequestDto(String password,
                                String user_type, //"cliente ou prestador"
                                String atividadePrest, //pode ser null se for cliente
                                String nome,
                                String email,
                                String cep){
}
