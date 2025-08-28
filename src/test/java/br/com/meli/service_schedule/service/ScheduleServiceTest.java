package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.dto.ScheduleRequestDto;
import br.com.meli.service_schedule.dto.ScheduleResponseDto;
import br.com.meli.service_schedule.dto.ScheduleUpdateDto;
import br.com.meli.service_schedule.exception.ConflictException;
import br.com.meli.service_schedule.exception.EntidadeNaoEncontradaException;
import br.com.meli.service_schedule.exception.GenericException;
import br.com.meli.service_schedule.model.*;
import br.com.meli.service_schedule.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AgendaPrestadorRepository agendaPrestadorRepository;

    @Mock
    private PrestadorRepository prestadorRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ScheduleService scheduleService;

    private ScheduleRequestDto scheduleRequestDto;
    private ScheduleUpdateDto scheduleUpdateDto;
    private ServicoModel servicoModel;
    private ClienteModel clienteModel;
    private PrestadorModel prestadorModel;
    private AgendaPrestadorModel agendaPrestadorModel;
    private ScheduleModel scheduleModel;
    private LocalDateTime dataHoraFutura;

    @BeforeEach
    void setUp() {
        dataHoraFutura = LocalDateTime.now().plusDays(1);

        scheduleRequestDto = new ScheduleRequestDto(1L, 2L, 3L);
        scheduleUpdateDto = new ScheduleUpdateDto(2L, 4L);

        servicoModel = new ServicoModel();
        servicoModel.setId(1L);
        servicoModel.setNome("Instalação Elétrica");
        servicoModel.setDescricao("Instalação de sistema elétrico");

        clienteModel = new ClienteModel();
        clienteModel.setId(2L);
        clienteModel.setNome("João Cliente");
        clienteModel.setEmail("joao@email.com");
        clienteModel.setEndereco("Rua A, 123");

        prestadorModel = new PrestadorModel();
        prestadorModel.setId(3L);
        prestadorModel.setNome("Maria Prestadora");
        prestadorModel.setEmail("maria@email.com");
        prestadorModel.setAtividadePrest(Atividades.ELETRECISTA);

        agendaPrestadorModel = new AgendaPrestadorModel();
        agendaPrestadorModel.setId(3L);
        agendaPrestadorModel.setDataHoraDisponivel(dataHoraFutura);
        agendaPrestadorModel.setStatus(AgendaStatus.DISPONIVEL);
        agendaPrestadorModel.setPrestadorModel(prestadorModel);

        scheduleModel = new ScheduleModel();
        scheduleModel.setId(1L);
        scheduleModel.setServicoModel(servicoModel);
        scheduleModel.setClienteModel(clienteModel);
        scheduleModel.setPrestadorModel(prestadorModel);
        scheduleModel.setAgendaPrestadorModel(agendaPrestadorModel);
        scheduleModel.setDataHora(dataHoraFutura);
        scheduleModel.setStatus(ScheduleStatus.PENDENTE);
        scheduleModel.setCriadoEm(LocalDateTime.now());
        scheduleModel.setAtualizadoEm(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve cadastrar schedule com sucesso")
    void deveCadastrarScheduleComSucesso() {
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servicoModel));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(clienteModel));
        when(agendaPrestadorRepository.findById(3L)).thenReturn(Optional.of(agendaPrestadorModel));
        when(prestadorRepository.findPrestadorById(3L)).thenReturn(prestadorModel);
        when(scheduleRepository.save(any(ScheduleModel.class))).thenAnswer(invocation -> {
            ScheduleModel savedSchedule = invocation.getArgument(0);
            savedSchedule.setId(1L);
            return savedSchedule;
        });
        when(agendaPrestadorRepository.save(any(AgendaPrestadorModel.class))).thenReturn(agendaPrestadorModel);

        ScheduleResponseDto resultado = scheduleService.cadastrarschedule(scheduleRequestDto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Instalação Elétrica", resultado.servicoNome());
        assertEquals("João Cliente", resultado.clienteNome());
        assertEquals("Maria Prestadora", resultado.prestadorNome());
        assertEquals("PENDENTE", resultado.status());

        verify(scheduleRepository).save(any(ScheduleModel.class));
        verify(agendaPrestadorRepository).save(any(AgendaPrestadorModel.class));
        verify(eventPublisher).publishEvent(any(EmailScheduleEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar schedule com serviço inexistente")
    void deveLancarExcecaoAoCadastrarScheduleComServicoInexistente() {
        when(servicoRepository.findById(1L)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            scheduleService.cadastrarschedule(scheduleRequestDto);
        });

        assertEquals("Servico não encontrado", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar schedule com cliente inexistente")
    void deveLancarExcecaoAoCadastrarScheduleComClienteInexistente() {
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servicoModel));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            scheduleService.cadastrarschedule(scheduleRequestDto);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar schedule com usuário que não é cliente")
    void deveLancarExcecaoAoCadastrarScheduleComUsuarioQueNaoECliente() {
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servicoModel));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(prestadorModel));

        GenericException exception = assertThrows(GenericException.class, () -> {
            scheduleService.cadastrarschedule(scheduleRequestDto);
        });

        assertEquals("Usuario informado não é um cliente", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar schedule com agenda inexistente")
    void deveLancarExcecaoAoCadastrarScheduleComAgendaInexistente() {
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servicoModel));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(clienteModel));
        when(agendaPrestadorRepository.findById(3L)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            scheduleService.cadastrarschedule(scheduleRequestDto);
        });

        assertEquals("Agenda não encontrada: 3", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar schedule com agenda não disponível")
    void deveLancarExcecaoAoCadastrarScheduleComAgendaNaoDisponivel() {
        agendaPrestadorModel.setStatus(AgendaStatus.RESERVADO);

        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servicoModel));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(clienteModel));
        when(agendaPrestadorRepository.findById(3L)).thenReturn(Optional.of(agendaPrestadorModel));

        GenericException exception = assertThrows(GenericException.class, () -> {
            scheduleService.cadastrarschedule(scheduleRequestDto);
        });

        assertTrue(exception.getMessage().contains("não está disponível"));
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar schedule com agenda no passado")
    void deveLancarExcecaoAoCadastrarScheduleComAgendaNoPassado() {
        agendaPrestadorModel.setDataHoraDisponivel(LocalDateTime.now().minusHours(1));

        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servicoModel));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(clienteModel));
        when(agendaPrestadorRepository.findById(3L)).thenReturn(Optional.of(agendaPrestadorModel));

        GenericException exception = assertThrows(GenericException.class, () -> {
            scheduleService.cadastrarschedule(scheduleRequestDto);
        });

        assertTrue(exception.getMessage().contains("está com data/hora no passado"));
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar schedule com sucesso")
    void deveAtualizarScheduleComSucesso() {
        ServicoModel novoServico = new ServicoModel();
        novoServico.setId(2L);
        novoServico.setNome("Novo Serviço");

        AgendaPrestadorModel novaAgenda = new AgendaPrestadorModel();
        novaAgenda.setId(4L);
        novaAgenda.setDataHoraDisponivel(dataHoraFutura.plusDays(1));
        novaAgenda.setStatus(AgendaStatus.DISPONIVEL);
        novaAgenda.setPrestadorModel(prestadorModel);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(scheduleModel));
        when(servicoRepository.findById(2L)).thenReturn(Optional.of(novoServico));
        when(agendaPrestadorRepository.findById(4L)).thenReturn(Optional.of(novaAgenda));
        when(scheduleRepository.save(any(ScheduleModel.class))).thenReturn(scheduleModel);
        when(agendaPrestadorRepository.save(any(AgendaPrestadorModel.class))).thenReturn(agendaPrestadorModel);

        ScheduleResponseDto resultado = scheduleService.atualizarschedule(1L, scheduleUpdateDto);

        assertNotNull(resultado);
        verify(scheduleRepository).save(any(ScheduleModel.class));
        verify(agendaPrestadorRepository, times(2)).save(any(AgendaPrestadorModel.class));
        verify(eventPublisher).publishEvent(any(EmailScheduleEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar schedule inexistente")
    void deveLancarExcecaoAoAtualizarScheduleInexistente() {
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            scheduleService.atualizarschedule(999L, scheduleUpdateDto);
        });

        assertEquals("id do agendamento não encontrado", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar schedule com status inválido")
    void deveLancarExcecaoAoAtualizarScheduleComStatusInvalido() {
        scheduleModel.setStatus(ScheduleStatus.FINALIZADO);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(scheduleModel));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            scheduleService.atualizarschedule(1L, scheduleUpdateDto);
        });

        assertEquals("Apenas schedules pendentes/aceitos ou Rejeitados podem ser reagendados", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar schedule com dados nulos")
    void deveLancarExcecaoAoAtualizarScheduleComDadosNulos() {
        ScheduleUpdateDto dtoNulo = new ScheduleUpdateDto(null, null);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(scheduleModel));

        GenericException exception = assertThrows(GenericException.class, () -> {
            scheduleService.atualizarschedule(1L, dtoNulo);
        });

        assertEquals("Servico ou Agenda não podem ser nulos", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar schedule sem alterações")
    void deveLancarExcecaoAoAtualizarScheduleSemAlteracoes() {
        ScheduleUpdateDto dtoSemAlteracao = new ScheduleUpdateDto(1L, 3L);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(scheduleModel));

        GenericException exception = assertThrows(GenericException.class, () -> {
            scheduleService.atualizarschedule(1L, dtoSemAlteracao);
        });

        assertEquals("Nenhum dado foi alterado, não é necessário atualizar o agendamento", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar todos os schedules")
    void deveListarTodosOsSchedules() {
        List<ScheduleModel> schedules = Arrays.asList(scheduleModel);
        when(scheduleRepository.findAll()).thenReturn(schedules);

        List<ScheduleResponseDto> resultado = scheduleService.listarSchedules();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Maria Prestadora", resultado.get(0).clienteNome());
        verify(scheduleRepository).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há schedules")
    void deveRetornarListaVaziaQuandoNaoHaSchedules() {
        when(scheduleRepository.findAll()).thenReturn(Arrays.asList());

        List<ScheduleResponseDto> resultado = scheduleService.listarSchedules();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(scheduleRepository).findAll();
    }

    @Test
    @DisplayName("Deve listar schedules por prestador")
    void deveListarSchedulesPorPrestador() {
        List<PrestadorModel> prestadores = Arrays.asList(prestadorModel);
        List<ScheduleModel> schedules = Arrays.asList(scheduleModel);

        when(prestadorRepository.findByNome("Maria Prestadora")).thenReturn(prestadores);
        when(scheduleRepository.findByPrestadorModel(prestadorModel)).thenReturn(schedules);

        List<ScheduleModel> resultado = scheduleService.listarSchedulesPorPrestador("Maria Prestadora");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(scheduleModel, resultado.get(0));
        verify(prestadorRepository).findByNome("Maria Prestadora");
        verify(scheduleRepository).findByPrestadorModel(prestadorModel);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar schedules de prestador inexistente")
    void deveLancarExcecaoAoListarSchedulesDePrestadorInexistente() {
        when(prestadorRepository.findByNome("Prestador Inexistente")).thenReturn(Arrays.asList());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            scheduleService.listarSchedulesPorPrestador("Prestador Inexistente");
        });

        assertEquals("Prestador não encontrado: Prestador Inexistente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando prestador não tem agendamentos")
    void deveLancarExcecaoQuandoPrestadorNaoTemAgendamentos() {
        List<PrestadorModel> prestadores = Arrays.asList(prestadorModel);

        when(prestadorRepository.findByNome("Maria Prestadora")).thenReturn(prestadores);
        when(scheduleRepository.findByPrestadorModel(prestadorModel)).thenReturn(Arrays.asList());

        GenericException exception = assertThrows(GenericException.class, () -> {
            scheduleService.listarSchedulesPorPrestador("Maria Prestadora");
        });

        assertEquals("Nenhum agendamento encontrado para o prestador: Maria Prestadora", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cancelar schedule com sucesso")
    void deveCancelarScheduleComSucesso() {
        scheduleModel.setDataHora(LocalDateTime.now().plusHours(3));
        agendaPrestadorModel.setDataHoraDisponivel(LocalDateTime.now().plusHours(5));

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(scheduleModel));
        when(agendaPrestadorRepository.findAgendaPrestadorModelsById(3L)).thenReturn(agendaPrestadorModel);
        when(scheduleRepository.save(any(ScheduleModel.class))).thenReturn(scheduleModel);
        when(agendaPrestadorRepository.save(any(AgendaPrestadorModel.class))).thenReturn(agendaPrestadorModel);

        ScheduleResponseDto resultado = scheduleService.cancelarSchedule(1L, "Motivo válido");

        assertNotNull(resultado);
        assertEquals("CANCELADO", resultado.status());
        verify(scheduleRepository).save(any(ScheduleModel.class));
        verify(agendaPrestadorRepository).save(any(AgendaPrestadorModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar schedule com motivo inválido")
    void deveLancarExcecaoAoCancelarScheduleComMotivoInvalido() {
        GenericException exception = assertThrows(GenericException.class, () -> {
            scheduleService.cancelarSchedule(1L, "");
        });

        assertEquals("Motivo de cancelamento invalido", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar schedule inexistente")
    void deveLancarExcecaoAoCancelarScheduleInexistente() {
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            scheduleService.cancelarSchedule(999L, "Motivo válido");
        });

        assertEquals("id do agendamento não encontrado", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar schedule com status inválido")
    void deveLancarExcecaoAoCancelarScheduleComStatusInvalido() {
        scheduleModel.setStatus(ScheduleStatus.FINALIZADO);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(scheduleModel));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            scheduleService.cancelarSchedule(1L, "Motivo válido");
        });

        assertEquals("Apenas schedules pendentes ou aceito podem ser cancelados", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar schedule muito próximo do horário")
    void deveLancarExcecaoAoCancelarScheduleMuitoProximoDoHorario() {
        scheduleModel.setDataHora(LocalDateTime.now().plusMinutes(30));

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(scheduleModel));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            scheduleService.cancelarSchedule(1L, "Motivo válido");
        });

        assertTrue(exception.getMessage().contains("Não é possível cancelar um agendamento que está a menos de 2 horas"));
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve aceitar schedule com sucesso")
    void deveAceitarScheduleComSucesso() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(scheduleModel));
        when(agendaPrestadorRepository.findAgendaPrestadorModelsById(3L)).thenReturn(agendaPrestadorModel);
        when(scheduleRepository.save(any(ScheduleModel.class))).thenReturn(scheduleModel);
        when(agendaPrestadorRepository.save(any(AgendaPrestadorModel.class))).thenReturn(agendaPrestadorModel);

        ScheduleResponseDto resultado = scheduleService.aceitarSchedule(1L);

        assertNotNull(resultado);
        assertEquals("ACEITO", resultado.status());
        verify(scheduleRepository).save(any(ScheduleModel.class));
        verify(agendaPrestadorRepository).save(any(AgendaPrestadorModel.class));
        verify(emailService).enviarEmailClienteSchedule(anyString(), anyString(), anyString(), anyString(), anyString(), any(LocalDateTime.class), eq(true));
    }

    @Test
    @DisplayName("Deve lançar exceção ao aceitar schedule inexistente")
    void deveLancarExcecaoAoAceitarScheduleInexistente() {
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            scheduleService.aceitarSchedule(999L);
        });

        assertEquals("id do agendamento não encontrado", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve recusar schedule com sucesso")
    void deveRecusarScheduleComSucesso() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(scheduleModel));
        when(agendaPrestadorRepository.findAgendaPrestadorModelsById(3L)).thenReturn(agendaPrestadorModel);
        when(scheduleRepository.save(any(ScheduleModel.class))).thenReturn(scheduleModel);
        when(agendaPrestadorRepository.save(any(AgendaPrestadorModel.class))).thenReturn(agendaPrestadorModel);

        ScheduleResponseDto resultado = scheduleService.recusarSchedule(1L);

        assertNotNull(resultado);
        assertEquals("REJEITADO", resultado.status());
        verify(scheduleRepository).save(any(ScheduleModel.class));
        verify(agendaPrestadorRepository).save(any(AgendaPrestadorModel.class));
        verify(emailService).enviarEmailClienteSchedule(anyString(), anyString(), anyString(), anyString(), anyString(), any(LocalDateTime.class), eq(false));
    }

    @Test
    @DisplayName("Deve lançar exceção ao recusar schedule inexistente")
    void deveLancarExcecaoAoRecusarScheduleInexistente() {
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            scheduleService.recusarSchedule(999L);
        });

        assertEquals("id do agendamento não encontrado", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve concluir schedule com sucesso")
    void deveConcluirScheduleComSucesso() {
        scheduleModel.setStatus(ScheduleStatus.ACEITO);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(scheduleModel));
        when(agendaPrestadorRepository.findAgendaPrestadorModelsById(3L)).thenReturn(agendaPrestadorModel);
        when(scheduleRepository.save(any(ScheduleModel.class))).thenReturn(scheduleModel);
        when(agendaPrestadorRepository.save(any(AgendaPrestadorModel.class))).thenReturn(agendaPrestadorModel);

        ScheduleResponseDto resultado = scheduleService.concluirSchedule(1L);

        assertNotNull(resultado);
        assertEquals("FINALIZADO", resultado.status());
        verify(scheduleRepository).save(any(ScheduleModel.class));
        verify(agendaPrestadorRepository).save(any(AgendaPrestadorModel.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao concluir schedule inexistente")
    void deveLancarExcecaoAoConcluirScheduleInexistente() {
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            scheduleService.concluirSchedule(999L);
        });

        assertEquals("id do agendamento não encontrado", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao concluir schedule não aceito")
    void deveLancarExcecaoAoConcluirScheduleNaoAceito() {
        scheduleModel.setStatus(ScheduleStatus.PENDENTE);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(scheduleModel));

        ConflictException exception = assertThrows(ConflictException.class, () -> {
            scheduleService.concluirSchedule(1L);
        });

        assertEquals("Apenas schedules aceitos podem ser finalizados", exception.getMessage());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar agenda prestador com sucesso")
    void deveBuscarAgendaPrestadorComSucesso() {
        when(agendaPrestadorRepository.findById(3L)).thenReturn(Optional.of(agendaPrestadorModel));

        AgendaPrestadorModel resultado = scheduleService.buscarAgendaPrestador(3L);

        assertNotNull(resultado);
        assertEquals(agendaPrestadorModel, resultado);
        verify(agendaPrestadorRepository).findById(3L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar agenda prestador inexistente")
    void deveLancarExcecaoAoBuscarAgendaPrestadorInexistente() {
        when(agendaPrestadorRepository.findById(999L)).thenReturn(Optional.empty());

        EntidadeNaoEncontradaException exception = assertThrows(EntidadeNaoEncontradaException.class, () -> {
            scheduleService.buscarAgendaPrestador(999L);
        });

        assertEquals("Agenda não encontrada: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Deve definir agenda como cancelada quando cancelamento próximo do horário")
    void deveDefinirAgendaComoCanceladaQuandoCancelamentoProximoDoHorario() {
        scheduleModel.setDataHora(LocalDateTime.now().plusHours(3));
        agendaPrestadorModel.setDataHoraDisponivel(LocalDateTime.now().plusHours(3));

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(scheduleModel));
        when(agendaPrestadorRepository.findAgendaPrestadorModelsById(3L)).thenReturn(agendaPrestadorModel);
        when(scheduleRepository.save(any(ScheduleModel.class))).thenReturn(scheduleModel);
        when(agendaPrestadorRepository.save(any(AgendaPrestadorModel.class))).thenReturn(agendaPrestadorModel);

        scheduleService.cancelarSchedule(1L, "Motivo válido");

        verify(agendaPrestadorRepository).save(argThat(agenda ->
                agenda.getStatus() == AgendaStatus.CANCELADO
        ));
    }
}
