package br.com.meli.service_schedule.validator.usuario;

import br.com.meli.service_schedule.dto.UsuarioRequestDto;
import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.model.Atividades;
import org.springframework.stereotype.Component;

@Component
public class ValidaAtividade implements UsuarioValidator {

    public void validarUsuario(UsuarioRequestDto dto) {

        if ("cliente".equalsIgnoreCase(dto.user_type()))
            return;
        else if (dto.atividadePrest() == null) {
            throw new GenericException("Para prestador, Atividade é obrigatória.");
        }

        String atividadeDTO = dto.atividadePrest();

        if (!Atividades.existeAtividade(atividadeDTO)) {
            throw new GenericException("Atividade não existe: " + atividadeDTO);
        }


    }

}
