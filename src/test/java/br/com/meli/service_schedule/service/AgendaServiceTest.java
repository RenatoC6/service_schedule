package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.AgendaRequestDto;
import br.com.meli.service_schedule.exception.EntidadeNaoEncontradaException;
import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.model.AgendaPrestadorModel;
import br.com.meli.service_schedule.model.AgendaStatus;
import br.com.meli.service_schedule.model.PrestadorModel;
import br.com.meli.service_schedule.repository.AgendaPrestadorRepository;
import br.com.meli.service_schedule.repository.PrestadorRepository;
import br.com.meli.service_schedule.validator.agenda.AgendaValidator;
import br.com.meli.service_schedule.validator.prestador.PrestadorValidator;
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
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para AgendaService")
class AgendaServiceTest {

    @Mock
    private PrestadorRepository prestadorRepository;

    @Mock
    private AgendaPrestadorRepository agendaPrestadorRepository;

    @Mock
    private List<PrestadorValidator> prestadorValidators;

    @Mock
    private List<AgendaValidator> agendaValidators;

    @Mock
    private PrestadorValidator prestadorValidator;

    @Mock
    private AgendaValidator agendaValidator;

    @InjectMocks
    private AgendaService agendaService;

    private AgendaRequestDto agendaRequestDto;
    private PrestadorModel prestadorModel;
    private AgendaPrestadorModel agendaPrestadorModel;
    private LocalDateTime dataHoraFutura;

    @BeforeEach
    void setUp() {
        dataHoraFutura = LocalDateTime.now().plusDays(1);
        
        agendaRequestDto = new AgendaRequestDto(1L, dataHoraFutura);
        
        prestadorModel = new PrestadorModel();
        prestadorModel.setId(1L);
        prestadorModel.setNome("João Silva");
        
        agendaPrestadorModel = new AgendaPrestadorModel();
        agendaPrestadorModel.setId(1L);
        agendaPrestadorModel.setPrestadorModel(prestadorModel);
        agendaPrestadorModel.setDataHoraDisponivel(dataHoraFutura);
        agendaPrestadorModel.setStatus(AgendaStatus.DISPONIVEL);
    }

    @Test
    @DisplayName("Deve cadastrar agenda com sucesso")
    void deveCadastrarAgendaComSucesso() {
        doNothing().when(prestadorValidators).forEach(any());
        doNothing().when(agendaValidators).forEach(any());
        when(prestadorRepository.findPrestadorById(1L)).thenReturn(prestadorModel);
        when(agendaPrestadorRepository.save(any(AgendaPrestadorModel.class))).thenReturn(agendaPrestadorModel);

        AgendaPrestadorModel resultado = agendaService.cadastrarAgenda(agendaRequestDto);

        assertNotNull(resultado);
        assertEquals(AgendaStatus.DISPONIVEL, resultado.getStatus());
        assertEquals(dataHoraFutura, resultado.getDataHoraDisponivel());
        assertEquals(prestadorModel, resultado.getPrestadorModel());
        
        verify(prestadorRepository).findPrestadorById(1L);
        verify(agendaPrestadorRepository).save(any(AgendaPrestadorModel.class));
    }

    @Test
    @DisplayName("Deve executar validações ao cadastrar agenda")
    void deveExecutarValidacoesAoCadastrarAgenda() {
        doNothing().when(prestadorValidators).forEach(any());
        doNothing().when(agendaValidators).forEach(any());
        when(prestadorRepository.findPrestadorById(1L)).thenReturn(prestadorModel);
        when(agendaPrestadorRepository.save(any(AgendaPrestadorModel.class))).thenReturn(agendaPrestadorModel);

        agendaService.cadastrarAgenda(agendaRequestDto);

        verify(prestadorValidators).forEach(any());
        verify(agendaValidators).forEach(any());
    }

