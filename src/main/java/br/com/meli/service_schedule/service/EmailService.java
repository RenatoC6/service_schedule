package br.com.meli.service_schedule.service;

import br.com.meli.service_schedule.exception.GenericException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;


    public void enviarEmailAceiteSchedule(String emailPrestador, String nomePrestador, Long idSchedule, String nomeServico, String descricaoServico) {
        String assunto = "Solicitação de novo agendamento: " + nomeServico;
        String urlAceite = "http://localhost:8090/schedule/" + idSchedule + "/aceitar";
        String urlRecusa = "http://localhost:8090/schedule/" + idSchedule + "/recusar";

        String mensagemHtml = String.format(
                "<p>Olá %s, você possui uma nova solicitação de agendamento.</p>" +
                        "<p><strong>Serviço:</strong> %s</p>" +
                        "<p><strong>Descrição:</strong> %s</p>" +
                        "<p>Escolha uma das opções abaixo:</p>" +
                        "<a style='padding:10px 25px; background:#4CAF50; color:#fff; text-decoration:none; border-radius:5px; margin-right:10px;' href='%s'>ACEITAR</a>" +
                        "<a style='padding:10px 25px; background:#F44336; color:#fff; text-decoration:none; border-radius:5px;' href='%s'>RECUSAR</a>",
                nomePrestador, nomeServico, descricaoServico, urlAceite, urlRecusa
        );

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(emailPrestador);
            helper.setSubject(assunto);
            helper.setText(mensagemHtml, true); // true = HTML
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new GenericException(e.getMessage());
        }

    }

    public void enviarEmailClienteSchedule(String emailCliente,
                                           String nomeCliente,
                                           String nomePrestador,
                                           String nomeServico,
                                           String descricaoServico,
                                           LocalDateTime dataAgendamento,
                                           boolean aceito) {

        String assunto = "Solicitação de agendamento " + (aceito ? "ACEITA" : "RECUSADA") + ": " + nomeServico;
        String status = aceito ? "aceita" : "recusada";

        String mensagemHtml = String.format(
                "<p>Olá %s, sua solicitação de agendamento para o serviço %s com o(a) %s foi <b>%s</b>, para a data: <b>%s</b>.</p>" +
                        "<p><strong>Descrição:</strong> %s</p>",
                nomeCliente, nomeServico, nomePrestador, status, dataAgendamento, descricaoServico
        );

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(emailCliente);
            helper.setSubject(assunto);
            helper.setText(mensagemHtml, true); // true = HTML
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new GenericException(e.getMessage());
        }
    }
}

