package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.UsuarioRequestDto;
import br.com.meli.service_schedule.dto.UsuarioRequestDtoCliente;
import br.com.meli.service_schedule.dto.UsuarioRequestDtoPrestador;
import br.com.meli.service_schedule.dto.ViaCepDto;
import br.com.meli.service_schedule.exception.EntidadeNaoEncontradaException;
import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.model.Atividades;
import br.com.meli.service_schedule.model.ClienteModel;
import br.com.meli.service_schedule.model.PrestadorModel;
import br.com.meli.service_schedule.model.UsuarioModel;
import br.com.meli.service_schedule.repository.UsuarioRepository;
import br.com.meli.service_schedule.validator.usuario.UsuarioValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private List<UsuarioValidator> validators;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDto usuarioRequestDtoPrestador;
    private UsuarioRequestDto usuarioRequestDtoCliente;
    private UsuarioRequestDtoPrestador usuarioRequestDtoPrestadorUpdate;
    private UsuarioRequestDtoCliente usuarioRequestDtoClienteUpdate;
    private PrestadorModel prestadorModel;
    private ClienteModel clienteModel;
    private ViaCepDto viaCepDto;

    @BeforeEach
    void setUp() {
        usuarioRequestDtoPrestador = new UsuarioRequestDto(
                "password123",
                "prestador",
                "ELETRECISTA",
                "João Silva",
                "joao@email.com",
                "12345-678"
        );

        usuarioRequestDtoCliente = new UsuarioRequestDto(
                "password123",
                "cliente",
                null,
                "Maria Santos",
                "maria@email.com",
                "12345-678"
        );

        usuarioRequestDtoPrestadorUpdate = new UsuarioRequestDtoPrestador(
                "newPassword123",
                "ENCANADOR",
                "João Silva Updated",
                "joao.updated@email.com",
                "87654-321"
        );

        usuarioRequestDtoClienteUpdate = new UsuarioRequestDtoCliente(
                "newPassword123",
                "Maria Santos Updated",
                "maria.updated@email.com",
                "87654-321"
        );

        prestadorModel = new PrestadorModel();
        prestadorModel.setId(1L);
        prestadorModel.setNome("João Silva");
        prestadorModel.setEmail("joao@email.com");
        prestadorModel.setPassword("encodedPassword");
        prestadorModel.setCep("12345-678");
        prestadorModel.setAtividadePrest(Atividades.ELETRECISTA);
        prestadorModel.setCreatedAt(LocalDateTime.now());

        clienteModel = new ClienteModel();
        clienteModel.setId(2L);
        clienteModel.setNome("Maria Santos");
        clienteModel.setEmail("maria@email.com");
        clienteModel.setPassword("encodedPassword");
        clienteModel.setCep("12345-678");
        clienteModel.setCreatedAt(LocalDateTime.now());

        viaCepDto = new ViaCepDto(
                "12345-678",
                "Rua das Flores",
                "",
                "Centro",
                "São Paulo",
                "SP",
                "3550308",
                "1004",
                "11",
                "7107",
                false
        );
    }

    @Test
    @DisplayName("Deve cadastrar prestador com sucesso")
    void deveCadastrarPrestadorComSucesso() {
        UsuarioService usuarioServiceSpy = spy(usuarioService);
        doNothing().when(validators).forEach(any());
        when(usuarioRepository.existsUsuarioByEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(PrestadorModel.class))).thenReturn(prestadorModel);
        doReturn(viaCepDto).when(usuarioServiceSpy).buscarCep("12345-678");

        UsuarioModel resultado = usuarioServiceSpy.cadastrarUsuario(usuarioRequestDtoPrestador);

        assertNotNull(resultado);
        assertTrue(resultado instanceof PrestadorModel);
        PrestadorModel prestador = (PrestadorModel) resultado;
        assertEquals(Atividades.ELETRECISTA, prestador.getAtividadePrest());
        verify(usuarioRepository).save(any(PrestadorModel.class));
        verify(passwordEncoder).encode("password123");
        verify(usuarioServiceSpy).buscarCep("12345-678");
    }

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso")
    void deveCadastrarClienteComSucesso() {
        UsuarioService usuarioServiceSpy = spy(usuarioService);
        doNothing().when(validators).forEach(any());
        when(usuarioRepository.existsUsuarioByEmail("maria@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(ClienteModel.class))).thenReturn(clienteModel);
        doReturn(viaCepDto).when(usuarioServiceSpy).buscarCep("12345-678");

        UsuarioModel resultado = usuarioServiceSpy.cadastrarUsuario(usuarioRequestDtoCliente);

        assertNotNull(resultado);
        assertTrue(resultado instanceof ClienteModel);
        verify(usuarioRepository).save(any(ClienteModel.class));
        verify(passwordEncoder).encode("password123");
        verify(usuarioServiceSpy).buscarCep("12345-678");
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar usuário com tipo inválido")
    void deveLancarExcecaoAoCadastrarUsuarioComTipoInvalido() {
        UsuarioRequestDto dtoInvalido = new UsuarioRequestDto(
                "password123",
                "admin",
                null,
                "Admin User",
                "admin@email.com",
                "12345-678"
        );

        UsuarioService usuarioServiceSpy = spy(usuarioService);
        doNothing().when(validators).forEach(any());
        when(usuarioRepository.existsUsuarioByEmail("admin@email.com")).thenReturn(false);
        doReturn(viaCepDto).when(usuarioServiceSpy).buscarCep("12345-678");

        GenericException exception = assertThrows(GenericException.class, () -> {
            usuarioServiceSpy.cadastrarUsuario(dtoInvalido);
        });

        assertEquals("Tipo de Usuario(userType),  deve ser 'cliente' ou 'prestador'", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar usuário com email já existente")
    void deveLancarExcecaoAoCadastrarUsuarioComEmailJaExistente() {
        when(usuarioRepository.existsUsuarioByEmail("joao@email.com")).thenReturn(true);

        GenericException exception = assertThrows(GenericException.class, () -> {
            usuarioService.cadastrarUsuario(usuarioRequestDtoPrestador);
        });

        assertEquals("Email já cadastrado: joao@email.com", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar usuário com email inválido")
    void deveLancarExcecaoAoCadastrarUsuarioComEmailInvalido() {
        UsuarioRequestDto dtoEmailInvalido = new UsuarioRequestDto(
                "password123",
                "cliente",
                null,
                "Maria Santos",
                "email-invalido",
                "12345-678"
        );

        GenericException exception = assertThrows(GenericException.class, () -> {
            usuarioService.cadastrarUsuario(dtoEmailInvalido);
        });

        assertEquals("Email inválido: email-invalido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar prestador com sucesso")
    void deveAtualizarPrestadorComSucesso() {
        UsuarioService usuarioServiceSpy = spy(usuarioService);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(prestadorModel));
        when(usuarioRepository.existsUsuarioByEmail("joao.updated@email.com")).thenReturn(false);
        doNothing().when(validators).forEach(any());
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(usuarioRepository.save(any(PrestadorModel.class))).thenReturn(prestadorModel);
        doReturn(viaCepDto).when(usuarioServiceSpy).buscarCep("87654-321");

        UsuarioModel resultado = usuarioServiceSpy.atualizarUsuarioPrestador(1L, usuarioRequestDtoPrestadorUpdate);

        assertNotNull(resultado);
        assertTrue(resultado instanceof PrestadorModel);
        verify(usuarioRepository).save(any(PrestadorModel.class));
        verify(passwordEncoder).encode("newPassword123");
        verify(usuarioServiceSpy).buscarCep("87654-321");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar prestador inexistente")
    void deveLancarExcecaoAoTentarAtualizarPrestadorInexistente() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            usuarioService.atualizarUsuarioPrestador(999L, usuarioRequestDtoPrestadorUpdate);
        });

        assertEquals("Usuario nao encontrado: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar cliente como prestador")
    void deveLancarExcecaoAoTentarAtualizarClienteComoPrestador() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(clienteModel));

        GenericException exception = assertThrows(GenericException.class, () -> {
            usuarioService.atualizarUsuarioPrestador(2L, usuarioRequestDtoPrestadorUpdate);
        });

        assertEquals("Usuario nao e prestador", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        UsuarioService usuarioServiceSpy = spy(usuarioService);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(clienteModel));
        when(usuarioRepository.existsUsuarioByEmail("maria.updated@email.com")).thenReturn(false);
        doNothing().when(validators).forEach(any());
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(usuarioRepository.save(any(ClienteModel.class))).thenReturn(clienteModel);
        doReturn(viaCepDto).when(usuarioServiceSpy).buscarCep("87654-321");

        UsuarioModel resultado = usuarioServiceSpy.atualizarUsuarioCliente(2L, usuarioRequestDtoClienteUpdate);

        assertNotNull(resultado);
        assertTrue(resultado instanceof ClienteModel);
        verify(usuarioRepository).save(any(ClienteModel.class));
        verify(passwordEncoder).encode("newPassword123");
        verify(usuarioServiceSpy).buscarCep("87654-321");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar cliente inexistente")
    void deveLancarExcecaoAoTentarAtualizarClienteInexistente() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            usuarioService.atualizarUsuarioCliente(999L, usuarioRequestDtoClienteUpdate);
        });

        assertEquals("Usuario nao encontrado: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar prestador como cliente")
    void deveLancarExcecaoAoTentarAtualizarPrestadorComoCliente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(prestadorModel));

        GenericException exception = assertThrows(GenericException.class, () -> {
            usuarioService.atualizarUsuarioCliente(1L, usuarioRequestDtoClienteUpdate);
        });

        assertEquals("Usuario nao e cliente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosOsUsuarios() {
        List<UsuarioModel> usuarios = Arrays.asList(prestadorModel, clienteModel);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<UsuarioModel> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(prestadorModel));
        assertTrue(resultado.contains(clienteModel));
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void deveRetornarListaVaziaQuandoNaoHaUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList());

        List<UsuarioModel> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("Deve deletar usuário existente com sucesso")
    void deveDeletarUsuarioExistenteComSucesso() {
        Long usuarioId = 1L;
        when(usuarioRepository.existsById(usuarioId)).thenReturn(true);

        GenericException exception = assertThrows(GenericException.class, () -> {
            usuarioService.deleteUsuario(usuarioId);
        });

        assertEquals("Usuario excluido com sucesso: " + usuarioId, exception.getMessage());
        verify(usuarioRepository).existsById(usuarioId);
        verify(usuarioRepository).deleteById(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar usuário inexistente")
    void deveLancarExcecaoAoTentarDeletarUsuarioInexistente() {
        Long usuarioId = 999L;
        when(usuarioRepository.existsById(usuarioId)).thenReturn(false);

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            usuarioService.deleteUsuario(usuarioId);
        });

        assertEquals("Usuario nao encontrado: " + usuarioId, exception.getMessage());
        verify(usuarioRepository).existsById(usuarioId);
        verify(usuarioRepository, never()).deleteById(usuarioId);
    }


    @Test
    @DisplayName("Deve lançar exceção ao buscar CEP inválido")
    void deveLancarExcecaoAoBuscarCepInvalido() {
        GenericException exception = assertThrows(GenericException.class, () -> {
            usuarioService.buscarCep("123");
        });

        assertEquals("CEP invalido 123", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar CEP nulo")
    void deveLancarExcecaoAoBuscarCepNulo() {
        GenericException exception = assertThrows(GenericException.class, () -> {
            usuarioService.buscarCep(null);
        });

        assertEquals("CEP invalido null", exception.getMessage());
    }


    @Test
    @DisplayName("Deve atualizar atributos do CEP corretamente")
    void deveAtualizarAtributosDoCepCorretamente() {
        UsuarioModel usuario = new ClienteModel();

        usuarioService.atualizaAtributosCep(usuario, viaCepDto);

        assertEquals("Rua das Flores", usuario.getEndereco());
        assertEquals("12345-678", usuario.getCep());
        assertEquals("São Paulo", usuario.getCidade());
        assertEquals("SP", usuario.getEstado());
    }

    @Test
    @DisplayName("Deve validar email válido com sucesso")
    void deveValidarEmailValidoComSucesso() {
        when(usuarioRepository.existsUsuarioByEmail("novo@email.com")).thenReturn(false);

        assertDoesNotThrow(() -> {
            usuarioService.validarEmail("novo@email.com");
        });

        verify(usuarioRepository).existsUsuarioByEmail("novo@email.com");
    }

    @Test
    @DisplayName("Deve lançar exceção para email sem @")
    void deveLancarExcecaoParaEmailSemArroba() {
        GenericException exception = assertThrows(GenericException.class, () -> {
            usuarioService.validarEmail("emailinvalido");
        });

        assertEquals("Email inválido: emailinvalido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para email nulo")
    void deveLancarExcecaoParaEmailNulo() {
        GenericException exception = assertThrows(GenericException.class, () -> {
            usuarioService.validarEmail(null);
        });

        assertEquals("Email inválido: null", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para email já cadastrado")
    void deveLancarExcecaoParaEmailJaCadastrado() {
        when(usuarioRepository.existsUsuarioByEmail("existente@email.com")).thenReturn(true);

        GenericException exception = assertThrows(GenericException.class, () -> {
            usuarioService.validarEmail("existente@email.com");
        });

        assertEquals("Email já cadastrado: existente@email.com", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve validar email ao atualizar prestador com mesmo email")
    void naoDeveValidarEmailAoAtualizarPrestadorComMesmoEmail() {
        UsuarioRequestDtoPrestador dtoMesmoEmail = new UsuarioRequestDtoPrestador(
                "newPassword123",
                "ENCANADOR",
                "João Silva Updated",
                "joao@email.com",
                "87654-321"
        );

        UsuarioService usuarioServiceSpy = spy(usuarioService);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(prestadorModel));
        doNothing().when(validators).forEach(any());
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(usuarioRepository.save(any(PrestadorModel.class))).thenReturn(prestadorModel);
        doReturn(viaCepDto).when(usuarioServiceSpy).buscarCep("87654-321");

        UsuarioModel resultado = usuarioServiceSpy.atualizarUsuarioPrestador(1L, dtoMesmoEmail);

        assertNotNull(resultado);
        verify(usuarioRepository, never()).existsUsuarioByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Não deve validar email ao atualizar cliente com mesmo email")
    void naoDeveValidarEmailAoAtualizarClienteComMesmoEmail() {
        UsuarioRequestDtoCliente dtoMesmoEmail = new UsuarioRequestDtoCliente(
                "newPassword123",
                "Maria Santos Updated",
                "maria@email.com",
                "87654-321"
        );

        UsuarioService usuarioServiceSpy = spy(usuarioService);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(clienteModel));
        doNothing().when(validators).forEach(any());
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(usuarioRepository.save(any(ClienteModel.class))).thenReturn(clienteModel);
        doReturn(viaCepDto).when(usuarioServiceSpy).buscarCep("87654-321");

        UsuarioModel resultado = usuarioServiceSpy.atualizarUsuarioCliente(2L, dtoMesmoEmail);

        assertNotNull(resultado);
        verify(usuarioRepository, never()).existsUsuarioByEmail("maria@email.com");
    }

    @Test
    @DisplayName("Não deve buscar CEP ao atualizar prestador com mesmo CEP")
    void naoDeveBuscarCepAoAtualizarPrestadorComMesmoCep() {
        UsuarioRequestDtoPrestador dtoMesmoCep = new UsuarioRequestDtoPrestador(
                "newPassword123",
                "ENCANADOR",
                "João Silva Updated",
                "joao.updated@email.com",
                "12345-678"
        );

        UsuarioService usuarioServiceSpy = spy(usuarioService);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(prestadorModel));
        when(usuarioRepository.existsUsuarioByEmail("joao.updated@email.com")).thenReturn(false);
        doNothing().when(validators).forEach(any());
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(usuarioRepository.save(any(PrestadorModel.class))).thenReturn(prestadorModel);

        UsuarioModel resultado = usuarioServiceSpy.atualizarUsuarioPrestador(1L, dtoMesmoCep);

        assertNotNull(resultado);
        verify(usuarioServiceSpy, never()).buscarCep(anyString());
    }

    @Test
    @DisplayName("Não deve buscar CEP ao atualizar cliente com mesmo CEP")
    void naoDeveBuscarCepAoAtualizarClienteComMesmoCep() {
        UsuarioRequestDtoCliente dtoMesmoCep = new UsuarioRequestDtoCliente(
                "newPassword123",
                "Maria Santos Updated",
                "maria.updated@email.com",
                "12345-678"
        );

        UsuarioService usuarioServiceSpy = spy(usuarioService);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(clienteModel));
        when(usuarioRepository.existsUsuarioByEmail("maria.updated@email.com")).thenReturn(false);
        doNothing().when(validators).forEach(any());
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(usuarioRepository.save(any(ClienteModel.class))).thenReturn(clienteModel);

        UsuarioModel resultado = usuarioServiceSpy.atualizarUsuarioCliente(2L, dtoMesmoCep);

        assertNotNull(resultado);
        verify(usuarioServiceSpy, never()).buscarCep(anyString());
    }
}
