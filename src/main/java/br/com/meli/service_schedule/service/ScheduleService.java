package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.ScheduleRequestDto;
import br.com.meli.service_schedule.dto.ScheduleResponseDto;
import br.com.meli.service_schedule.model.ClienteModel;
import br.com.meli.service_schedule.model.PrestadorModel;
import br.com.meli.service_schedule.model.ScheduleModel;
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

    public ScheduleResponseDto criarSchedule(ScheduleRequestDto dto) {
        ScheduleModel schedule = new ScheduleModel();
        schedule.setServico(servicoRepository.findById(dto.servicoId()).orElseThrow());
        schedule.setCliente((ClienteModel) usuarioRepository.findById(dto.clienteId()).orElseThrow());
        schedule.setPrestador((PrestadorModel) usuarioRepository.findById(dto.prestadorId()).orElseThrow());
        schedule.setAgendaPrestador(agendaPrestadorRepository.findById(dto.agendaPrestadorId()).orElseThrow());
        schedule.setDataHora(dto.dataHora());
        schedule.setStatus(ScheduleModel.StatusAgendamento.pendente);
        schedule.setCriadoEm(LocalDateTime.now());
        schedule.setAtualizadoEm(LocalDateTime.now());

        scheduleRepository.save(schedule);

        return new ScheduleResponseDto(schedule.getId(), schedule.getCliente().getNome(),
                schedule.getPrestador().getNome(), schedule.getServico().getNome(), schedule.getDataHora(),
                schedule.getStatus().name());

    }
}
