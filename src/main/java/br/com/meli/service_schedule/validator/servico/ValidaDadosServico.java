package br.com.meli.service_schedule.validator.servico;

import br.com.meli.service_schedule.dto.ServicoRequestDto;
import br.com.meli.service_schedule.exception.GenericException;
import org.springframework.stereotype.Component;

@Component
public class ValidaDadosServico implements ServicoValidator{

    @Override
    public void validarServico(ServicoRequestDto dto) {
        if (dto.nome() == null || dto.nome().isEmpty()) {
            throw new GenericException("O nome do serviço não pode ser vazio.");
        }
        if (dto.descricao() == null || dto.descricao().isEmpty()) {
            throw new GenericException("A descrição do serviço não pode ser vazia.");
        }
        if (dto.preco() <= 0) {
            throw new GenericException("O preço do serviço deve ser maior que zero.");
        }
    }
}
