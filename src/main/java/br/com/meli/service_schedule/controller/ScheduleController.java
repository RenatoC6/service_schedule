package br.com.meli.service_schedule.controller;

import br.com.meli.service_schedule.dto.ScheduleRequestDto;
import br.com.meli.service_schedule.dto.ScheduleResponseDto;
import br.com.meli.service_schedule.model.ScheduleModel;
import br.com.meli.service_schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Operation(summary = "Cadastrar agendamento de servicos")
    @PostMapping
    public ResponseEntity<?> criarSchedule(@RequestBody ScheduleRequestDto dto) {

        ScheduleResponseDto scheduleResponseDto = scheduleService.cadastrarschedule(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleResponseDto);
    }

    @Operation(summary = "Listar todos agendamento pendentes")
    @GetMapping
    public ResponseEntity<?> ListarSchedule() {
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.listarSchedules());
    }

    @Operation(summary = "Listar agendamento por prestador")
    @GetMapping("/{nomePrestador}")
    public ResponseEntity<?> ListarSchedulePorPrestador(@PathVariable String nomePrestador) {

        List<ScheduleModel> scheduleModelList = scheduleService.listarSchedulesPorPrestador(nomePrestador);

        return ResponseEntity.status(HttpStatus.OK).body(scheduleModelList);
    }

    @Operation(summary = "Cancelar um agendamento",
            description = "Cancela o agendamento identificado por {id} e registra o motivo fornecido pelo usuário."
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> cancelarSchedule(
            @Parameter(description = "ID do schedule a ser cancelado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Motivo do cancelamento (obrigatório)", required = true)
            @RequestParam String motivoObs) {

        ScheduleResponseDto scheduleResponseDto = scheduleService.cancelarSchedule(id, motivoObs);

        return ResponseEntity.status(HttpStatus.OK).body(scheduleResponseDto);

    }

    @Operation(summary = "Concluir um agendamento")
    @PutMapping("/{id}/concluir")
    public ResponseEntity<?> concluirSchedule(
            @Parameter(description = "ID do schedule a ser concluído", required = true)
            @PathVariable Long id) {

        ScheduleResponseDto scheduleResponseDto = scheduleService.concluirSchedule(id);

        return ResponseEntity.status(HttpStatus.OK).body(scheduleResponseDto);
    }


}
