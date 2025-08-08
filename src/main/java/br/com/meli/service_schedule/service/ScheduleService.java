package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.ScheduleRequestDto;
import br.com.meli.service_schedule.dto.ScheduleResponseDto;
import br.com.meli.service_schedule.exception.EntidadeNaoEncontradaException;
import br.com.meli.service_schedule.model.*;
import br.com.meli.service_schedule.repository.AgendaPrestadorRepository;
import br.com.meli.service_schedule.repository.ScheduleRepository;
import br.com.meli.service_schedule.repository.ServicoRepository;
import br.com.meli.service_schedule.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public ScheduleResponseDto cadastrarschedule(ScheduleRequestDto dto) {
        ScheduleModel schedule = new ScheduleModel();
        schedule.setServico(buscarServico(dto.servicoId()));
        schedule.setCliente(buscarCliente(dto.clienteId()));
        schedule.setPrestador(buscarPrestador(dto.prestadorId()));
        schedule.setAgendaPrestador(buscarAgendaPrestador(dto.agendaPrestadorId()));
        schedule.setDataHora(dto.dataHora());
        schedule.setStatus(ScheduleModel.StatusAgendamento.pendente);
        schedule.setCriadoEm(LocalDateTime.now());
        schedule.setAtualizadoEm(LocalDateTime.now());

        scheduleRepository.save(schedule);

        return new ScheduleResponseDto(schedule.getId(), schedule.getCliente().getNome(),
                schedule.getPrestador().getNome(), schedule.getServico().getNome(), schedule.getDataHora(),
                schedule.getStatus().name());

    }

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

    public AgendaPrestadorModel buscarAgendaPrestador(Long agendaPrestadorId) {
        return agendaPrestadorRepository.findById(agendaPrestadorId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Agenda do prestador não encontrada"));
    }
}
