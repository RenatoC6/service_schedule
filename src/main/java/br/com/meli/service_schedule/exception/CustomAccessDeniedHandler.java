package br.com.meli.service_schedule.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> errorResponse = new LinkedHashMap<>();
//        errorResponse.put("status", 403);
//        errorResponse.put("error", "Acesso Negado");
        errorResponse.put("message", "Você não tem permissão para acessar este recurso.");
        errorResponse.put("details", "Possíveis causas:");
        errorResponse.put("causes", Map.of(
                "1", "Token JWT inválido ou expirado",
                "2", "Token não fornecido no header Authorization",
                "3", "Formato incorreto do token (deve ser: Bearer <token>)",
                "4", "Usuário não tem as permissões necessárias"
        ));
        errorResponse.put("solution", "Faça login novamente em /auth/login para obter um novo token válido");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
