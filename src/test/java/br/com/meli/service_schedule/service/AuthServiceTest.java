package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.LoginRequestDto;
import br.com.meli.service_schedule.dto.LoginResponseDto;
import br.com.meli.service_schedule.exception.BadCredentialException;
import br.com.meli.service_schedule.model.ClienteModel;
import br.com.meli.service_schedule.model.PrestadorModel;
import br.com.meli.service_schedule.repository.UsuarioRepository;
import br.com.meli.service_schedule.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDto loginRequestDto;
    private ClienteModel clienteModel;
    private PrestadorModel prestadorModel;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("usuario@email.com");
        loginRequestDto.setPassword("password123");

        clienteModel = new ClienteModel();
        clienteModel.setId(1L);
        clienteModel.setEmail("usuario@email.com");
        clienteModel.setNome("João Cliente");
        clienteModel.setPassword("hashedPassword");

        prestadorModel = new PrestadorModel();
        prestadorModel.setId(2L);
        prestadorModel.setEmail("prestador@email.com");
        prestadorModel.setNome("Maria Prestadora");
        prestadorModel.setPassword("hashedPassword");
    }

    @Test
    @DisplayName("Deve autenticar cliente com sucesso")
    void deveAutenticarClienteComSucesso() {
        when(usuarioRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(clienteModel));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("usuario@email.com")).thenReturn("jwt-token-123");

        LoginResponseDto resultado = authService.autenticar(loginRequestDto);

        assertNotNull(resultado);
        assertEquals("jwt-token-123", resultado.getToken());
        assertEquals("Bearer", resultado.getType());
        assertEquals("usuario@email.com", resultado.getEmail());
        assertEquals("João Cliente", resultado.getNome());

        verify(usuarioRepository).findByEmail("usuario@email.com");
        verify(passwordEncoder).matches("password123", "hashedPassword");
        verify(jwtUtil).generateToken("usuario@email.com");
    }

    @Test
    @DisplayName("Deve autenticar prestador com sucesso")
    void deveAutenticarPrestadorComSucesso() {
        loginRequestDto.setEmail("prestador@email.com");
        
        when(usuarioRepository.findByEmail("prestador@email.com")).thenReturn(Optional.of(prestadorModel));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("prestador@email.com")).thenReturn("jwt-token-456");

        LoginResponseDto resultado = authService.autenticar(loginRequestDto);

        assertNotNull(resultado);
        assertEquals("jwt-token-456", resultado.getToken());
        assertEquals("Bearer", resultado.getType());
        assertEquals("prestador@email.com", resultado.getEmail());
        assertEquals("Maria Prestadora", resultado.getNome());

        verify(usuarioRepository).findByEmail("prestador@email.com");
        verify(passwordEncoder).matches("password123", "hashedPassword");
        verify(jwtUtil).generateToken("prestador@email.com");
    }

    @Test
    @DisplayName("Deve lançar exceção ao autenticar com email inexistente")
    void deveLancarExcecaoAoAutenticarComEmailInexistente() {
        when(usuarioRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        loginRequestDto.setEmail("inexistente@email.com");

        BadCredentialException exception = assertThrows(BadCredentialException.class, () -> {
            authService.autenticar(loginRequestDto);
        });

        assertEquals("Email para Login inválido", exception.getMessage());
        verify(usuarioRepository).findByEmail("inexistente@email.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao autenticar com senha incorreta")
    void deveLancarExcecaoAoAutenticarComSenhaIncorreta() {
        when(usuarioRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(clienteModel));
        when(passwordEncoder.matches("senhaErrada", "hashedPassword")).thenReturn(false);

        loginRequestDto.setPassword("senhaErrada");

        BadCredentialException exception = assertThrows(BadCredentialException.class, () -> {
            authService.autenticar(loginRequestDto);
        });

        assertEquals("Senha para Login inválida", exception.getMessage());
        verify(usuarioRepository).findByEmail("usuario@email.com");
        verify(passwordEncoder).matches("senhaErrada", "hashedPassword");
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Deve validar token válido com sucesso")
    void deveValidarTokenValidoComSucesso() {
        String authHeader = "Bearer jwt-token-valid";
        String token = "jwt-token-valid";
        String email = "usuario@email.com";

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn(email);

        Map<String, Object> resultado = authService.validarToken(authHeader);

        assertNotNull(resultado);
        assertTrue((Boolean) resultado.get("valid"));
        assertEquals(email, resultado.get("email"));

        verify(jwtUtil).validateToken(token);
        verify(jwtUtil).extractUsername(token);
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar token sem header Authorization")
    void deveLancarExcecaoAoValidarTokenSemHeaderAuthorization() {
        BadCredentialException exception = assertThrows(BadCredentialException.class, () -> {
            authService.validarToken(null);
        });

        assertEquals("Token de autorização não fornecido ou formato inválido", exception.getMessage());
        verify(jwtUtil, never()).validateToken(anyString());
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar token com formato inválido")
    void deveLancarExcecaoAoValidarTokenComFormatoInvalido() {
        String authHeaderInvalido = "InvalidFormat jwt-token";

        BadCredentialException exception = assertThrows(BadCredentialException.class, () -> {
            authService.validarToken(authHeaderInvalido);
        });

        assertEquals("Token de autorização não fornecido ou formato inválido", exception.getMessage());
        verify(jwtUtil, never()).validateToken(anyString());
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar token inválido")
    void deveLancarExcecaoAoValidarTokenInvalido() {
        String authHeader = "Bearer jwt-token-invalid";
        String token = "jwt-token-invalid";

        when(jwtUtil.validateToken(token)).thenReturn(false);

        BadCredentialException exception = assertThrows(BadCredentialException.class, () -> {
            authService.validarToken(authHeader);
        });

        assertEquals("Token JWT inválido ou expirado", exception.getMessage());
        verify(jwtUtil).validateToken(token);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar token expirado")
    void deveLancarExcecaoAoValidarTokenExpirado() {
        String authHeader = "Bearer jwt-token-expired";
        String token = "jwt-token-expired";

        when(jwtUtil.validateToken(token)).thenReturn(false);

        BadCredentialException exception = assertThrows(BadCredentialException.class, () -> {
            authService.validarToken(authHeader);
        });

        assertEquals("Token JWT inválido ou expirado", exception.getMessage());
        verify(jwtUtil).validateToken(token);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    @DisplayName("Deve extrair token corretamente do header Authorization")
    void deveExtrairTokenCorretamenteDoHeaderAuthorization() {
        String authHeader = "Bearer meu-jwt-token-123";
        String expectedToken = "meu-jwt-token-123";
        String email = "test@email.com";

        when(jwtUtil.validateToken(expectedToken)).thenReturn(true);
        when(jwtUtil.extractUsername(expectedToken)).thenReturn(email);

        Map<String, Object> resultado = authService.validarToken(authHeader);

        assertNotNull(resultado);
        assertTrue((Boolean) resultado.get("valid"));
        assertEquals(email, resultado.get("email"));

        verify(jwtUtil).validateToken(expectedToken);
        verify(jwtUtil).extractUsername(expectedToken);
    }

    @Test
    @DisplayName("Deve validar token com espaços extras no header")
    void deveValidarTokenComEspacosExtrasNoHeader() {
        String authHeader = "Bearer   jwt-token-with-spaces   ";
        String expectedToken = "  jwt-token-with-spaces   ";
        String email = "usuario@email.com";

        when(jwtUtil.validateToken(expectedToken)).thenReturn(true);
        when(jwtUtil.extractUsername(expectedToken)).thenReturn(email);

        Map<String, Object> resultado = authService.validarToken(authHeader);

        assertNotNull(resultado);
        assertTrue((Boolean) resultado.get("valid"));
        assertEquals(email, resultado.get("email"));

        verify(jwtUtil).validateToken(expectedToken);
        verify(jwtUtil).extractUsername(expectedToken);
    }

    @Test
    @DisplayName("Deve retornar mapa com estrutura correta na validação de token")
    void deveRetornarMapaComEstruturaCorretaNaValidacaoDeToken() {
        String authHeader = "Bearer valid-token";
        String token = "valid-token";
        String email = "usuario@email.com";

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractUsername(token)).thenReturn(email);

        Map<String, Object> resultado = authService.validarToken(authHeader);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.containsKey("valid"));
        assertTrue(resultado.containsKey("email"));
        assertTrue((Boolean) resultado.get("valid"));
        assertEquals(email, resultado.get("email"));
    }
}
