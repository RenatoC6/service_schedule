package br.com.meli.service_schedule.validator.servico;

import br.com.meli.service_schedule.dto.ServicoRequestDto;

public interface ServicoValidator {

    void validarServico(ServicoRequestDto dto);
}
