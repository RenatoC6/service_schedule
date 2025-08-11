package br.com.meli.service_schedule.model;

public enum Atividades {

    eletrecista, encanador, pedreiro, pintor, gesseiro, servicos_gerais;

    public static boolean existeAtividade(String value) {
        if (value == null) return false;
        for (Atividades atividade : values()) {
            if (atividade.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
