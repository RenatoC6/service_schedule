package br.com.meli.service_schedule.validator.usuario;

import br.com.meli.service_schedule.exception.GenericException;
import org.springframework.stereotype.Component;

@Component
public class ValidaUserType implements UsuarioValidator {

    @Override
    public void validarUsuario(String userType, String atividadePrest) {
        if (!"cliente".equalsIgnoreCase(userType) && !"prestador".equalsIgnoreCase(userType)) {
            throw new GenericException("O campo userType é obrigatório e deve ser 'cliente' ou 'prestador'");
        }
    }
}
