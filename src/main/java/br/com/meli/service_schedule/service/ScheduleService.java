package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.ScheduleRequestDto;
import br.com.meli.service_schedule.dto.ScheduleResponseDto;
import br.com.meli.service_schedule.dto.ScheduleUpdateDto;
import br.com.meli.service_schedule.exception.ConflictException;
import br.com.meli.service_schedule.exception.EntidadeNaoEncontradaException;
import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.model.*;
import br.com.meli.service_schedule.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public ScheduleResponseDto cadastrarschedule(ScheduleRequestDto dto) {

        var servico = servicoRepository.findById(dto.servicoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Servico não encontrado"));

        var cliente = usuarioRepository.findById(dto.clienteId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado"));
        if (!(cliente instanceof ClienteModel)) {
            throw new GenericException("Usuario informado não é um cliente");
        }

        AgendaPrestadorModel agendaPrestador = buscarAgendaPrestador(dto.agendaPrestadorId());

        PrestadorModel prestador = prestadorRepository.findPrestadorById(agendaPrestador.getPrestadorModel().getId());

        ScheduleModel schedule = new ScheduleModel();
        schedule.setServicoModel(servico);
        schedule.setClienteModel((ClienteModel) cliente);
        schedule.setPrestadorModel(prestador);
        schedule.setAgendaPrestadorModel(agendaPrestador);
        schedule.setDataHora(agendaPrestador.getDataHoraDisponivel());
        schedule.setStatus(ScheduleStatus.PENDENTE);
        schedule.setCriadoEm(LocalDateTime.now());
        schedule.setAtualizadoEm(LocalDateTime.now());

        scheduleRepository.save(schedule);

        agendaPrestador.setStatus(AgendaStatus.AGUARDANDO);
        agendaPrestadorRepository.save(agendaPrestador);

        // evento Assincrono para enviar email
        eventPublisher.publishEvent(new EmailScheduleEvent(schedule.getPrestadorModel().getEmail(),
                schedule.getPrestadorModel().getNome(),
                schedule.getId(),
                schedule.getServicoModel().getNome(),
                schedule.getServicoModel().getDescricao(),
                schedule.getClienteModel().getNome(),
                schedule.getClienteModel().getEndereco(),
                schedule.getDataHora()
        ));

        return new ScheduleResponseDto(schedule.getId(), schedule.getServicoModel().getNome(), schedule.getClienteModel().getNome(),
                schedule.getPrestadorModel().getNome(), schedule.getDataHora(),
                schedule.getStatus().name());

    }

    @Transactional
    public ScheduleResponseDto atualizarschedule(Long idSchedule, ScheduleUpdateDto dto) {

        ScheduleModel schedule = scheduleRepository.findById(idSchedule)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("id do agendamento não encontrado"));

        if (schedule.getStatus() != ScheduleStatus.PENDENTE &&
                schedule.getStatus() != ScheduleStatus.ACEITO &&
                schedule.getStatus() != ScheduleStatus.REJEITADO) {
            throw new ConflictException("Apenas schedules pendentes/aceitos ou Rejeitados podem ser reagendados");
        }

        if (dto.servicoId() == null || dto.agendaPrestadorId() == null) {
            throw new GenericException("Servico ou Agenda não podem ser nulos");
        }

        if (dto.servicoId().equals(schedule.getServicoModel().getId()) && dto.agendaPrestadorId().equals(schedule.getPrestadorModel().getId())) {
            throw new GenericException("Nenhum dado foi alterado, não é necessário atualizar o agendamento");
        }


        if (!dto.servicoId().equals(schedule.getServicoModel().getId())) {
            var servico = servicoRepository.findById(dto.servicoId())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Servico não encontrado"));
            schedule.setServicoModel(servico);
        }

        if (!dto.agendaPrestadorId().equals(schedule.getPrestadorModel().getId())) {

            // atualiza a agenda que sendo alterada para disponível
            AgendaPrestadorModel agendaAntiga = schedule.getAgendaPrestadorModel();
            agendaAntiga.setStatus(AgendaStatus.DISPONIVEL);
            agendaPrestadorRepository.save(agendaAntiga);

            // trata agenda nova
            AgendaPrestadorModel agendaPrestador = buscarAgendaPrestador(dto.agendaPrestadorId());
            schedule.setAgendaPrestadorModel(agendaPrestador);
            schedule.setDataHora(agendaPrestador.getDataHoraDisponivel());
            schedule.setPrestadorModel(agendaPrestador.getPrestadorModel());
            agendaPrestador.setStatus(AgendaStatus.AGUARDANDO);
            agendaPrestadorRepository.save(agendaPrestador);
        }

        schedule.setAtualizadoEm(LocalDateTime.now());
        schedule.setStatus(ScheduleStatus.PENDENTE);

        scheduleRepository.save(schedule);

        // evento Assincrono para enviar email
        eventPublisher.publishEvent(new EmailScheduleEvent(schedule.getPrestadorModel().getEmail(),
                schedule.getPrestadorModel().getNome(),
                schedule.getId(),
                schedule.getServicoModel().getNome(),
                schedule.getServicoModel().getDescricao(),
                schedule.getClienteModel().getNome(),
                schedule.getClienteModel().getEndereco(),
                schedule.getDataHora()
        ));

        return new ScheduleResponseDto(schedule.getId(), schedule.getServicoModel().getNome(),
                schedule.getClienteModel().getNome(), schedule.getPrestadorModel().getNome(),
                schedule.getDataHora(), schedule.getStatus().name());


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

        if (schedule.getStatus() != ScheduleStatus.PENDENTE && schedule.getStatus() != ScheduleStatus.ACEITO) {
            throw new ConflictException("Apenas schedules pendentes ou aceito podem ser cancelados");
        }

        if (schedule.getDataHora().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Não é possível cancelar um agendamento que está a menos de 2 horas do horário marcado: " + schedule.getDataHora());
        }

        schedule.setMotivo(motivoCancelamento);
        schedule.setAtualizadoEm(LocalDateTime.now());
        schedule.setStatus(ScheduleStatus.CANCELADO);
        scheduleRepository.save(schedule);

        Long idAgenda = schedule.getAgendaPrestadorModel().getId();
        AgendaPrestadorModel agenda = agendaPrestadorRepository.findAgendaPrestadorModelsById(idAgenda);

        if (agenda.getDataHoraDisponivel().isAfter(LocalDateTime.now().plusHours(4))) {
            agenda.setStatus(AgendaStatus.DISPONIVEL);
        } else {
            agenda.setStatus(AgendaStatus.CANCELADO);
        }

        agendaPrestadorRepository.save(agenda);

        return new ScheduleResponseDto(schedule.getId(), schedule.getClienteModel().getNome(),
                schedule.getPrestadorModel().getNome(), schedule.getServicoModel().getNome(), schedule.getDataHora(),
                schedule.getStatus().name());
    }

    public ScheduleResponseDto aceitarSchedule(Long scheduleId) {
        ScheduleModel schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("id do agendamento não encontrado"));

        schedule.setAtualizadoEm(LocalDateTime.now());
        schedule.setStatus(ScheduleStatus.ACEITO);
        scheduleRepository.save(schedule);

        Long idAgenda = schedule.getAgendaPrestadorModel().getId();
        AgendaPrestadorModel agenda = agendaPrestadorRepository.findAgendaPrestadorModelsById(idAgenda);

        agenda.setStatus(AgendaStatus.RESERVADO);
        agendaPrestadorRepository.save(agenda);

        // Enviar email de aceite para o cliente
        emailService.enviarEmailClienteSchedule(schedule.getClienteModel().getEmail(), schedule.getClienteModel().getNome(),
                schedule.getPrestadorModel().getNome(), schedule.getServicoModel().getNome(), schedule.getServicoModel().getDescricao(), schedule.getDataHora(), true);


        return new ScheduleResponseDto(schedule.getId(), schedule.getClienteModel().getNome(),
                schedule.getPrestadorModel().getNome(), schedule.getServicoModel().getNome(), schedule.getDataHora(),
                schedule.getStatus().name());
    }

    public ScheduleResponseDto recusarSchedule(Long scheduleId) {
        ScheduleModel schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("id do agendamento não encontrado"));

        schedule.setAtualizadoEm(LocalDateTime.now());
        schedule.setStatus(ScheduleStatus.REJEITADO);
        scheduleRepository.save(schedule);

        Long idAgenda = schedule.getAgendaPrestadorModel().getId();
        AgendaPrestadorModel agenda = agendaPrestadorRepository.findAgendaPrestadorModelsById(idAgenda);

        agenda.setStatus(AgendaStatus.DISPONIVEL);
        agendaPrestadorRepository.save(agenda);

        // Enviar email de recusa para o cliente
        emailService.enviarEmailClienteSchedule(schedule.getClienteModel().getEmail(), schedule.getClienteModel().getNome(),
                schedule.getPrestadorModel().getNome(), schedule.getServicoModel().getNome(), schedule.getServicoModel().getDescricao(), schedule.getDataHora(), false);

        return new ScheduleResponseDto(schedule.getId(), schedule.getClienteModel().getNome(),
                schedule.getPrestadorModel().getNome(), schedule.getServicoModel().getNome(), schedule.getDataHora(),
                schedule.getStatus().name());
    }


    public ScheduleResponseDto concluirSchedule(Long scheduleId) {
        ScheduleModel schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("id do agendamento não encontrado"));

        if (schedule.getStatus() != ScheduleStatus.ACEITO) {
            throw new ConflictException("Apenas schedules aceitos podem ser finalizados");
        }

        schedule.setAtualizadoEm(LocalDateTime.now());
        schedule.setStatus(ScheduleStatus.FINALIZADO);
        scheduleRepository.save(schedule);

        Long idAgenda = schedule.getAgendaPrestadorModel().getId();
        AgendaPrestadorModel agenda = agendaPrestadorRepository.findAgendaPrestadorModelsById(idAgenda);

        agenda.setStatus(AgendaStatus.CONCLUIDO);
        agendaPrestadorRepository.save(agenda);

        return new ScheduleResponseDto(schedule.getId(), schedule.getClienteModel().getNome(),
                schedule.getPrestadorModel().getNome(), schedule.getServicoModel().getNome(), schedule.getDataHora(),
                schedule.getStatus().name());
    }


// metodos auxiliares

    public AgendaPrestadorModel buscarAgendaPrestador(Long agendaPrestadorId) {

        AgendaPrestadorModel agenda = agendaPrestadorRepository.findById(agendaPrestadorId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Agenda não encontrada: " + agendaPrestadorId));

        if (!agenda.getStatus().equals(AgendaStatus.DISPONIVEL)) {
            throw new GenericException("A agenda " + agendaPrestadorId + " não está disponível. Status atual: " + agenda.getStatus());
        }

        if (agenda.getDataHoraDisponivel().isBefore(LocalDateTime.now())) {
            throw new GenericException("A agenda " + agendaPrestadorId + " está com data/hora no passado: " + agenda.getDataHoraDisponivel());
        }

        return agenda;
    }

}
