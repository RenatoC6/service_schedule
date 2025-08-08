package br.com.meli.service_schedule.controller;

import br.com.meli.service_schedule.dto.ScheduleRequestDto;
import br.com.meli.service_schedule.dto.ScheduleResponseDto;
import br.com.meli.service_schedule.dto.UsuarioRequestDto;
import br.com.meli.service_schedule.model.UsuarioModel;
import br.com.meli.service_schedule.service.ScheduleService;
import br.com.meli.service_schedule.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> criarUsuario(@RequestBody UsuarioRequestDto dto) {

        UsuarioModel usuarioModel = usuarioService.cadastrarUsuario(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioModel);
    }
}
