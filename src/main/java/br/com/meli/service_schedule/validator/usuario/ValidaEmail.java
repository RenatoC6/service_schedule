package br.com.meli.service_schedule.validator.usuario;

import br.com.meli.service_schedule.dto.UsuarioRequestDto;
import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidaEmail implements UsuarioValidator { // Nomes coincidem

    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    public void validarUsuario(UsuarioRequestDto dto) {
        String email = dto.email();
        if (email == null || !email.contains("@")) {
            throw new GenericException("Email inválido: " + email);
        }

        boolean exists = usuarioRepository.existsByEmailIgnoreCase(email);
        if (exists) {
            throw new GenericException("Email já cadastrado: " + email);
        }
    }
}