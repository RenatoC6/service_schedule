package br.com.meli.service_schedule.controller;

import br.com.meli.service_schedule.dto.ServicoRequestDto;
import br.com.meli.service_schedule.model.ServicoModel;
import br.com.meli.service_schedule.service.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servico")
public class ServicoController {

    @Autowired
    private ServicoService servicoService;

    @PostMapping
    public ResponseEntity<?> criarServico(@RequestBody ServicoRequestDto dto) {

        ServicoModel servicoModel = servicoService.cadastrarServico(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(servicoModel);
    }

    @PutMapping("/{idServico}")
    public ResponseEntity<?> atualizarServico(@PathVariable Long idServico, @RequestBody ServicoRequestDto dto) {

        ServicoModel servicoModel = servicoService.atualizarServico(idServico, dto);

        return ResponseEntity.status(HttpStatus.OK).body(servicoModel);
    }

    @GetMapping
    public ResponseEntity<?> listarServico() {
        List<ServicoModel> servicoModel = servicoService.listarServicos();

        return ResponseEntity.status(HttpStatus.OK).body(servicoModel);

    }

    @DeleteMapping("/{idServico}")
    public void deletarServico(@PathVariable Long idServico) {
        servicoService.deletarServico(idServico);

    }

}


