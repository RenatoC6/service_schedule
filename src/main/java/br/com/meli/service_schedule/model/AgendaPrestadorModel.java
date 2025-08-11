package br.com.meli.service_schedule.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agenda_prestador")
public class AgendaPrestadorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prestador_id")
    private PrestadorModel prestadorModel;

    @Column(name = "data_hora_disponivel")
    private java.time.LocalDateTime dataHoraDisponivel;

    @Enumerated(EnumType.STRING)
    private AgendaStatus status;

     public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrestadorModel getPrestador() {
        return prestadorModel;
    }

    public void setPrestador(PrestadorModel prestadorModel) {
        this.prestadorModel = prestadorModel;
    }

    public LocalDateTime getDataHoraDisponivel() {
        return dataHoraDisponivel;
    }

    public void setDataHoraDisponivel(LocalDateTime dataHoraDisponivel) {
        this.dataHoraDisponivel = dataHoraDisponivel;
    }

    public AgendaStatus getStatus() {
        return status;
    }

    public void setStatus(AgendaStatus status) {
        this.status = status;
    }
}
