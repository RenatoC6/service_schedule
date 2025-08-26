package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.LoginRequestDto;
import br.com.meli.service_schedule.dto.LoginResponseDto;
import br.com.meli.service_schedule.exception.BadCredentialException;
import br.com.meli.service_schedule.model.UsuarioModel;
import br.com.meli.service_schedule.repository.UsuarioRepository;
import br.com.meli.service_schedule.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponseDto autenticar(LoginRequestDto dto) {

        UsuarioModel usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadCredentialException("Email para Login inválido"));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new BadCredentialException("Senha para Login inválida");
        }


        String token = jwtUtil.generateToken(dto.getEmail());

        LoginResponseDto responseLogin = new LoginResponseDto(token, usuario.getEmail(), usuario.getNome());
        return responseLogin;

    }

    public Map<String, Object> validarToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialException("Token de autorização não fornecido ou formato inválido");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            throw new BadCredentialException("Token JWT inválido ou expirado");
        }

        String email = jwtUtil.extractUsername(token);
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("email", email);

        return response;
    }

}
