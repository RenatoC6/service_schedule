package br.com.meli.service_schedule.dto;

import java.time.LocalDateTime;

public record AgendaRequestDto(Long prestadorId,
                               LocalDateTime dataHoraDisponivel,
                               String status) {
}
