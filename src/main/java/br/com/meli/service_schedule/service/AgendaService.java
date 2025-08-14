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
        agenda.setPrestadorModel(prestadorRepository.findPrestadorById(dto.prestadorId()));
        agenda.setDataHoraDisponivel(dto.dataHoraDisponivel());
        agenda.setStatus(AgendaStatus.DISPONIVEL);

        return agendaPrestadorRepository.save(agenda);
    }

    public List<AgendaPrestadorModel> listarAgendasDisponiveis() {

        String status = AgendaStatus.DISPONIVEL.name();
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

    public AgendaPrestadorModel atualizarAgenda(Long idAtual, AgendaRequestDto dto) {

        AgendaPrestadorModel agendaAtual = agendaPrestadorRepository.findById(idAtual)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Agenda não encontrada: " + idAtual));


        if (agendaAtual.getStatus() != AgendaStatus.DISPONIVEL) {
            throw new GenericException("A agenda " + idAtual + " não está disponível para atualização. Status atual: " + agendaAtual.getStatus());
        }

        if (!agendaAtual.getPrestadorModel().getId().equals(dto.prestadorId())) {
            this.prestadorValidator.forEach(validatorsPrest -> validatorsPrest.validarPrestador(dto.prestadorId()));
        }

        this.agendaValidators.forEach(validators -> validators.validarAgenda(dto));


        agendaAtual.setDataHoraDisponivel(dto.dataHoraDisponivel());
        agendaAtual.setStatus(AgendaStatus.DISPONIVEL);
        agendaAtual.setPrestadorModel(prestadorRepository.findPrestadorById(dto.prestadorId()));

        return agendaPrestadorRepository.save(agendaAtual);
    }

}