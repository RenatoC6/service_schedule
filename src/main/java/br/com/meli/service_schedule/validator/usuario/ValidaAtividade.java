package br.com.meli.service_schedule.validator.usuario;

import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.model.Atividades;
import org.springframework.stereotype.Component;

@Component
public class ValidaAtividade implements UsuarioValidator {

    public void validarUsuario(String userType, String atividadePrest) {

        if ("cliente".equalsIgnoreCase(userType))
            return;
        else if (atividadePrest == null) {
            throw new GenericException("Para prestador, Atividade é obrigatória.");
        }

        if (!Atividades.existeAtividade(atividadePrest)) {
            throw new GenericException("Atividade não existe: " + atividadePrest);
        }


    }

}
