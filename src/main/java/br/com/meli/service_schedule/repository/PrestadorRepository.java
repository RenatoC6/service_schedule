package br.com.meli.service_schedule.repository;

import br.com.meli.service_schedule.model.PrestadorModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrestadorRepository extends JpaRepository<PrestadorModel, Long> {

    PrestadorModel findPrestadorById(Long id);

    List<PrestadorModel> findByNome(String nome);

    boolean existsPrestadorById(Long id);


}
