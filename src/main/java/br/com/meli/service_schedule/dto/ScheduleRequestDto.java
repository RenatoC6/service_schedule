package br.com.meli.service_schedule.dto;

public record ScheduleRequestDto(Long servicoId,
                                 Long clienteId,
                                 Long prestadorId,
                                 Long agendaPrestadorId) {
}
