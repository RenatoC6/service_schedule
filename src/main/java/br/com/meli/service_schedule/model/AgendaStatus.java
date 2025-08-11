package br.com.meli.service_schedule.model;

public enum AgendaStatus {

    disponivel, Aguardando, reservado;

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
