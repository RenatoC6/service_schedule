package br.com.meli.service_schedule.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> errorResponse = new LinkedHashMap<>();
//        errorResponse.put("status", 401);
//        errorResponse.put("error", "Não Autorizado");
        errorResponse.put("message", "Token invalido ou inexistente. Corrija o token ou faça login para acessar este recurso.");
        errorResponse.put("details", "Para acessar este endpoint, você precisa:");
        errorResponse.put("instructions", Map.of(
                "1", "Fazer login no endpoint /auth/login",
                "2", "Incluir o token JWT no header: Authorization: Bearer <seu-token>",
                "3", "Verificar se o token não expirou"
        ));


        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
