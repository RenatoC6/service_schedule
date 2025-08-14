package br.com.meli.service_schedule.service;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EmailScheduleEvent {
    private final String emailPrestador;
    private final String nomePrestador;
    private  final Long idSchedule;
    private final String nomeServico;
    private final String descricaoServico;
    private final String nomeCliente;
    private final String enderecoCliente;
    private final LocalDateTime dataServico;

    public EmailScheduleEvent(String emailPrestador, String nomePrestador, Long idServico, String nomeServico,
                              String descricaoServico, String nomeCliente, String enderecoCliente, LocalDateTime dataServico) {
        this.emailPrestador = emailPrestador;
        this.nomePrestador = nomePrestador;
        this.idSchedule = idServico;
        this.nomeServico = nomeServico;
        this.descricaoServico = descricaoServico;
        this.nomeCliente = nomeCliente;
        this.enderecoCliente = enderecoCliente;
        this.dataServico = dataServico;

    }

}
