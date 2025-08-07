package br.com.meli.service_schedule.dto;

import java.time.LocalDateTime;

public record ScheduleResponseDto(Long id,
                                  String servicoNome,
                                  String clienteNome,
                                  String prestadorNome,
                                  LocalDateTime dataHora,
                                  String status) {
}
