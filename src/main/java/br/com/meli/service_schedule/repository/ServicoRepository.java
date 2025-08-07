package br.com.meli.service_schedule.repository;

import br.com.meli.service_schedule.model.ServicoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<ServicoModel, Long> {

    List<ServicoModel> findByCategoria(String categoria);

    List<ServicoModel> findByPrestadorId(Long prestadorId);
}
