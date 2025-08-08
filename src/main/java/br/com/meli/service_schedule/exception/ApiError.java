package br.com.meli.service_schedule.exception;

public class ApiError {

    final String mensagem;

    public ApiError(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }
}
