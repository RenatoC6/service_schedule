package br.com.meli.service_schedule.dto;

import java.time.LocalDateTime;

public record AgendaResponseDto(Long id,
                                Long prestadorId,
                                String prestadorNome,
                                LocalDateTime dataHoraDisponivel,
                                String status) {
}
