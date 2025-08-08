package br.com.meli.service_schedule.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@DiscriminatorValue("prestador")
public class PrestadorModel extends UsuarioModel {

    @Enumerated(EnumType.STRING)
    private AtividadePrest atividadePrest;

    public enum AtividadePrest {
        eletrecista, encanador, pedreiro, pintor, gesseiro, servicos_gerais
    }

    public AtividadePrest getAtividadePrest() {
        return atividadePrest;
    }

    public void setStatus(AtividadePrest atividadePrest) {
        this.atividadePrest = atividadePrest;
    }
}

