package br.com.meli.service_schedule.service;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailScheduleListener {

    private final EmailService emailService;

    public EmailScheduleListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @EventListener
    public void handleEmailScheduleEvent(EmailScheduleEvent event) {
        emailService.enviarEmailAceiteSchedule(
                event.getEmailPrestador(),
                event.getNomePrestador(),
                event.getIdSchedule(),
                event.getNomeServico(),
                event.getDescricaoServico(),
                event.getNomeCliente(),
                event.getEnderecoCliente(),
                event.getDataServico()
        );
    }

}
