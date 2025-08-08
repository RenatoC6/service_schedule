package br.com.meli.service_schedule.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("prestador")
public class PrestadorModel extends UsuarioModel {

    @Column(name="atividade")
    @Enumerated(EnumType.STRING)
    private Atividades atividadePrest;

    public Atividades getAtividadePrest() {
        return atividadePrest;
    }

    public void setAtividadePrest(Atividades atividadePrest) {
        this.atividadePrest = atividadePrest;
    }


}

