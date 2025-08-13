package br.com.meli.service_schedule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void enviarEmailAceiteSchedule(String emailPrestador, String nomePrestador, Long idSchedule) {
        String assunto = "Solicitação de novo agendamento";
        String urlAceite = "https:/localhost:8090/schedule/" + idSchedule; // Troque pela URL real do seu endpoint de aceite
        String mensagem = String.format(
                "Olá %s, você possui uma nova solicitação de agendamento. Para aceitar, clique no link abaixo:\n%s",
                nomePrestador, urlAceite);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emailPrestador);
        mailMessage.setSubject(assunto);
        mailMessage.setText(mensagem);
        javaMailSender.send(mailMessage);
    }
}
