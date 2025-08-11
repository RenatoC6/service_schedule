package br.com.meli.service_schedule.validator.agenda;

import br.com.meli.service_schedule.dto.AgendaRequestDto;
import br.com.meli.service_schedule.exception.ConflictException;
import br.com.meli.service_schedule.model.AgendaPrestadorModel;
import br.com.meli.service_schedule.model.PrestadorModel;
import br.com.meli.service_schedule.repository.AgendaPrestadorRepository;
import br.com.meli.service_schedule.repository.PrestadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ValidaDataConflitoAgenda implements AgendaValidator {

    @Autowired
    private AgendaPrestadorRepository agendaRepository;

    @Autowired
    PrestadorRepository prestadorRepository;

    @Override
    public void validarAgenda(AgendaRequestDto dto) {

        LocalDateTime umaHoraAntes = dto.dataHoraDisponivel().minusHours(1);
        LocalDateTime umaHoraDepois = dto.dataHoraDisponivel().plusHours(1);

        PrestadorModel prestadorModel = prestadorRepository.findPrestadorById(dto.prestadorId());

        List<AgendaPrestadorModel> conflitos = agendaRepository.findByPrestadorModelIdAndDataHoraDisponivelBetween(
                prestadorModel.getId(), umaHoraAntes, umaHoraDepois);

        if (!conflitos.isEmpty()) {
            throw new ConflictException("Já existe um horário para este prestador em até 1h da data informada.");
        }


    }
}
