package br.com.meli.service_schedule.dto;

public record ServicoRequestDto(Long id,
                                String nome,
                                String descricao,
                                Double preco,
                                String categoria,
                                Long prestadorId,
                                String prestadorNome) {
}
