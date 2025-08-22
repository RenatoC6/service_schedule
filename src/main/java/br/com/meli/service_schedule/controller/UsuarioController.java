package br.com.meli.service_schedule.controller;

import br.com.meli.service_schedule.dto.*;
import br.com.meli.service_schedule.model.UsuarioModel;
import br.com.meli.service_schedule.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "alterar prestador")
    @PutMapping("/{idUsuario}/prestador")
    public ResponseEntity<?> atualizarUsuarioPrestador(@PathVariable Long idUsuario, @RequestBody UsuarioRequestDtoPrestador dto) {

        UsuarioModel usuarioModel = usuarioService.atualizarUsuarioPrestador(idUsuario, dto);

        return ResponseEntity.status(HttpStatus.OK).body(usuarioModel);
    }

    @Operation(summary = "alterar cliente")
    @PutMapping("/{idUsuario}/cliente")
    public ResponseEntity<?> atualizarUsuarioCliente(@PathVariable Long idUsuario, @RequestBody UsuarioRequestDtoCliente dto) {

        UsuarioModel usuarioModel = usuarioService.atualizarUsuarioCliente(idUsuario, dto);

        return ResponseEntity.status(HttpStatus.OK).body(usuarioModel);
    }

    @GetMapping
    public ResponseEntity<?> listarUsuarios() {

        List<UsuarioModel> usuarios = usuarioService.listarUsuarios();

        return  ResponseEntity.status(HttpStatus.OK).body(usuarios);

    }

    @DeleteMapping("/{idUsuario}")
    public void deletarUsuario(@RequestBody Long idUsuario) {

        usuarioService.deleteUsuario(idUsuario);

    }
}
