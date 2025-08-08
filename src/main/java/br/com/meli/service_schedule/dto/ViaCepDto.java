package br.com.meli.service_schedule.dto;

public record ViaCepDto(String cep,
                        String logradouro,
                        String complemento,
                        String bairro,
                        String localidade,
                        String uf,
                        String ibge,
                        String gia,
                        String ddd,
                        String siafi,
                        boolean erro) {
}