    @Test
    @DisplayName("Deve listar agendas disponíveis")
    void deveListarAgendasDisponiveis() {
        List<AgendaPrestadorModel> agendasDisponiveis = Arrays.asList(agendaPrestadorModel);
        when(agendaPrestadorRepository.findByStatusDisponivel("DISPONIVEL")).thenReturn(agendasDisponiveis);

        List<AgendaPrestadorModel> resultado = agendaService.listarAgendasDisponiveis();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(agendaPrestadorModel, resultado.get(0));
        
        verify(agendaPrestadorRepository).findByStatusDisponivel("DISPONIVEL");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há agendas disponíveis")
    void deveRetornarListaVaziaQuandoNaoHaAgendasDisponiveis() {
        when(agendaPrestadorRepository.findByStatusDisponivel("DISPONIVEL")).thenReturn(Arrays.asList());

        List<AgendaPrestadorModel> resultado = agendaService.listarAgendasDisponiveis();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve deletar agenda existente com sucesso")
    void deveDeletarAgendaExistenteComSucesso() {
        Long agendaId = 1L;
        when(agendaPrestadorRepository.existsById(agendaId)).thenReturn(true);

        GenericException exception = assertThrows(GenericException.class, () -> {
            agendaService.deleteAgenda(agendaId);
        });

        assertEquals("Agenda deletada com sucesso: " + agendaId, exception.getMessage());
        verify(agendaPrestadorRepository).existsById(agendaId);
        verify(agendaPrestadorRepository).deleteById(agendaId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar agenda inexistente")
    void deveLancarExcecaoAoTentarDeletarAgendaInexistente() {
        Long agendaId = 999L;
        when(agendaPrestadorRepository.existsById(agendaId)).thenReturn(false);

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            agendaService.deleteAgenda(agendaId);
        });

        assertEquals("Agenda não encontrada: " + agendaId, exception.getMessage());
        verify(agendaPrestadorRepository).existsById(agendaId);
        verify(agendaPrestadorRepository, never()).deleteById(agendaId);
    }

    @Test
    @DisplayName("Deve atualizar agenda com sucesso")
    void deveAtualizarAgendaComSucesso() {
        Long agendaId = 1L;
        LocalDateTime novaDataHora = LocalDateTime.now().plusDays(2);
        AgendaRequestDto novoDto = new AgendaRequestDto(1L, novaDataHora);
        
        when(agendaPrestadorRepository.findById(agendaId)).thenReturn(Optional.of(agendaPrestadorModel));
        doNothing().when(agendaValidators).forEach(any());
        when(prestadorRepository.findPrestadorById(1L)).thenReturn(prestadorModel);
        when(agendaPrestadorRepository.save(any(AgendaPrestadorModel.class))).thenReturn(agendaPrestadorModel);

        AgendaPrestadorModel resultado = agendaService.atualizarAgenda(agendaId, novoDto);

        assertNotNull(resultado);
        verify(agendaPrestadorRepository).findById(agendaId);
        verify(agendaPrestadorRepository).save(any(AgendaPrestadorModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar agenda inexistente")
    void deveLancarExcecaoAoTentarAtualizarAgendaInexistente() {
        Long agendaId = 999L;
        when(agendaPrestadorRepository.findById(agendaId)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            agendaService.atualizarAgenda(agendaId, agendaRequestDto);
        });

        assertEquals("Agenda não encontrada: " + agendaId, exception.getMessage());
        verify(agendaPrestadorRepository).findById(agendaId);
        verify(agendaPrestadorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar agenda com status diferente de DISPONIVEL")
    void deveLancarExcecaoAoTentarAtualizarAgendaComStatusIncorreto() {
        Long agendaId = 1L;
        agendaPrestadorModel.setStatus(AgendaStatus.RESERVADO);
        
        when(agendaPrestadorRepository.findById(agendaId)).thenReturn(Optional.of(agendaPrestadorModel));

        GenericException exception = assertThrows(GenericException.class, () -> {
            agendaService.atualizarAgenda(agendaId, agendaRequestDto);
        });

        assertTrue(exception.getMessage().contains("não está disponível para atualização"));
        verify(agendaPrestadorRepository).findById(agendaId);
        verify(agendaPrestadorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve executar validação de prestador ao alterar prestador na atualização")
    void deveExecutarValidacaoDePrestadorAoAlterarPrestadorNaAtualizacao() {
        Long agendaId = 1L;
        Long novoPrestadorId = 2L;
        AgendaRequestDto novoDto = new AgendaRequestDto(novoPrestadorId, dataHoraFutura);
        
        PrestadorModel novoPrestador = new PrestadorModel();
        novoPrestador.setId(novoPrestadorId);
        
        when(agendaPrestadorRepository.findById(agendaId)).thenReturn(Optional.of(agendaPrestadorModel));
        doNothing().when(prestadorValidators).forEach(any());
        doNothing().when(agendaValidators).forEach(any());
        when(prestadorRepository.findPrestadorById(novoPrestadorId)).thenReturn(novoPrestador);
        when(agendaPrestadorRepository.save(any(AgendaPrestadorModel.class))).thenReturn(agendaPrestadorModel);

        agendaService.atualizarAgenda(agendaId, novoDto);

        verify(prestadorValidators).forEach(any());
        verify(agendaValidators).forEach(any());
        verify(prestadorRepository).findPrestadorById(novoPrestadorId);
    }

    @Test
    @DisplayName("Não deve executar validação de prestador ao manter mesmo prestador na atualização")
    void naoDeveExecutarValidacaoDePrestadorAoManterMesmoPrestadorNaAtualizacao() {
        Long agendaId = 1L;
        
        when(agendaPrestadorRepository.findById(agendaId)).thenReturn(Optional.of(agendaPrestadorModel));
        doNothing().when(agendaValidators).forEach(any());
        when(prestadorRepository.findPrestadorById(1L)).thenReturn(prestadorModel);
        when(agendaPrestadorRepository.save(any(AgendaPrestadorModel.class))).thenReturn(agendaPrestadorModel);

        agendaService.atualizarAgenda(agendaId, agendaRequestDto);

        verify(agendaValidators).forEach(any());
        verify(prestadorRepository).findPrestadorById(1L);
    }
}
