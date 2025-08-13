package br.com.meli.service_schedule.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
    private ScheduleStatus status;

    private  String motivo;

}
