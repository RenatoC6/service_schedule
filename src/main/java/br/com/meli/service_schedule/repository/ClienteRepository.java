package br.com.meli.service_schedule.repository;

import br.com.meli.service_schedule.model.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<ClienteModel, Long> {

    ClienteModel findClienteModelById(Long id);

    boolean existsByEmail(String email);
}
