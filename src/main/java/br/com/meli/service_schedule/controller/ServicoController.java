package br.com.meli.service_schedule.controller;

import br.com.meli.service_schedule.dto.ServicoRequestDto;
import br.com.meli.service_schedule.model.ServicoModel;
import br.com.meli.service_schedule.service.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}


