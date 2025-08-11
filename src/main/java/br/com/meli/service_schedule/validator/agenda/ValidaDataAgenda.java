package br.com.meli.service_schedule.validator.agenda;

import br.com.meli.service_schedule.dto.AgendaRequestDto;
import br.com.meli.service_schedule.exception.GenericException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidaDataAgenda implements AgendaValidator {


    @Override
    public void validarAgenda(AgendaRequestDto dto) {


        if (dto.dataHoraDisponivel() == null) {
            throw new GenericException("data invalida.");
        }

        LocalDateTime agora = LocalDateTime.now();
        if (dto.dataHoraDisponivel().isBefore(agora)) {
            throw new GenericException("Data e hora devem ser futuras.");
        }

    }
}