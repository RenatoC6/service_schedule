package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.AgendaRequestDto;
import br.com.meli.service_schedule.exception.EntidadeNaoEncontradaException;
import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.model.AgendaPrestadorModel;
import br.com.meli.service_schedule.model.AgendaStatus;
import br.com.meli.service_schedule.repository.AgendaPrestadorRepository;
import br.com.meli.service_schedule.repository.PrestadorRepository;
import br.com.meli.service_schedule.validator.agenda.AgendaValidator;
import br.com.meli.service_schedule.validator.prestador.PrestadorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaService {

    @Autowired
    PrestadorRepository prestadorRepository;

    @Autowired
    AgendaPrestadorRepository agendaPrestadorRepository;

    @Autowired
    private List<PrestadorValidator> prestadorValidator;

    @Autowired
    private List<AgendaValidator> agendaValidators;


    public AgendaPrestadorModel cadastrarAgenda(AgendaRequestDto dto) {

        this.prestadorValidator.forEach(validatorsPrest -> validatorsPrest.validarPrestador(dto.prestadorId()));

        this.agendaValidators.forEach(validators -> validators.validarAgenda(dto));

        AgendaPrestadorModel agenda = new AgendaPrestadorModel();
        agenda.setPrestador(prestadorRepository.findPrestadorById(dto.prestadorId()));
        agenda.setDataHoraDisponivel(dto.dataHoraDisponivel());
        agenda.setStatus(AgendaStatus.disponivel);

        return agendaPrestadorRepository.save(agenda);
    }

    public List<AgendaPrestadorModel> listarAgendasDisponiveis() {

        String status = AgendaStatus.disponivel.name();
        return agendaPrestadorRepository.findByStatusDisponivel(status);
    }

    public void deleteAgenda(Long id) {
        if (agendaPrestadorRepository.existsById(id)) {
            agendaPrestadorRepository.deleteById(id);
            throw new GenericException("Agenda deletada com sucesso: " + id);
        } else {
            throw new EntidadeNaoEncontradaException("Agenda não encontrada: " + id);
        }
    }

}