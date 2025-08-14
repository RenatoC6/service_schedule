package br.com.meli.service_schedule.service;

import lombok.Getter;

@Getter
public class EmailScheduleEvent {
    private final String emailPrestador;
    private final String nomePrestador;
    private  final Long idSchedule;
    private final String nomeServico;
    private final String descricaoServico;

    public EmailScheduleEvent(String emailPrestador, String nomePrestador, Long idServico, String nomeServico, String descricaoServico) {
        this.emailPrestador = emailPrestador;
        this.nomePrestador = nomePrestador;
        this.idSchedule = idServico;
        this.nomeServico = nomeServico;
        this.descricaoServico = descricaoServico;
    }

}
