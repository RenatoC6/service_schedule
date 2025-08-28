package br.com.meli.service_schedule.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private LocalDateTime dataServico;

    @BeforeEach
    void setUp() {
        dataServico = LocalDateTime.of(2024, 12, 25, 14, 30);
    }

    @Test
    @DisplayName("Deve enviar email de aceite de schedule com sucesso")
    void deveEnviarEmailAceiteScheduleComSucesso() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailAceiteSchedule(
                "prestador@email.com",
                "Maria Prestadora",
                1L,
                "Limpeza Residencial",
                "Limpeza completa da casa",
                "João Cliente",
                "Rua das Flores, 123",
                dataServico
        );

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email de aceite com dados corretos")
    void deveEnviarEmailAceiteComDadosCorretos() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailAceiteSchedule(
                "prestador@email.com",
                "Maria Prestadora",
                123L,
                "Jardinagem",
                "Poda de árvores e plantas",
                "Ana Cliente",
                "Av. Principal, 456",
                dataServico
        );

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar envio de email de aceite")
    void deveLancarExcecaoAoFalharEnvioEmailAceite() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Erro de conexão")).when(javaMailSender).send(mimeMessage);

        MailSendException exception = assertThrows(MailSendException.class, () -> {
            emailService.enviarEmailAceiteSchedule(
                    "prestador@email.com",
                    "Maria Prestadora",
                    1L,
                    "Limpeza",
                    "Descrição",
                    "Cliente",
                    "Endereço",
                    dataServico
            );
        });

        assertEquals("Erro de conexão", exception.getMessage());
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email para cliente com agendamento aceito")
    void deveEnviarEmailClienteAgendamentoAceito() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailClienteSchedule(
                "cliente@email.com",
                "João Cliente",
                "Maria Prestadora",
                "Limpeza Residencial",
                "Limpeza completa da casa",
                dataServico,
                true
        );

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email para cliente com agendamento recusado")
    void deveEnviarEmailClienteAgendamentoRecusado() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailClienteSchedule(
                "cliente@email.com",
                "João Cliente",
                "Maria Prestadora",
                "Jardinagem",
                "Poda de plantas",
                dataServico,
                false
        );

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar envio de email para cliente")
    void deveLancarExcecaoAoFalharEnvioEmailCliente() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Falha na autenticação")).when(javaMailSender).send(mimeMessage);

        MailSendException exception = assertThrows(MailSendException.class, () -> {
            emailService.enviarEmailClienteSchedule(
                    "cliente@email.com",
                    "João Cliente",
                    "Maria Prestadora",
                    "Serviço",
                    "Descrição",
                    dataServico,
                    true
            );
        });

        assertEquals("Falha na autenticação", exception.getMessage());
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve processar corretamente diferentes tipos de serviços no email de aceite")
    void deveProcessarCorretamenteDiferentesTiposServicosEmailAceite() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailAceiteSchedule(
                "prestador@email.com",
                "Carlos Prestador",
                999L,
                "Consultoria Técnica",
                "Análise de sistemas e infraestrutura de TI",
                "Empresa XYZ",
                "Centro Empresarial, Sala 1001",
                dataServico
        );

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve processar corretamente diferentes datas no email")
    void deveProcessarCorretamenteDiferentesDataNoEmail() throws MessagingException {
        LocalDateTime dataFutura = LocalDateTime.of(2025, 6, 15, 9, 0);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailAceiteSchedule(
                "prestador@email.com",
                "Ana Prestadora",
                50L,
                "Aula de Música",
                "Aula particular de piano",
                "Pedro Cliente",
                "Rua da Música, 789",
                dataFutura
        );

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve processar email cliente com nomes especiais")
    void deveProcessarEmailClienteComNomesEspeciais() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailClienteSchedule(
                "cliente.especial@email.com",
                "José da Silva Santos",
                "Maria José Oliveira",
                "Serviço de Beleza",
                "Corte e escova profissional",
                dataServico,
                true
        );

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve processar email cliente recusado com dados completos")
    void deveProcessarEmailClienteRecusadoComDadosCompletos() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailClienteSchedule(
                "cliente.teste@email.com",
                "Roberto Cliente",
                "Sandra Prestadora",
                "Manutenção Elétrica",
                "Instalação de tomadas e interruptores",
                dataServico,
                false
        );

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve criar MimeMessage para cada envio de email")
    void deveCriarMimeMessageParaCadaEnvioEmail() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailAceiteSchedule(
                "prestador1@email.com",
                "Prestador 1",
                1L,
                "Serviço 1",
                "Descrição 1",
                "Cliente 1",
                "Endereço 1",
                dataServico
        );

        emailService.enviarEmailClienteSchedule(
                "cliente1@email.com",
                "Cliente 1",
                "Prestador 1",
                "Serviço 1",
                "Descrição 1",
                dataServico,
                true
        );

        verify(javaMailSender, times(2)).createMimeMessage();
        verify(javaMailSender, times(2)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve processar email de aceite com ID de schedule muito grande")
    void deveProcessarEmailAceiteComIdScheduleMuitoGrande() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailAceiteSchedule(
                "prestador@email.com",
                "Prestador Teste",
                999999999L,
                "Serviço Especial",
                "Descrição detalhada do serviço",
                "Cliente Especial",
                "Endereço muito longo da rua com número grande",
                dataServico
        );

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve processar email cliente com data no passado")
    void deveProcessarEmailClienteComDataNoPassado() throws MessagingException {
        LocalDateTime dataPassada = LocalDateTime.of(2023, 1, 1, 10, 0);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailClienteSchedule(
                "cliente@email.com",
                "Cliente Teste",
                "Prestador Teste",
                "Serviço Passado",
                "Descrição do serviço",
                dataPassada,
                true
        );

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve processar email com caracteres especiais nos dados")
    void deveProcessarEmailComCaracteresEspeciaisNosDados() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailAceiteSchedule(
                "prestador@email.com",
                "José & Maria Ltda.",
                1L,
                "Serviço de A&B <Especial>",
                "Descrição com 'aspas' e \"aspas duplas\"",
                "Cliente & Cia.",
                "Rua das Flores, 123 - Apto 45B",
                dataServico
        );

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }
}
