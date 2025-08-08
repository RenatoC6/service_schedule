package br.com.meli.service_schedule.validator.servico;

import br.com.meli.service_schedule.dto.ScheduleRequestDto;

public interface ServicoValidator {

    void validarServico(ScheduleRequestDto dto);
}
