package br.com.meli.service_schedule.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agenda_prestador")
public class AgendaPrestador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prestador_id")
    private Prestador prestador;

    @Column(name = "data_hora_disponivel")
    private java.time.LocalDateTime dataHoraDisponivel;

    @Enumerated(EnumType.STRING)
    private StatusAgenda status;

    public enum StatusAgenda {
        disponivel, reservado, aguardando
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prestador getPrestador() {
        return prestador;
    }

    public void setPrestador(Prestador prestador) {
        this.prestador = prestador;
    }

    public LocalDateTime getDataHoraDisponivel() {
        return dataHoraDisponivel;
    }

    public void setDataHoraDisponivel(LocalDateTime dataHoraDisponivel) {
        this.dataHoraDisponivel = dataHoraDisponivel;
    }

    public StatusAgenda getStatus() {
        return status;
    }

    public void setStatus(StatusAgenda status) {
        this.status = status;
    }
}
