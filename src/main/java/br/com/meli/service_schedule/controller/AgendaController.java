package br.com.meli.service_schedule.controller;

import br.com.meli.service_schedule.dto.AgendaRequestDto;
import br.com.meli.service_schedule.model.AgendaPrestadorModel;
import br.com.meli.service_schedule.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/agenda")
public class AgendaController {

    @Autowired
    AgendaService agendaService;

    @PostMapping
    public ResponseEntity<?> criarAegenda(@RequestBody AgendaRequestDto dto) {


        AgendaPrestadorModel agendaModel = agendaService.cadastrarAgenda(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(agendaModel);
    }

    @GetMapping
    public ResponseEntity<?> listAgendasDisponiveis() {
        return ResponseEntity.status(HttpStatus.OK).body(agendaService.listarAgendasDisponiveis());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {

        agendaService.deleteAgenda(id);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAgenda(@PathVariable Long id, @RequestBody AgendaRequestDto dto) {
        AgendaPrestadorModel updatedAgenda = agendaService.atualizarAgenda(id, dto);

        return ResponseEntity.status(HttpStatus.OK).body(updatedAgenda);
    }

}
