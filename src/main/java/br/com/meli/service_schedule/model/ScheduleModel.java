package br.com.meli.service_schedule.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "schedule")
public class ScheduleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "servico_id")
    private ServicoModel servicoModel;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteModel clienteModel;

    @ManyToOne
    @JoinColumn(name = "prestador_id")
    private PrestadorModel prestadorModel;

    @ManyToOne
    @JoinColumn(name = "agenda_prestador_id")
    private AgendaPrestadorModel agendaPrestadorModel;

    @Column(name = "data_hora")
    private java.time.LocalDateTime dataHora;

    @Column(name = "criado_em")
    private java.time.LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private java.time.LocalDateTime atualizadoEm;

    @Enumerated(EnumType.STRING)
    private StatusAgendamento status;

    public enum StatusAgendamento {
        pendente, aceito, rejeitado, cancelado, finalizado
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServicoModel getServico() {
        return servicoModel;
    }

    public void setServico(ServicoModel servicoModel) {
        this.servicoModel = servicoModel;
    }

    public ClienteModel getCliente() {
        return clienteModel;
    }

    public void setCliente(ClienteModel clienteModel) {
        this.clienteModel = clienteModel;
    }

    public PrestadorModel getPrestador() {
        return prestadorModel;
    }

    public void setPrestador(PrestadorModel prestadorModel) {
        this.prestadorModel = prestadorModel;
    }

    public AgendaPrestadorModel getAgendaPrestador() {
        return agendaPrestadorModel;
    }

    public void setAgendaPrestador(AgendaPrestadorModel agendaPrestadorModel) {
        this.agendaPrestadorModel = agendaPrestadorModel;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }
}
