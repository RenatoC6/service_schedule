package br.com.meli.service_schedule.repository;

import br.com.meli.service_schedule.model.ServicoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<ServicoModel, Long> {

    boolean existsByNome(String nome);


}
