package br.com.meli.service_schedule.dto;

import java.time.LocalDateTime;

public record ScheduleRequestDto(Long servicoId,
                                 Long clienteId,
                                 Long prestadorId,
                                 Long agendaPrestadorId,
                                 LocalDateTime dataHora) {
}
