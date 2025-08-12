package br.com.meli.service_schedule.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

}
