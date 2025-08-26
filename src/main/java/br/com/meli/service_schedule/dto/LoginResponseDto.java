package br.com.meli.service_schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    
    private String token;
    private String type = "Bearer";
    private String email;
    private String nome;
    
    public LoginResponseDto(String token, String email, String nome) {
        this.token = token;
        this.email = email;
        this.nome = nome;
    }
}
