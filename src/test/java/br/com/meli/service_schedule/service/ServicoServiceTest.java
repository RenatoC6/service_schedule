package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.ServicoRequestDto;
import br.com.meli.service_schedule.exception.EntidadeNaoEncontradaException;
import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.model.ServicoModel;
import br.com.meli.service_schedule.repository.ServicoRepository;
import br.com.meli.service_schedule.validator.servico.ServicoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ServicoService")
class ServicoServiceTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private List<ServicoValidator> servicoValidators;

    @Mock
    private ServicoValidator servicoValidator;

    @InjectMocks
    private ServicoService servicoService;

    private ServicoRequestDto servicoRequestDto;
    private ServicoModel servicoModel;

    @BeforeEach
    void setUp() {
        servicoRequestDto = new ServicoRequestDto("Limpeza Residencial", "Limpeza completa da casa", 150.0);
        
        servicoModel = new ServicoModel();
        servicoModel.setId(1L);
        servicoModel.setNome("Limpeza Residencial");
        servicoModel.setDescricao("Limpeza completa da casa");
        servicoModel.setPreco(150.0);
        servicoModel.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve cadastrar serviço com sucesso")
    void deveCadastrarServicoComSucesso() {
        when(servicoRepository.existsByNome("LIMPEZA RESIDENCIAL")).thenReturn(false);
        doNothing().when(servicoValidators).forEach(any());
        when(servicoRepository.save(any(ServicoModel.class))).thenReturn(servicoModel);

        ServicoModel resultado = servicoService.cadastrarServico(servicoRequestDto);

        assertNotNull(resultado);
        assertEquals("Limpeza Residencial", resultado.getNome());
        assertEquals("Limpeza completa da casa", resultado.getDescricao());
        assertEquals(150.0, resultado.getPreco());
        
        verify(servicoRepository).existsByNome("LIMPEZA RESIDENCIAL");
        verify(servicoValidators).forEach(any());
        verify(servicoRepository).save(any(ServicoModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar serviço com nome já existente")
    void deveLancarExcecaoAoTentarCadastrarServicoComNomeJaExistente() {
        when(servicoRepository.existsByNome("LIMPEZA RESIDENCIAL")).thenReturn(true);

        GenericException exception = assertThrows(GenericException.class, () -> {
            servicoService.cadastrarServico(servicoRequestDto);
        });

        assertEquals("Já existe um serviço cadastrado com o nome: Limpeza Residencial", exception.getMessage());
        verify(servicoRepository).existsByNome("LIMPEZA RESIDENCIAL");
        verify(servicoValidators, never()).forEach(any());
        verify(servicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve executar validações ao cadastrar serviço")
    void deveExecutarValidacoesAoCadastrarServico() {
        when(servicoRepository.existsByNome("LIMPEZA RESIDENCIAL")).thenReturn(false);
        doNothing().when(servicoValidators).forEach(any());
        when(servicoRepository.save(any(ServicoModel.class))).thenReturn(servicoModel);

        servicoService.cadastrarServico(servicoRequestDto);

        verify(servicoValidators).forEach(any());
    }

    @Test
    @DisplayName("Deve listar todos os serviços")
    void deveListarTodosOsServicos() {
        List<ServicoModel> servicos = Arrays.asList(servicoModel);
        when(servicoRepository.findAll()).thenReturn(servicos);

        List<ServicoModel> resultado = servicoService.listarServicos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(servicoModel, resultado.get(0));
        
        verify(servicoRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há serviços")
    void deveRetornarListaVaziaQuandoNaoHaServicos() {
        when(servicoRepository.findAll()).thenReturn(Arrays.asList());

        List<ServicoModel> resultado = servicoService.listarServicos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve deletar serviço existente com sucesso")
    void deveDeletarServicoExistenteComSucesso() {
        Long servicoId = 1L;
        when(servicoRepository.existsById(servicoId)).thenReturn(true);

        GenericException exception = assertThrows(GenericException.class, () -> {
            servicoService.deletarServico(servicoId);
        });

        assertEquals("Servico deletado com sucesso: " + servicoId, exception.getMessage());
        verify(servicoRepository).existsById(servicoId);
        verify(servicoRepository).deleteById(servicoId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar serviço inexistente")
    void deveLancarExcecaoAoTentarDeletarServicoInexistente() {
        Long servicoId = 999L;
        when(servicoRepository.existsById(servicoId)).thenReturn(false);

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            servicoService.deletarServico(servicoId);
        });

        assertEquals("Servico não encontrado: " + servicoId, exception.getMessage());
        verify(servicoRepository).existsById(servicoId);
        verify(servicoRepository, never()).deleteById(servicoId);
    }

    @Test
    @DisplayName("Deve atualizar serviço com sucesso")
    void deveAtualizarServicoComSucesso() {
        Long servicoId = 1L;
        ServicoRequestDto novoDto = new ServicoRequestDto("Limpeza Residencial", "Nova descrição", 200.0);
        
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servicoModel));
        doNothing().when(servicoValidators).forEach(any());
        when(servicoRepository.save(any(ServicoModel.class))).thenReturn(servicoModel);

        ServicoModel resultado = servicoService.atualizarServico(servicoId, novoDto);

        assertNotNull(resultado);
        verify(servicoRepository).findById(servicoId);
        verify(servicoValidators).forEach(any());
        verify(servicoRepository).save(any(ServicoModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar serviço inexistente")
    void deveLancarExcecaoAoTentarAtualizarServicoInexistente() {
        Long servicoId = 999L;
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            servicoService.atualizarServico(servicoId, servicoRequestDto);
        });

        assertEquals("Servico não encontrado: " + servicoId, exception.getMessage());
        verify(servicoRepository).findById(servicoId);
        verify(servicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve verificar nome duplicado ao atualizar com nome diferente")
    void deveVerificarNomeDuplicadoAoAtualizarComNomeDiferente() {
        Long servicoId = 1L;
        ServicoRequestDto novoDto = new ServicoRequestDto("Novo Nome", "Nova descrição", 200.0);
        
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servicoModel));
        doNothing().when(servicoValidators).forEach(any());
        when(servicoRepository.existsByNome("NOVO NOME")).thenReturn(true);

        GenericException exception = assertThrows(GenericException.class, () -> {
            servicoService.atualizarServico(servicoId, novoDto);
        });

        assertEquals("Já existe um serviço cadastrado com o nome: Novo Nome", exception.getMessage());
        verify(servicoRepository).findById(servicoId);
        verify(servicoValidators).forEach(any());
        verify(servicoRepository).existsByNome("NOVO NOME");
        verify(servicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve verificar nome duplicado ao atualizar com mesmo nome")
    void naoDeveVerificarNomeDuplicadoAoAtualizarComMesmoNome() {
        Long servicoId = 1L;
        ServicoRequestDto novoDto = new ServicoRequestDto("Limpeza Residencial", "Nova descrição", 200.0);
        
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servicoModel));
        doNothing().when(servicoValidators).forEach(any());
        when(servicoRepository.save(any(ServicoModel.class))).thenReturn(servicoModel);

        servicoService.atualizarServico(servicoId, novoDto);

        verify(servicoRepository).findById(servicoId);
        verify(servicoValidators).forEach(any());
        verify(servicoRepository, never()).existsByNome(anyString());
        verify(servicoRepository).save(any(ServicoModel.class));
    }

    @Test
    @DisplayName("Deve atualizar serviço permitindo nome diferente quando não existe duplicata")
    void deveAtualizarServicoPermitindoNomeDiferenteQuandoNaoExisteDuplicata() {
        Long servicoId = 1L;
        ServicoRequestDto novoDto = new ServicoRequestDto("Novo Nome", "Nova descrição", 200.0);
        
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servicoModel));
        doNothing().when(servicoValidators).forEach(any());
        when(servicoRepository.existsByNome("NOVO NOME")).thenReturn(false);
        when(servicoRepository.save(any(ServicoModel.class))).thenReturn(servicoModel);

        ServicoModel resultado = servicoService.atualizarServico(servicoId, novoDto);

        assertNotNull(resultado);
        verify(servicoRepository).findById(servicoId);
        verify(servicoValidators).forEach(any());
        verify(servicoRepository).existsByNome("NOVO NOME");
        verify(servicoRepository).save(any(ServicoModel.class));
    }
}
