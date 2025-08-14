package br.com.meli.service_schedule.model;

public enum AgendaStatus {

    DISPONIVEL, AGUARDANDO, RESERVADO, CONCLUIDO, CANCELADO;

    public static boolean existeStatus(String statusDto) {
        if (statusDto == null) return false;
        for (AgendaStatus status : values()) {
            if (status.name().equalsIgnoreCase(statusDto)) {
                return true;
            }
        }
        return false;
    }

}
