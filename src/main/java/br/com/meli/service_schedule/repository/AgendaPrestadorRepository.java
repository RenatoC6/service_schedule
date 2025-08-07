package br.com.meli.service_schedule.repository;

import br.com.meli.service_schedule.model.AgendaPrestadorModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaPrestadorRepository extends JpaRepository<AgendaPrestadorModel, Long> {

    List<AgendaPrestadorModel> findByPrestadorIdAndStatus(Long prestadorId, AgendaPrestadorModel.StatusAgenda status);

}
