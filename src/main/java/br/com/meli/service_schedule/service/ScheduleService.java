package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.ScheduleRequestDto;
import br.com.meli.service_schedule.dto.ScheduleResponseDto;
import br.com.meli.service_schedule.exception.ConflictException;
import br.com.meli.service_schedule.exception.EntidadeNaoEncontradaException;
import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.model.*;
import br.com.meli.service_schedule.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ServicoRepository servicoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AgendaPrestadorRepository agendaPrestadorRepository;
    @Autowired
    private PrestadorRepository prestadorRepository;
    @Autowired
    private EmailService emailService;


    public ScheduleResponseDto cadastrarschedule(ScheduleRequestDto dto) {
        ScheduleModel schedule = new ScheduleModel();
        schedule.setServicoModel(buscarServico(dto.servicoId()));
        schedule.setClienteModel(buscarCliente(dto.clienteId()));
        schedule.setPrestadorModel(buscarPrestador(dto.prestadorId()));
        AgendaPrestadorModel agendaPrestadorModel = buscarAgendaPrestador(dto.agendaPrestadorId(), dto.prestadorId());
        schedule.setAgendaPrestadorModel(agendaPrestadorModel);
        schedule.setDataHora(agendaPrestadorModel.getDataHoraDisponivel());
        schedule.setStatus(ScheduleStatus.pendente);
        schedule.setCriadoEm(LocalDateTime.now());
        schedule.setAtualizadoEm(LocalDateTime.now());

        scheduleRepository.save(schedule);

        agendaPrestadorModel.setStatus(AgendaStatus.aguardando);
        agendaPrestadorRepository.save(agendaPrestadorModel);

        emailService.enviarEmailAceiteSchedule(
                schedule.getPrestadorModel().getEmail(),
                schedule.getPrestadorModel().getNome(),
                schedule.getId()
        );
        return new ScheduleResponseDto(schedule.getId(), schedule.getClienteModel().getNome(),
                schedule.getPrestadorModel().getNome(), schedule.getServicoModel().getNome(), schedule.getDataHora(),
                schedule.getStatus().name());

    }

    public List<ScheduleResponseDto> listarSchedules() {

        List<ScheduleModel> schedules = scheduleRepository.findAll();
        return schedules.stream()
                .map(schedule -> new ScheduleResponseDto(schedule.getId(), schedule.getClienteModel().getNome(),
                        schedule.getPrestadorModel().getNome(), schedule.getServicoModel().getNome(),
                        schedule.getDataHora(), schedule.getStatus().name()))
                .toList();
    }

    public List<ScheduleModel> listarSchedulesPorPrestador(@PathVariable String nomePrestador) {

        List<PrestadorModel> prestadores = prestadorRepository.findByNome(nomePrestador);

        if (prestadores == null || prestadores.isEmpty()) {
            throw new EntidadeNaoEncontradaException("Prestador não encontrado: " + nomePrestador);
        }

        List<ScheduleModel> schedules = new ArrayList<>();
        for (PrestadorModel prest : prestadores) {
            schedules.addAll(scheduleRepository.findByPrestadorModel(prest));
        }

        if (schedules.isEmpty()) {
            throw new GenericException("Nenhum agendamento encontrado para o prestador: " + nomePrestador);
        }

        return schedules;
    }

    public ScheduleResponseDto cancelarSchedule(Long scheduleId, String motivoCancelamento) {
        if (motivoCancelamento == null || motivoCancelamento.isBlank()) {
            throw new GenericException("Motivo de cancelamento invalido");
        }
        ScheduleModel schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("id do agendamento não encontrado"));

        if (schedule.getStatus() != ScheduleStatus.pendente) {
            throw new ConflictException("Apenas schedules pendentes podem ser cancelados");
        }

        if (schedule.getDataHora().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Não é possível cancelar um agendamento que está a menos de 2 horas do horário marcado: " + schedule.getDataHora());
        }

        schedule.setMotivo(motivoCancelamento);
        schedule.setAtualizadoEm(LocalDateTime.now());
        schedule.setStatus(ScheduleStatus.cancelado);
        scheduleRepository.save(schedule);

        Long idAgenda = schedule.getAgendaPrestadorModel().getId();
        AgendaPrestadorModel agenda = agendaPrestadorRepository.findAgendaPrestadorModelsById(idAgenda);

        if (agenda.getDataHoraDisponivel().isAfter(LocalDateTime.now().plusHours(4))) {
            agenda.setStatus(AgendaStatus.disponivel);
        } else {
            agenda.setStatus(AgendaStatus.cancelado);
        }

        agendaPrestadorRepository.save(agenda);

        return new ScheduleResponseDto(schedule.getId(), schedule.getClienteModel().getNome(),
                schedule.getPrestadorModel().getNome(), schedule.getServicoModel().getNome(), schedule.getDataHora(),
                schedule.getStatus().name());
    }


    public ScheduleResponseDto concluirSchedule(Long scheduleId) {
        ScheduleModel schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("id do agendamento não encontrado"));

        if (schedule.getStatus() != ScheduleStatus.aceito) {
            throw new ConflictException("Apenas schedules aceitos podem ser finalizados");
        }

        schedule.setAtualizadoEm(LocalDateTime.now());
        schedule.setStatus(ScheduleStatus.finalizado);
        scheduleRepository.save(schedule);

        Long idAgenda = schedule.getAgendaPrestadorModel().getId();
        AgendaPrestadorModel agenda = agendaPrestadorRepository.findAgendaPrestadorModelsById(idAgenda);

        agenda.setStatus(AgendaStatus.concluido);
        agendaPrestadorRepository.save(agenda);

        return new ScheduleResponseDto(schedule.getId(), schedule.getClienteModel().getNome(),
                schedule.getPrestadorModel().getNome(), schedule.getServicoModel().getNome(), schedule.getDataHora(),
                schedule.getStatus().name());
    }


    // metodos auxiliares para buscar entidades

    public ServicoModel buscarServico(Long servicoid) {
        return servicoRepository.findById(servicoid)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Servico não encontrado"));
    }

    public PrestadorModel buscarPrestador(Long prestadorId) {
        return (PrestadorModel) usuarioRepository.findById(prestadorId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Prestador não encontrado"));
    }

    public ClienteModel buscarCliente(Long clienteId) {
        return (ClienteModel) usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado"));

    }

    public AgendaPrestadorModel buscarAgendaPrestador(Long agendaPrestadorId, Long prestadorId) {

        AgendaPrestadorModel agenda = agendaPrestadorRepository.findById(agendaPrestadorId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Agenda não encontrada: " + agendaPrestadorId));

        if (!agenda.getPrestadorModel().getId().equals(prestadorId)) {
            throw new ConflictException("Agenda não pertence ao prestador informado");
        }

        if (!agenda.getStatus().equals(AgendaStatus.disponivel)) {
            throw new GenericException("A agenda " + agendaPrestadorId + " não está disponível. Status atual: " + agenda.getStatus());
        }

        return agenda;
    }

}
