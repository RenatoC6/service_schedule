package br.com.meli.service_schedule.controller;

import br.com.meli.service_schedule.dto.LoginRequestDto;
import br.com.meli.service_schedule.dto.LoginResponseDto;
import br.com.meli.service_schedule.security.JwtUtil;
import br.com.meli.service_schedule.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna um token JWT")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {

        LoginResponseDto response = authService.autenticar(loginRequest);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/validate")
    @Operation(summary = "Validar token", description = "Valida se um token JWT é válido")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {

        Map<String, Object> response = authService.validarToken(authHeader);

        return ResponseEntity.ok(response);
    }
}
