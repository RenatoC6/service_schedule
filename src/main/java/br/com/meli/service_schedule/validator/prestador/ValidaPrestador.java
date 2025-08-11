package br.com.meli.service_schedule.validator.prestador;

import br.com.meli.service_schedule.exception.EntidadeNaoEncontradaException;
import br.com.meli.service_schedule.repository.PrestadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidaPrestador implements PrestadorValidator {

    @Autowired
    PrestadorRepository prestadorRepository;

    public void validarPrestador(Long prestadorId) {
        if(!prestadorRepository.existsPrestadorById(prestadorId)){
            throw new EntidadeNaoEncontradaException("Prestador não encontrado: " + prestadorId);
        }
    }
}
