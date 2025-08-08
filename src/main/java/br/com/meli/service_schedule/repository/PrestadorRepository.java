package br.com.meli.service_schedule.repository;

import br.com.meli.service_schedule.model.PrestadorModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestadorRepository extends JpaRepository<PrestadorModel, Long> {

    PrestadorModel findPrestadorModelById(Long id);

    boolean existsByEmail(String email);
}
